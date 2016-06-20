package org.qTeam.core.federationManager;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.qTeam.api.tripletStoreAccessor.TripletStoreAccessor;
import org.qTeam.api.tripletStoreAccessor.TripletStoreAccessor.ResourceRepositoryException;
import org.hornetq.utils.json.JSONArray;
import org.hornetq.utils.json.JSONException;
import org.hornetq.utils.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

public class PolicyEngine {

	public Response handleRequest(String jsonRequest) {
		// Parsing the JSON String to an Java Class
		Request request = parseFromJSON(jsonRequest);	
		
		// Finding all countrys that are suitable for the given attributes
		ArrayList<String> countryList = findCountrys(request);
		
		// Check if one of our prefered Locations is in the ArrayList
		ArrayList<String> favoriteCountrys = new ArrayList<String>();
		for(String s : request.getPreferences()){
			if(countryList.contains(s)){
				favoriteCountrys.add(s);
			}
		}
		
		
		// Get all Datacenters for the possible Countrys
		ArrayList<Datacenter> datacenterArray = new ArrayList<Datacenter>();
		if(!favoriteCountrys.isEmpty()){
			datacenterArray = getDatacentersForCountrys(favoriteCountrys);	
		}else{
			datacenterArray = getDatacentersForCountrys(countryList);	
		}
		
		Response response = createResponse(request,datacenterArray);
		
		
		return response;
	}
	
	


	private Response createResponse(Request request, ArrayList<Datacenter> datacenterArray) {
		Response response = null;
		if(datacenterArray.isEmpty()){
			response = new Response(request.getName(),request.getVendor(),request.getVersion(),false,null);
		}else{
			response = new Response(request.getName(),request.getVendor(),request.getVersion(),true,datacenterArray);
		}

		return response;
	}




	private ArrayList<Datacenter> getDatacentersForCountrys(ArrayList<String> countryArray) {
		// TODO Ask TripletStoreAccessor for all Datacenters
		Model model = null;
		
//	 	Getting ALL Resources in the Database. Will change to get only Resources from one Country
//		or maybe better get ALL Countrys but without the other Resources
		try {
			model = TripletStoreAccessor.getResourcesAsModel();
		} catch (ResourceRepositoryException e) {
			e.printStackTrace();
		}
		
		//Filtering out the Countrys we dont want to have
		for(String s : countryArray){
			// TODO
//			model.listResourcesWithProperty("av:isIn", "av:"+s );
		}
		
		
		
		
//		resultModel.listStatements(new SimpleSelector(null, Omn.isReservationOf,(Object)null));
		
		return null;
	}




	private ArrayList<String> findCountrys(Request request) {
		ArrayList<String> countryList = new ArrayList<String>();
		/*
		 * Here is the part for the Core Policy Engine
		 * Have to implement this when Ontology and attributes are finished
		 */
		countryList.add("Germany");
		return countryList;
	}




	private Response parseToJson(){
		return null;
		
	}
	
	
	private Request parseFromJSON(String jsonString){
		Request request = new Request();
		JSONObject tmpJson = new  JSONObject();
		
		try {
			tmpJson.getJSONObject(jsonString);
			
			request.setName(tmpJson.getString("name"));
			request.setVendor(tmpJson.getString("vendor"));
			request.setVersion(tmpJson.getString("version"));
			
			JSONArray tmpArray = tmpJson.getJSONArray("preferences");
			for(int i=0 ; i < tmpArray.length() ; i++){
			request.getPreferences().add(tmpArray.getString(i));
			}
			
			tmpArray = tmpJson.getJSONArray("attributes");
			for(int i=0 ; i < tmpArray.length() ; i++){
			request.getAttributes().add(tmpArray.getString(i));
			}
			
			return request;

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
		return null;
		
	}

}