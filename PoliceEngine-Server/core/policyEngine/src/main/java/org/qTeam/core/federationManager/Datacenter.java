package org.qTeam.core.federationManager;

import java.io.ByteArrayInputStream;

import javax.json.Json;
import javax.json.JsonReader;

import org.hornetq.utils.json.JSONException;
import org.hornetq.utils.json.JSONObject;

import javax.json.JsonArray;
import javax.json.JsonObject;

public class Datacenter {
	private String location;
	private String country;
	private String continent;
	private String name;
	private String price;
	
	
	
	public String parseToJsonString(){
		JSONObject jo = new JSONObject();
		String jsonResponse = null;
		try {
			jo.put("name", name);
			jo.put("location", location);
			jo.put("price", price);
			jo.put("country", country);
			jo.put("continent", continent);

			jsonResponse = jo.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return jsonResponse;
	}
	
	public JSONObject parseToJsonObject(){
		JSONObject jo = new JSONObject();
		try {
			jo.put("name", name);
			jo.put("location", location);
			jo.put("price", price);
			jo.put("country", country);
			jo.put("continent", continent);
			return jo;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}


		
	}




	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getContinent() {
		return continent;
	}

	public void setContinent(String continent) {
		this.continent = continent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
}
