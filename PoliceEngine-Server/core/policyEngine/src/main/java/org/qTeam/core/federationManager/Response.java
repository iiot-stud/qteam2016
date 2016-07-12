package org.qTeam.core.federationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hornetq.utils.json.JSONArray;
import org.hornetq.utils.json.JSONException;
import org.hornetq.utils.json.JSONObject;
import org.qTeam.api.core.IMessageBus;
import org.qTeam.api.core.MessageUtil;
import org.qTeam.api.tripletStoreAccessor.TripletStoreAccessor;

import com.google.gson.JsonObject;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;

public class Response {
	private String name;
	private String vendor;
	private String version;
	private boolean success;
	private Model slotModel;
	private List<HashMap<String,String>> slotList;
	private Model datacenterModel;
	
	private static Logger LOGGER = Logger.getLogger(Response.class.toString());

	
	public Response(String name, String vendor, String version, boolean success, Model slotModel, Model datacenterModel){
		this.name = name;
		this.vendor = vendor;
		this.version = version;
		this.success = success;
		this.slotModel = slotModel;
		this.datacenterModel = datacenterModel;
	}
	
	public Response(){
	}
	
	public String parseToJsonString(){
		
		JSONObject mainObject = new JSONObject();
		try {
			mainObject.put("name", name);
			mainObject.put("vendor", vendor);
			mainObject.put("version", version);
			
			// Check if we could find a Country for the Request
			if(!success){
				mainObject.put("success", success);
				mainObject.put("locations", "");
				return mainObject.toString();
			}
			
			// If we found something we will continue with the Locations
			// TODO Implement Locations-Array
			JSONArray jsonArray = new JSONArray();
			if(slotList == null){
				Property typeProperty = slotModel.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
				Node slotNode = slotModel.getResource("http://www.q-team.org/Ontology#Slot").asNode();

				for(Resource r : slotModel.listResourcesWithProperty(typeProperty, slotNode).toList()){
					JSONObject slotObject = new JSONObject();
					slotObject.put("name", r.getLocalName());
					
//					Model resourceModel = slotModel.createResource(r.getURI()).getModel();
					Model resourceModel = TripletStoreAccessor.getResource(r.getURI());
					LOGGER.log(Level.SEVERE, MessageUtil.serializeModel(resourceModel, IMessageBus.SERIALIZATION_TURTLE));

					Property hostedInProperty = resourceModel.getProperty("http://www.q-team.org/Ontology#hostedInDataCenter");
					RDFNode node= resourceModel.listObjectsOfProperty(hostedInProperty).next();
//					Resource datacenterResource = datacenterModel.getResource(node.toString());
//					Resource datacenterResource = ModelFactory.createDefaultModel().getResource(node.toString());
					Model datacenter = TripletStoreAccessor.getResource(node.toString());
					Property locatedInProp = datacenter.getProperty("http://www.q-team.org/Ontology#locatedIn");
					String country = datacenter.listObjectsOfProperty(locatedInProp).next().asResource().getLocalName();
					slotObject.put("country", country);
					
					Property latitudeProp = datacenter.getProperty("http://www.q-team.org/Ontology#latitude");
					slotObject.put("latitude", datacenter.listObjectsOfProperty(latitudeProp).next().asLiteral().getValue().toString());
					Property longitudeProp = datacenter.getProperty("http://www.q-team.org/Ontology#longitude");
					slotObject.put("longitude", datacenter.listObjectsOfProperty(longitudeProp).next().asLiteral().getValue().toString());

					slotObject.put("price", ThreadLocalRandom.current().nextInt(1, 10 + 1));
					switch (country){
					case "Germany":
						slotObject.put("continent", "Europe");
						break;
					case "France":
						slotObject.put("continent", "Europe");
						break;
					case "England":
						slotObject.put("continent", "Europe");
						break;
					case "China":
						slotObject.put("continent", "Asia");
						break;
					case "USA":
						slotObject.put("continent", "Mericaa");
						break;
					default:
						slotObject.put("continent", "Europe");
						break;

					}
						
					
					
					
					jsonArray.put(slotObject);
				}
			}else{
				Iterator<HashMap<String, String>> it = slotList.iterator();
				while(it.hasNext()){
					HashMap<String,String> map = it.next();
					Set<String> set = map.keySet();
					JSONObject slotObject = new JSONObject();
					for(String s : set){
						slotObject.put(s,map.get(s));
					}
					jsonArray.put(slotObject);
				}
			}

			mainObject.put("locations", jsonArray);
			
			return mainObject.toString();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Model getSlotModel() {
		return slotModel;
	}

	public void setSlotModel(Model slotModel) {
		this.slotModel = slotModel;
	}

	public List<HashMap<String, String>> getSlotList() {
		return slotList;
	}

	public void setSlotList(List<HashMap<String, String>> slotList) {
		this.slotList = slotList;
	}


}
