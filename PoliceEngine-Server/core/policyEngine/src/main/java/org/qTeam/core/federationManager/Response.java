package org.qTeam.core.federationManager;

import java.util.ArrayList;
import java.util.List;

import org.hornetq.utils.json.JSONArray;
import org.hornetq.utils.json.JSONException;
import org.hornetq.utils.json.JSONObject;

public class Response {
	private String name;
	private String vendor;
	private String version;
	private boolean success;
	private List<Datacenter> locations;
	
	public Response(String name, String vendor, String version, boolean success, List<Datacenter> locations){
		this.name = name;
		this.vendor = vendor;
		this.version = version;
		this.success = success;
		this.locations = locations;
	}
	
	public Response(){
		locations = new ArrayList<Datacenter>();
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
			for(Datacenter d : locations){
				jsonArray.put(d.parseToJsonObject());
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

	public List<Datacenter> getLocations() {
		return locations;
	}

	public void setLocations(List<Datacenter> locations) {
		this.locations = locations;
	}

}
