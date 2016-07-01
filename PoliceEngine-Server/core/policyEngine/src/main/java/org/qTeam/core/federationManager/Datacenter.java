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
			return jo;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}


		
	}



	protected String getCountry() {
		return country;
	}



	protected void setCountry(String country) {
		this.country = country;
	}



	protected String getContinent() {
		return continent;
	}



	protected void setContinent(String continent) {
		this.continent = continent;
	}



	protected String getName() {
		return name;
	}



	protected void setName(String name) {
		this.name = name;
	}



	protected String getPrice() {
		return price;
	}



	protected void setPrice(String price) {
		this.price = price;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
