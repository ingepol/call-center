package ar.almundo.callcenter.model;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import ar.almundo.callcenter.enumerator.EmployeeState;
import ar.almundo.callcenter.enumerator.EmployeeType;

public class EmployeeTest {

    @Test(expected = NullPointerException.class)
    public void testEmployeeWithInvalidFirstParameter() {
        new Employee(null, EmployeeType.OPERATOR);
    }
    
    @Test(expected = NullPointerException.class)
    public void testEmployeeWithInvalidSecondParameter() {
        new Employee("operator", null);
    }

    @Test
    public void testEmployeeCreation() {
        Employee employee = Employee.buildOperator("operator");

        assertNotNull(employee);
        assertEquals(EmployeeType.OPERATOR, employee.getEmployeeType());
        assertEquals(EmployeeState.AVAILABLE, employee.getEmployeeState());
    }

    @Test
    public void testEmployeeAttendWhileAvailable() throws InterruptedException {
        Employee employee = Employee.buildOperator("operator");
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(employee);
        employee.answer(Call.buildRandomCall(0, 1));

        executorService.awaitTermination(5, TimeUnit.SECONDS);
        assertEquals(1, employee.getAttendedCalls().size());
    }

    @Test
    public void testEmployeeStatesWhileAnswer() throws InterruptedException {
        Employee employee = Employee.buildOperator("operator");
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(employee);
        assertEquals(EmployeeState.AVAILABLE, employee.getEmployeeState());
        TimeUnit.SECONDS.sleep(1);
        employee.answer(Call.buildRandomCall(2, 3));
        TimeUnit.SECONDS.sleep(4);
        employee.answer(Call.buildRandomCall(0, 1));
        
        assertEquals(EmployeeState.BUSY, employee.getEmployeeState());

        executorService.awaitTermination(6, TimeUnit.SECONDS);
        assertEquals(2, employee.getAttendedCalls().size());
    }

}