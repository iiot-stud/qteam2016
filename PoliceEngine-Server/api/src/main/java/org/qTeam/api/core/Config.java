package org.qTeam.api.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.jena.atlas.logging.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.hp.hpl.jena.util.FileManager;

public class Config {

	private Path FILE_PATH;

	private static Logger LOGGER = Logger.getLogger(Config.class.toString());

	public Config() {
		this.FILE_PATH = IConfig.PROPERTIES_DIRECTORY
				.resolve(IConfig.FITEAGLE_FILE_NAME);
		checkFolder();
		setDefaultProperty();
	}

	public Config(String file_name) {
		file_name = file_name.concat(IConfig.EXTENSION);
		this.FILE_PATH = IConfig.PROPERTIES_DIRECTORY.resolve(file_name);
		checkFolder();
	}

	public Path getFilePath(){
	  return this.FILE_PATH;
	}
	
	public void createPropertiesFile() {
		deletePropertiesFile();
		setDefaultProperty();
	}

	private void checkFolder() {

		if (Files.notExists(IConfig.PROPERTIES_DIRECTORY)) {
			try {
				Files.createDirectory(IConfig.PROPERTIES_DIRECTORY);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "The directory can't be created ", e);
			}
		}
	}

	public void setDefaultProperty() {
		File file = FILE_PATH.toFile();
		if (!file.exists()) {
			Properties property = new Properties();
			property.put(IConfig.KEY_HOSTNAME, IConfig.DEFAULT_HOSTNAME);
			property.put(IConfig.LOCAL_NAMESPACE, IConfig.LOCAL_NAMESPACE_VALUE);
			property.put(IConfig.RESOURCE_NAMESPACE,
					IConfig.RESOURCE_NAMESPACE_VALUE);
			writeProperties(property);
		} else {
			Boolean changed = false;
			Properties property = readProperties();
			if (!property.containsKey(IConfig.KEY_HOSTNAME)) {
				property.put(IConfig.KEY_HOSTNAME, IConfig.DEFAULT_HOSTNAME);
				changed = true;
			}
			if (!property.containsKey(IConfig.RESOURCE_NAMESPACE)) {
				property.put(IConfig.RESOURCE_NAMESPACE,
						IConfig.RESOURCE_NAMESPACE_VALUE);
				changed = true;
			}
			if (!property.containsKey(IConfig.LOCAL_NAMESPACE)) {
				property.put(IConfig.LOCAL_NAMESPACE,
						IConfig.LOCAL_NAMESPACE_VALUE);
				changed = true;
			}
			if (changed == true) writeProperties(property);
		}
	}

	public void setNewProperty(String propertyKey, String propertyValue) {
		Properties property = readProperties();
		property.put(propertyKey, propertyValue);
		writeProperties(property);
	}

	public String getProperty(String propertyKey) {
		String propertyValue = null;
		propertyValue = readProperties().getProperty(propertyKey);
		if (propertyValue == null) {
			throw new IllegalArgumentException(
					"there is no value for the property " + propertyKey);
		}
		return propertyValue;
	}

	public HashMap<String, String> getAllProperties() {
		Properties property = readProperties();
		Enumeration<Object> enuKeys = property.keys();
		HashMap<String, String> allProperties = new HashMap<>();
		while (enuKeys.hasMoreElements()) {
			String key = (String) enuKeys.nextElement();
			String value = property.getProperty(key);
			allProperties.put(key, value);
			LOGGER.log(Level.INFO, key + ":" + value);
		}
		return allProperties;
	}

	public void deleteProperty(String propertyKey) {
		Properties property = readProperties();
		property.remove(propertyKey);
		writeProperties(property);
	}

	public void updateProperty(String propertyKey, String propertyValue) {
		Properties property = readProperties();
		property.put(propertyKey, propertyValue);
		writeProperties(property);
	}

	public void deletePropertiesFile() {
		if (Files.exists(FILE_PATH)) {
			try {
				Files.delete(FILE_PATH);
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "The file can't be deleted", e);
			}
		}
	}

	private Properties convertProperties() throws Exception{
		Properties property = new Properties();	
		InputStream inputStream = FileManager.get().open(FILE_PATH.toString());

	      property.load(inputStream);
	      inputStream.close();
	      writeProperties(property);
	      Gson gson = new GsonBuilder().setPrettyPrinting().create();
	      String input = IOUtils.toString(new FileInputStream(FILE_PATH.toFile()), "UTF-8");
	      property = gson.fromJson(input, Properties.class);

	      return property;
	}
	
	public Properties readProperties() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Properties property = new Properties();	
		
		try {
	        String input = IOUtils.toString(new FileInputStream(FILE_PATH.toFile()), "UTF-8");
			property = gson.fromJson(input, Properties.class);
		}catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Properties file " + FILE_PATH.toString()
					+ " can't be opened ", e);
		} catch (JsonSyntaxException e) {
			try{
				property = convertProperties();
			}catch(Exception e2){
				LOGGER.log(Level.SEVERE, "JSONException - Could not read Properties file - Then tried to convert it into JSON but failed");	
			}
		} 
		return property;
	}
	
	public String readJsonProperties(){
        String input = "";
		try {
			input = IOUtils.toString(new FileInputStream(FILE_PATH.toFile()), "UTF-8");
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, "Properties file is not found ", e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Properties couldn't be read ", e);
			e.printStackTrace();
		}
        return input;
    
	}

	public void writeProperties(Properties property) {
		FileWriter writer;
		try {
			File file = FILE_PATH.toFile();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String jsonString = gson.toJson(property);

			writer = new FileWriter(file);
			writer.write(jsonString);
			writer.flush();
			writer.close();
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, "Properties file " + FILE_PATH.toString()
					+ " is not found ", e);
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "properties couldn't be stored in "
					+ FILE_PATH.toString(), e);
		}
	}

	public boolean fileExists() {
		File file = FILE_PATH.toFile();
		return file.exists();
	}
}
