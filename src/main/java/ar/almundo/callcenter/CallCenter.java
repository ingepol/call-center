package ar.almundo.callcenter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.almundo.callcenter.dispatcher.Dispatcher;
import ar.almundo.callcenter.model.Employee;

public class CallCenter {
	private static final Logger logger = LoggerFactory.getLogger(CallCenter.class);
	
	public static void main(String[] args) {
		
		List<Employee> employees = new ArrayList<Employee>();
		
		employees.add(Employee.buildDirector("Anna"));
		employees.add(Employee.buildDirector("Juan"));
		employees.add(Employee.buildDirector("Karina"));
		employees.add(Employee.buildDirector("Camila"));

		employees.add(Employee.buildSupervisor("Maria"));
		employees.add(Employee.buildSupervisor("Pedro"));


		employees.add(Employee.buildOperator("Daniel"));
		employees.add(Employee.buildOperator("Laura"));
		employees.add(Employee.buildOperator("Cristian"));
		
		Dispatcher dispatcher = new Dispatcher(employees, true);
        dispatcher.start();
        
        try {
			TimeUnit.SECONDS.sleep(1);
			ExecutorService executorService = Executors.newSingleThreadExecutor();
			executorService.execute(dispatcher);
			TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ex) {
        	logger.error("The execution was interrupted " + ex.getMessage());
		}
        
       
	}
}
