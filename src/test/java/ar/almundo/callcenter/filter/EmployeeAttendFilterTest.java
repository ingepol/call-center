package ar.almundo.callcenter.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ar.almundo.callcenter.enumerator.EmployeeState;
import ar.almundo.callcenter.enumerator.EmployeeType;
import ar.almundo.callcenter.model.Employee;

public class EmployeeAttendFilterTest {
	private EmployeeAttendFilter employeeAttendFilter;

	public EmployeeAttendFilterTest() {
        this.employeeAttendFilter = new EmployeeAttendFilter();
    }

	@Test
	public void testAssignToOperator() {
		Employee operator = Employee.buildOperator("operator");
		Employee supervisor = Employee.buildSupervisor("supervisor");
		Employee director = Employee.buildDirector("director");
		List<Employee> employeeList = Arrays.asList(operator, supervisor, director);

		Employee employee = this.employeeAttendFilter.findEmployee(employeeList);

		assertNotNull(employee);
		assertEquals(EmployeeType.OPERATOR, employee.getEmployeeType());
	}

	@Test
	public void testAssignToSupervisor() {
		Employee operator = mock(Employee.class);
		when(operator.getEmployeeState()).thenReturn(EmployeeState.BUSY);
		Employee supervisor = Employee.buildSupervisor("supervisor");
		Employee director = Employee.buildDirector("direcetor");
		List<Employee> employeeList = Arrays.asList(operator, supervisor, director);

		Employee employee = this.employeeAttendFilter.findEmployee(employeeList);

		assertNotNull(employee);
		assertEquals(EmployeeType.SUPERVISOR, employee.getEmployeeType());
	}

	@Test
	public void testAssignToDirector() {
		Employee operator = mockBusyEmployee(EmployeeType.OPERATOR);
		Employee supervisor = mockBusyEmployee(EmployeeType.SUPERVISOR);
		Employee director = Employee.buildDirector("director");
		List<Employee> employeeList = Arrays.asList(operator, supervisor, director);

		Employee employee = this.employeeAttendFilter.findEmployee(employeeList);

		assertNotNull(employee);
		assertEquals(EmployeeType.DIRECTOR, employee.getEmployeeType());
	}

	@Test
	public void testAssignToNone() {
		Employee operator = mockBusyEmployee(EmployeeType.OPERATOR);
		Employee supervisor = mockBusyEmployee(EmployeeType.SUPERVISOR);
		Employee director = mockBusyEmployee(EmployeeType.DIRECTOR);
		List<Employee> employeeList = Arrays.asList(operator, supervisor, director);

		Employee employee = this.employeeAttendFilter.findEmployee(employeeList);

		assertNull(employee);
	}

	private static Employee mockBusyEmployee(EmployeeType employeeType) {
		Employee employee = mock(Employee.class);
		when(employee.getEmployeeType()).thenReturn(employeeType);
		when(employee.getEmployeeState()).thenReturn(EmployeeState.BUSY);
		return employee;
	}
}
