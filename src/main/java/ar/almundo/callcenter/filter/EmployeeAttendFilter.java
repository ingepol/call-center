package ar.almundo.callcenter.filter;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.almundo.callcenter.enumerator.EmployeeState;
import ar.almundo.callcenter.model.Employee;

public class EmployeeAttendFilter {
	
	private static final Logger logger = LoggerFactory.getLogger(EmployeeAttendFilter.class);

	/**
	 * Finds next available employee
	 * 
	 * The order of attention is given by the availability of employees like
	 * this: first the operators attend, second the supervisors and finally the
	 * directors.
	 * 
	 * @param employeeList
	 *            List of working employees
	 * @return first employee available to take a call or null if all employees
	 *         are busy.
	 */
	public Employee findEmployee(Collection<Employee> employeeList) {

		Validate.notNull(employeeList);

		Comparator<Employee> byLevelEmployee = (Employee e1, Employee e2) -> Integer
				.compare(e2.getEmployeeType().getLevel(), e1.getEmployeeType().getLevel());

		Comparator<Employee> byNameEmployee = (Employee e1, Employee e2) -> e1.getEmployeeName()
				.compareTo(e2.getEmployeeName());

		Comparator<Employee> comparator = byLevelEmployee.thenComparing(byNameEmployee);

		Optional<Employee> employee = employeeList.stream().filter(e -> e.getEmployeeState() == EmployeeState.AVAILABLE)
				.sorted(comparator).findFirst();

		if (!employee.isPresent()) {
			logger.debug("At this time our operators are not available.");
			return null;
		}

		return employee.get();
	}

}