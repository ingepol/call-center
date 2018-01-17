package ar.almundo.callcenter.dispatcher;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ar.almundo.callcenter.model.Call;

public class CallGenerator implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(CallGenerator.class);
	private static final int CALL_MIN_AMOUNT = 10;
	
	private static final int CALL_MAX_AMOUNT = 16;

    private static final int MIN_CALL_DURATION = 5;

    private static final int MAX_CALL_DURATION = 10;
    
    private Boolean active;
    
	public CallGenerator(Boolean callGenerateState) {
		Validate.notNull(callGenerateState);
		this.active = callGenerateState;
	}
	
	public synchronized Boolean getActive() {
		return active;
	}
	
	/**
	 * Stops the call generator and employee threads also the dispatcher run method immediately
	 */
	public synchronized void stop() {
		this.active = false;
	}
	
	/**
	* If the status of the call generator is active and the list of incoming calls is empty then an amount between 10 and 16 calls will be generated. 
	* The duration of a call can take between 5 and 10 seconds. These values can change, modifying the constant variables  of the class.
	 */
	@Override
	public void run() {
		if(getActive()){
			logger.info("Call generator star.");
		}
		while (getActive()) {
			
			if(Dispatcher.incomingCalls.isEmpty()){
				int totalCallGenerator = ThreadLocalRandom.current().nextInt(CALL_MIN_AMOUNT, CALL_MAX_AMOUNT);
				List<Call> calls = Call.buildListOfRandomCalls(totalCallGenerator, MIN_CALL_DURATION, MAX_CALL_DURATION);
				logger.info("New calls were generated and will be dispatched by the call center.");
				calls.forEach(call -> {
					Dispatcher.dispatchCall(call);
				});
			}
		}
		
	}

}
