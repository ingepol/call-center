package ar.almundo.callcenter.model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.almundo.callcenter.dispatcher.Dispatcher;
import ar.almundo.callcenter.enumerator.EmployeeState;
import ar.almundo.callcenter.enumerator.EmployeeType;

/**
 * Models the Employee Domain Objects
 */
public class Employee implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(Employee.class);

	public String name;

	private EmployeeType employeeType;

	private EmployeeState employeeState;
	
	private Call call;
	
	private ConcurrentLinkedDeque<Call> attendedCalls;

	public Employee(String name, EmployeeType employeeType) {
		Validate.notNull(employeeType);
		Validate.notNull(name);
		this.name = name;
		this.employeeType = employeeType;
		this.employeeState = EmployeeState.AVAILABLE;
		attendedCalls = new ConcurrentLinkedDeque<Call>();
	}

	public String getEmployeeName() {
		return name;
	}

	public EmployeeType getEmployeeType() {
		return employeeType;
	}

	public synchronized EmployeeState getEmployeeState() {
		return employeeState;
	}

	private synchronized void setEmployeeState(EmployeeState employeeState) {
		logger.debug(
				getEmployeeType().toLowerCase() + " " + getEmployeeName() + " changes its state to " + employeeState);
		this.employeeState = employeeState;
	}
	
	public synchronized List<Call> getAttendedCalls() {
        return new ArrayList<>(attendedCalls);
    }

	/**
	 * Assign busy status to the employee who will answer the call.
	 *
	 */
	public synchronized void answer(Call call) {
		try {
			this.call = call;			
			this.setEmployeeState(EmployeeState.BUSY);
			logger.info(getEmployeeType().toLowerCase() + " " + getEmployeeName() + " queues the call "
					+ call.getFormatIdentifier());
			logger.info(Dispatcher.incomingCalls.size() + " calls on hold");
		} catch (NoSuchElementException nee) {
			logger.debug("No calls on hold");
		}
	}

	public static Employee buildOperator(String name) {
		return new Employee(name, EmployeeType.OPERATOR);
	}

	public static Employee buildSupervisor(String name) {
		return new Employee(name, EmployeeType.SUPERVISOR);
	}

	public static Employee buildDirector(String name) {
		return new Employee(name, EmployeeType.DIRECTOR);
	}

	/**
	* If the queue of incoming calls it is not empty, then it changes its status from AVAILABLE to OCCUPIED, 
	* and takes the call and when finished change your status from BUSY to
	* AVAILABLE. This allows a group of threads to decide to send another call
	* to the employee.
	*/
	public void run() {
		logger.info(getEmployeeType() + " " + getEmployeeName() + " starts to work");
		while (true) {
			if (getEmployeeState() == EmployeeState.BUSY) {
				logger.info(getEmployeeType().toLowerCase() + " " + getEmployeeName() + " begins to answer the call "
						+ this.call.getFormatIdentifier());
				try {
					TimeUnit.SECONDS.sleep(this.call.getDurationInSeconds());
				} catch (InterruptedException ie) {
					Dispatcher.incomingCalls.addFirst(this.call);
					logger.debug("There was an error in the call, it will be added to the pending list to be attended "
							+ "by the next available operator.");
					logger.error(getEmployeeType().toLowerCase() + " " + getEmployeeName()
							+ " was interrupted and could not finish the call " + this.call.getFormatIdentifier());
				} finally {
					this.setEmployeeState(EmployeeState.AVAILABLE);
					logger.info(getEmployeeType().toLowerCase() + " " + getEmployeeName() + " finishes the call "
							+ this.call.getFormatIdentifier() + " with a duration of " + this.call.getDurationInSeconds()
							+ " seconds");
				}
				this.attendedCalls.add(call);
			}
		}
	}

}