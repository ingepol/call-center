package ar.almundo.callcenter.dispatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import ar.almundo.callcenter.dispatcher.Dispatcher;
import ar.almundo.callcenter.model.Call;
import ar.almundo.callcenter.model.Employee;

public class DispatcherTest {

	private static final int CALL_AMOUNT = 10;

	private static final int MIN_CALL_DURATION = 5;

	private static final int MAX_CALL_DURATION = 10;

	@Before
	public void executedBeforeEach() {
		Dispatcher.incomingCalls.clear();
	}

	@Test(expected = NullPointerException.class)
	public void testDispatcherCreationWithNullEmployees() {
		new Dispatcher(null, false);
	}

	@Test(expected = NullPointerException.class)
	public void testDispatcherCreationWithNullStrategy() {
		new Dispatcher(new ArrayList<>(), null, false);
	}

	@Test(expected = NullPointerException.class)
	public void testDispatcherCreationWithNullCallGenerate() {
		new Dispatcher(new ArrayList<>(), null);
	}

	@Test
	public void testDispatchCallsToEmployees() throws InterruptedException {
		List<Employee> employeeList = buildEmployeeList();
		Dispatcher dispatcher = new Dispatcher(employeeList, false);
		dispatcher.start();
		TimeUnit.SECONDS.sleep(1);
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(dispatcher);
		TimeUnit.SECONDS.sleep(1);

		buildCallList().stream().forEach(call -> {
			Dispatcher.dispatchCall(call);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				fail();
			}
		});

		executorService.awaitTermination(MAX_CALL_DURATION * 2, TimeUnit.SECONDS);
		assertEquals(CALL_AMOUNT, employeeList.stream().mapToInt(employee -> employee.getAttendedCalls().size()).sum());
	}

	private static List<Employee> buildEmployeeList() {
		Employee operator1 = Employee.buildOperator("operator1");
		Employee operator2 = Employee.buildOperator("operator2");
		Employee operator3 = Employee.buildOperator("operator3");
		Employee supervisor1 = Employee.buildSupervisor("supervisor1");
		Employee supervisor2 = Employee.buildSupervisor("supervisor2");
		Employee director = Employee.buildDirector("director");
		return Arrays.asList(operator1, operator2, operator3, supervisor1, supervisor2, director);
	}

	private static List<Call> buildCallList() {
		return Call.buildListOfRandomCalls(CALL_AMOUNT, MIN_CALL_DURATION, MAX_CALL_DURATION);
	}

}