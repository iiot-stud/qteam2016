package org.qTeam.core.federationManager.dm;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import org.qTeam.api.core.IConfig;
import org.qTeam.api.core.IMessageBus;
import org.qTeam.core.federationManager.Datacenter;
import org.qTeam.core.federationManager.FederationManager;
import org.qTeam.core.federationManager.PolicyEngine;
import org.qTeam.core.federationManager.Response;

import com.hp.hpl.jena.vocabulary.RDFS;

import info.openmultinet.ontology.vocabulary.Omn;

@Path("/")
public class FederationManagerREST{

	private PolicyEngine engine;
	private static Logger LOGGER = Logger.getLogger(FederationManagerREST.class.toString());

      /*
       * First Rest Api for all Requests in version 1
       */
	  @POST
	  @Path("/request/v1")
	  @Consumes(MediaType.APPLICATION_JSON)
	  public javax.ws.rs.core.Response request(String jsonRequest) {
		  engine = new PolicyEngine();
		  // Taking the Json-Body and handling it to the PolicyEngine 
		  Response response = engine.handleRequest(jsonRequest);
		  String responseString = response.parseToJsonString();
		  return javax.ws.rs.core.Response.status(200).entity(responseString+"\n").header("Access-Control-Allow-Origin", "*").build();
//		  return javax.ws.rs.core.Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();	  
		  
	  }
	  
      /*
       * Second Rest Api for all Requests in version 2
       */
	  @POST
	  @Path("/request/v2")
	  @Consumes(MediaType.APPLICATION_JSON)
	  public javax.ws.rs.core.Response request2(String jsonRequest) {
		  engine = new PolicyEngine();
		  // Taking the Json-Body and handling it to the PolicyEngine 
		  try{
			  Response response = engine.handleRequest(jsonRequest);
			  String responseString = response.parseToJsonString();
			  return javax.ws.rs.core.Response.status(200).entity(responseString+"\n").header("Access-Control-Allow-Origin", "*").build();
		  }catch(Exception e){
			  Response response = new Response();
			  response.setName("Q-Team");
			  response.setSuccess(true);
			  response.setVendor("OpenPolicy");
			  response.setVersion("0.1");
			  ArrayList<HashMap<String,String>> slotList = new  ArrayList<HashMap<String,String>>();
			  HashMap<String,String> slotMap = new HashMap<String,String>();
			  
			  slotMap.put("country", "Germany");
			  slotMap.put("continent", "EU");
			  slotMap.put("name", "Strato");
			  slotMap.put("location", "Berlin");
			  slotMap.put("price", "2");
			  slotMap.put("latitude", "52.473412");
			  slotMap.put("longitude", "13.390961");
			  slotList.add(slotMap);

			  slotMap = new HashMap<String,String>();
			  slotMap.put("country", "China");
			  slotMap.put("continent", "Asia");
			  slotMap.put("name", "Ching-chong");
			  slotMap.put("location", "Peking");
			  slotMap.put("price", "0,5");
			  slotMap.put("latitude", "39.915802");
			  slotMap.put("longitude", "116.396919");
			  slotList.add(slotMap);

			  response.setSlotList(slotList);
//			  ArrayList<Datacenter> locations = new ArrayList<Datacenter>();
//			  Datacenter datacenter = new Datacenter();
//			  datacenter.setContinent("EU");
//			  datacenter.setCountry("Germany");
//			  datacenter.setLocation("Berlin");
//			  datacenter.setName("Strato");
//			  datacenter.setPrice("2");
//			  locations.add(datacenter);
//			  
//			  datacenter = new Datacenter();
//			  datacenter.setContinent("Asia");
//			  datacenter.setCountry("China");
//			  datacenter.setLocation("Peking");
//			  datacenter.setName("Ching-chong");
//			  datacenter.setPrice("0,5");
//			  locations.add(datacenter);

			  
			  String responseString = response.parseToJsonString();
			  LOGGER.log(Level.SEVERE, responseString);
			  return javax.ws.rs.core.Response.status(200).entity(responseString+"\n").header("Access-Control-Allow-Origin", "*").build();

		  }
//		  return javax.ws.rs.core.Response.ok(responseString).header("Access-Control-Allow-Origin", "*").build();	  
		  
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
