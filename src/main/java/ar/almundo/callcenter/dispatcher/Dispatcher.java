package ar.almundo.callcenter.dispatcher;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.almundo.callcenter.filter.EmployeeAttendFilter;
import ar.almundo.callcenter.model.Call;
import ar.almundo.callcenter.model.Employee;

public class Dispatcher implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(Dispatcher.class);

	private Boolean active;

	private ExecutorService executorService;
	
	private CallGenerator callGenerator;

	private ConcurrentLinkedDeque<Employee> employees;

	public static ConcurrentLinkedDeque<Call> incomingCalls = new ConcurrentLinkedDeque<Call>();

	private EmployeeAttendFilter employeeAttendFilter;

	public Dispatcher(List<Employee> employees, Boolean callGenerateState) {
		this(employees, new EmployeeAttendFilter(), callGenerateState);
	}

	public Dispatcher(List<Employee> employees, EmployeeAttendFilter employeeAttendFilter, Boolean callGenerateState) {
		Validate.notNull(employees);
		Validate.notNull(employeeAttendFilter);
		Validate.notNull(callGenerateState);

		this.employees = new ConcurrentLinkedDeque<Employee>(employees);
		this.employeeAttendFilter = employeeAttendFilter;
		this.callGenerator = new CallGenerator(callGenerateState);
		this.executorService = Executors.newFixedThreadPool(employees.size() + 1);
	}

	public static synchronized void dispatchCall(Call call) {
		logger.info("Dispatch new call. Identifier " + call.getFormatIdentifier());
		incomingCalls.add(call);
	}

	/**
	 * Starts the call generator and employee threads which allows the dispatcher run method to
	 * execute
	 */
	public synchronized void start() {
		this.active = true;
		this.executorService.execute(this.callGenerator);

		for (Employee employee : this.employees) {
			this.executorService.execute(employee);
		}
	}

	/**
	 * Stops the call generator and employee threads also the dispatcher run method immediately
	 */
	public synchronized void stop() {
		this.active = false;
		this.executorService.shutdown();
	}

	public synchronized Boolean getActive() {
		return active;
	}

	/**
	 * 	If the incoming calls are not empty then an available employee is searched to answer the first call.
	 *  Calls will be queued until some workers are available.
	 */
	public void run() {
		while (getActive()) {
			if (incomingCalls.isEmpty()) {
				continue;
			} else {
				Employee employee = this.employeeAttendFilter.findEmployee(this.employees);
				if (employee == null) {
					continue;
				}
				try {
					Call call = incomingCalls.pollFirst();
					employee.answer(call);
				} catch (Exception e) {
					logger.error(e.toString());
				}
			}
		}
	}

}
