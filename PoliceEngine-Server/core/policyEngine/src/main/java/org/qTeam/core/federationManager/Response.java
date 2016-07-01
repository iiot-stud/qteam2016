package org.qTeam.core.federationManager;

import java.util.ArrayList;
import java.util.List;

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
			
			return mainObject.toString();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
