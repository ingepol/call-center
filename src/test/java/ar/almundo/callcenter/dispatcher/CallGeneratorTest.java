package ar.almundo.callcenter.dispatcher;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class CallGeneratorTest {

	@Test(expected = NullPointerException.class)
	public void testCallGeneratorCreationWithNullParameter() {
		new CallGenerator(null);
	}

	@Test
	public void testCallGeneratorNoActivate() throws InterruptedException {
		CallGenerator callGenerator = new CallGenerator(false);
		ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(callGenerator);
        TimeUnit.SECONDS.sleep(1);
        executorService.awaitTermination(2, TimeUnit.SECONDS);
        assertNotNull(callGenerator);
		assertEquals(callGenerator.getActive(), false);
        assertEquals(0,Dispatcher.incomingCalls.size());        
	}

	@Test
	public void testCallGeneratorActivate() throws InterruptedException {
		Integer min = 10;
		Integer max = 16;
		CallGenerator callGenerator = new CallGenerator(true);
		ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(callGenerator);
        TimeUnit.SECONDS.sleep(1);
        executorService.awaitTermination(2, TimeUnit.SECONDS);
        assertNotNull(callGenerator);
		assertEquals(callGenerator.getActive(), true);
		assertTrue(min <= Dispatcher.incomingCalls.size());
		assertTrue(Dispatcher.incomingCalls.size() <= max);
		callGenerator.stop();
	}

}
