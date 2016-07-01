package org.qTeam.core.federationManager;

import java.util.ArrayList;
import java.util.List;

public class Request {
	private String name;
	private String vendor;
	private String version;
	private List<String> preferences;
	private List<String> attributes;
	
	public Request(String name, String vendor, String version, List<String> preferences, List<String> attributes){
		this.name = name;
		this.vendor = vendor;
		this.version = version;
		this.preferences = preferences;
		this.attributes = attributes;
	}
	
	public Request(){
		attributes =new ArrayList<String>();
		preferences = new ArrayList<String>();
	}
	
	protected String getName() {
		return name;
	}
	protected void setName(String name) {
		this.name = name;
	}
	protected String getVendor() {
		return vendor;
	}
	protected void setVendor(String vendor) {
		this.vendor = vendor;
	}
	protected List<String> getPreferences() {
		return preferences;
	}
	protected void setPreferences(List<String> preferences) {
		this.preferences = preferences;
	}
	protected List<String> getAttributes() {
		return attributes;
	}
	protected void setAttributes(List<String> attributes) {
		this.attributes = attributes;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

}
