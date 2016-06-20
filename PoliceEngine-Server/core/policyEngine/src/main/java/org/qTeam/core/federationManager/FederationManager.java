package org.qTeam.core.federationManager;

import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.vocabulary.Omn_federation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.apache.jena.atlas.web.HttpException;
import org.qTeam.api.core.IMessageBus;
import org.qTeam.api.core.OntologyModelUtil;
import org.qTeam.api.tripletStoreAccessor.TripletStoreAccessor;
import org.qTeam.api.tripletStoreAccessor.TripletStoreAccessor.ResourceRepositoryException;
import org.qTeam.core.federationManager.dm.FederationManagerREST;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.vocabulary.RDF;

@Startup
@Singleton
@ApplicationPath("/")
public class FederationManager extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> s = new HashSet<Class<?>>();
		s.add(FederationManagerREST.class);
		return s;
	}

	private static final Logger LOGGER = Logger
			.getLogger(FederationManager.class.getName());
	protected Model federationModel;
	protected static FederationManager manager;
	private int failureCounter = 0;

	@javax.annotation.Resource
	public TimerService timerService;
	
    private Boolean rdfReady;
    private Boolean allreadySearchingForTripletStore;
    private InitialContext initialContext;

	public boolean initialized;

	@javax.annotation.PostConstruct
	public void setup() {
		manager = this;
		initialized = false;
		failureCounter =0;

		// TODO Make it not so confusing (Maybe Switch-Case)
		File federationOntologie = Paths.get(System.getProperty("user.home"))
				.resolve(".q-team").resolve("Q-Team.ttl").toFile();
		if (federationOntologie.exists()) {
			if (federationModel == null) {
				federationModel = OntologyModelUtil.loadModel(
						federationOntologie.toString(),
						IMessageBus.SERIALIZATION_TURTLE);

				if (federationModel.isEmpty()) {
					federationModel = OntologyModelUtil.loadModel(
							"ontologies/defaultFederation.ttl",
							IMessageBus.SERIALIZATION_TURTLE);
					LOGGER.log(
							Level.SEVERE,
							"Please add your Federation-Ontology to the '/home/User/.fiteagle/Federation.ttl' File and Re-Deploy the FederationManager ");
				}
			}

		} else {
			try {
				federationOntologie.createNewFile();
			} catch (IOException e) {
				LOGGER.log(
						Level.SEVERE,
						"Couldn't load Federation-Ontology. Tried to create new file '/home/User/.fiteagle/Federation.ttl' but Errored");
			}
			federationModel = OntologyModelUtil.loadModel(
					"ontologies/defaultFederation.ttl",
					IMessageBus.SERIALIZATION_TURTLE);
			LOGGER.log(
					Level.SEVERE,
					"Please add your Federation-Ontology to the '/home/User/.fiteagle/Federation.ttl' File and Re-Deploy the FederationManager ");
		}
		runSetup();

	}

	public void runSetup() {
		
		try {
			initialContext = new InitialContext();
			rdfReady = (Boolean) initialContext.lookup("java:global/RDF-Database-Ready");
	     	allreadySearchingForTripletStore = (Boolean) initialContext.lookup("java:global/RDF-Database-Testing");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!initialized) {
	    	TimerConfig config = new TimerConfig();
			config.setPersistent(false);
			timerService.createIntervalTimer(0, 5000, config);
		}
	}

	public static FederationManager getManager() {
		return manager;
	}
	


	@Timeout
	public void timerMethod(Timer timer) {
       	LOGGER.log(Level.SEVERE,"Will try to find Database now");

                	// setting boolean so others see someone is allready searching for Database
                 	try {
              		  allreadySearchingForTripletStore = true;
              		  initialContext.rebind("java:global/RDF-Database-Testing", allreadySearchingForTripletStore);
					} catch (NamingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
                 	
                 	//try to find Database
            		try {
            			TripletStoreAccessor.addModel(federationModel);
            	    	initialized = true;
                    	Iterator<Timer> timerIterator = timerService.getAllTimers().iterator();
                    	
                    	rdfReady = true;
                   	  	allreadySearchingForTripletStore = false;
                        while(timerIterator.hasNext()){
                        	timerIterator.next().cancel();
                        }
            			
                        setGlobalVariables();
                        LOGGER.log(Level.SEVERE,"Found Database");
                     	LOGGER.log(Level.SEVERE,"End");
                     	
                     	
            		} catch (ResourceRepositoryException e) {
            			// TODO Auto-generated catch block
            			e.printStackTrace();
            		} catch (InvalidModelException e) {
            			// TODO Auto-generated catch block
            			e.printStackTrace();
            		}                	
	}
	
    public void refreshGlobalVariables(){
    	try {
			rdfReady = (Boolean) initialContext.lookup("java:global/RDF-Database-Ready");
	     	allreadySearchingForTripletStore = (Boolean) initialContext.lookup("java:global/RDF-Database-Testing");
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void setGlobalVariables(){
    	try {
			initialContext.rebind("java:global/RDF-Database-Ready", rdfReady);
	     	initialContext.rebind("java:global/RDF-Database-Testing", allreadySearchingForTripletStore);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}