package org.qTeam.core.federationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hornetq.utils.json.JSONArray;
import org.hornetq.utils.json.JSONException;
import org.hornetq.utils.json.JSONObject;

import com.google.gson.JsonObject;
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
	
	public Response(String name, String vendor, String version, boolean success, Model slotModel){
		this.name = name;
		this.vendor = vendor;
		this.version = version;
		this.success = success;
		this.slotModel = slotModel;
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
				for(Resource r : slotModel.listSubjects().toList()){
					JSONObject slotObject = new JSONObject();
					slotObject.put("name", r.getLocalName());
					
					Model resourceModel = r.getModel();
					Property hostedInProperty = resourceModel.getProperty("http://www.q-team.org/Ontology#hostedInDataCenter");
					RDFNode node= resourceModel.listObjectsOfProperty(hostedInProperty).next();
					Model datacenter = ModelFactory.createDefaultModel().getResource(node.toString()).getModel();
					Property locatedInProp = datacenter.getProperty("http://www.q-team.org/Ontology#locatedIn");
					slotObject.put("country", datacenter.listObjectsOfProperty(locatedInProp).next().asResource().getLocalName());
					
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
