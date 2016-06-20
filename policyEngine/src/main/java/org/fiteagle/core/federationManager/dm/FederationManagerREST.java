package org.fiteagle.core.federationManager.dm;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import javax.inject.Inject;
import javax.jms.JMSException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.fiteagle.api.core.IConfig;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.core.federationManager.FederationManager;
import org.fiteagle.core.federationManager.PolicyEngine;
import org.fiteagle.core.federationManager.Response;

import com.hp.hpl.jena.vocabulary.RDFS;

import info.openmultinet.ontology.vocabulary.Omn;

@Path("/")
public class FederationManagerREST{

	private PolicyEngine engine;

      /*
       * First Rest Api for all Requests in version 1
       */
	  @POST
	  @Path("/request/v1")
	  @Consumes(MediaType.APPLICATION_JSON)
	  public String request(String jsonRequest) {
		  // Taking the Json-Body and handling it to the PolicyEngine 
		  Response response = engine.handleRequest(jsonRequest);
		  String responseString = response.parseToJsonString();
		  
	    	return  responseString;
	  }
	  
	  /*
	   * Just a REST Api to check if Server is live and working
	   */
	  @GET
	  @Path("/available")
	  public String isServerAvailable() {
	    	return  "Hello World";
	  }
	  
}
