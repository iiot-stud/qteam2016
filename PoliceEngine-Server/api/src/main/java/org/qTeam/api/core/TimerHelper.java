package org.qTeam.api.core;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;

@Startup
@ApplicationScoped
public class TimerHelper {
	private static final Logger LOGGER = Logger
			.getLogger(TimerHelper.class.getName());

	private Callable<Void> task;
	private int failureCounter = 0;

	public TimerHelper() {
	}

@PostConstruct
public void setup(){
	timerService = sessionContext.getTimerService();
	
	timerService = sessionContext.getTimerService();
}
	@Resource
	private ManagedExecutorService executorService;
	@Resource
    SessionContext sessionContext;
	private TimerService timerService;

	public void setNewTimer(Callable<Void> task) {
		this.task = task;
    	TimerConfig config = new TimerConfig();
		config.setPersistent(false);
		timerService.createIntervalTimer(0, 5000, config);
	}

	@Timeout
	public void timerMethod(Timer timer) {
		if (failureCounter < 10) {

			try {
				Future<Void> future = executorService.submit(task);
				future.get();
				LOGGER.log(Level.INFO, "Added something to RDF-Database");
				failureCounter = 0;
				timer.cancel();
			} catch (Exception e) {
				failureCounter++;
				LOGGER.log(Level.WARNING,
						"Errored while adding something to Database - will try again "
								+ (10 - failureCounter) + " times");
			}

		} else {
			timer.cancel();
			LOGGER.log(
					Level.SEVERE,
					"Tried to add something to Database several times, but failed. Please check the OpenRDF-Database");

		}

	}

}
