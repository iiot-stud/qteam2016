package org.qTeam.api.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.ws.rs.core.Response;

import org.apache.jena.riot.RiotException;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFReader;

public class MessageUtil {
  
  private static Logger LOGGER = Logger.getLogger(MessageUtil.class.toString());
  
  public static Message createRDFMessage(Model rdfModel, String methodType, String methodTarget, String serialization, String correlationID, JMSContext context) {
    final Message message = context.createTextMessage(serializeModel(rdfModel, serialization));
    try {
      message.setStringProperty(IMessageBus.METHOD_TYPE, methodType);
      message.setStringProperty(IMessageBus.SERIALIZATION, serialization);
      if(correlationID != null){
        message.setJMSCorrelationID(correlationID);
      }
      else{
        message.setJMSCorrelationID(UUID.randomUUID().toString());
      }
      if(methodTarget != null){
        message.setStringProperty(IMessageBus.METHOD_TARGET, methodTarget);
      }
    } catch (JMSException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return message;
  }
  
  public static Message createDefaultMessage (String methodType, String methodTarget, String serialization, String correlationID, JMSContext context){
	    final Message message = context.createMessage();
	    try {
	      message.setStringProperty(IMessageBus.METHOD_TYPE, methodType);
	      message.setStringProperty(IMessageBus.SERIALIZATION, serialization);
	      if(correlationID != null){
	        message.setJMSCorrelationID(correlationID);
	      }
	      else{
	        message.setJMSCorrelationID(UUID.randomUUID().toString());
	      }
	      if(methodTarget != null){
	        message.setStringProperty(IMessageBus.METHOD_TARGET, methodTarget);
	      }
	    } catch (JMSException e) {
	      LOGGER.log(Level.SEVERE, e.getMessage());
	    }
	    return message;
  }
  
  public static Message createRDFMessage(String rdfModel, String methodType, String methodTarget, String serialization, String correlationID, JMSContext context) {
    final Message message = context.createTextMessage(rdfModel);
    try {
      message.setStringProperty(IMessageBus.METHOD_TYPE, methodType);
      message.setStringProperty(IMessageBus.SERIALIZATION, serialization);
      if(correlationID != null){
        message.setJMSCorrelationID(correlationID);
      }
      else{
        message.setJMSCorrelationID(UUID.randomUUID().toString());
      }
      if(methodTarget != null){
        message.setStringProperty(IMessageBus.METHOD_TARGET, methodTarget);
      }
    } catch (JMSException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return message;
  }
  
  public static Message createSPARQLQueryMessage(String query, String methodTarget, String serialization, JMSContext context) {
    final Message message = context.createTextMessage(query);
    try {
      message.setStringProperty(IMessageBus.METHOD_TYPE, IMessageBus.TYPE_GET);
      message.setStringProperty(IMessageBus.SERIALIZATION, serialization);
      message.setJMSCorrelationID(UUID.randomUUID().toString());
      if(methodTarget != null){
        message.setStringProperty(IMessageBus.METHOD_TARGET, methodTarget);
      }
    } catch (JMSException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return message;
  }
  
  public static Message createErrorMessage(String errorMessage, String correlationID, JMSContext context) {
    final Message message = context.createTextMessage(errorMessage);
    try {
      message.setStringProperty(IMessageBus.METHOD_TYPE, IMessageBus.TYPE_ERROR);
      if(correlationID != null){
        message.setJMSCorrelationID(correlationID);
      }
      else{
        message.setJMSCorrelationID(UUID.randomUUID().toString());
      }
    } catch (JMSException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return message;
  }
  
  public static String getMessageType(Message message) {
    try {
      return message.getStringProperty(IMessageBus.METHOD_TYPE);
    } catch (JMSException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return null;
  }
  
  public static String getMessageTarget(Message message) {
    try {
      return message.getStringProperty(IMessageBus.METHOD_TARGET);
    } catch (JMSException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return null;
  }
  
  public static String getMessageSerialization(Message message) {
    try {
      return message.getStringProperty(IMessageBus.SERIALIZATION);
    } catch (JMSException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return null;
  }
  
  public static String getJMSCorrelationID(Message message) {
    try {
      return message.getJMSCorrelationID();
    } catch (JMSException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return null;
  }
  
  public static String serializeModel(Model rdfModel, String serialization) {
    StringWriter writer = new StringWriter();
    rdfModel.write(writer, serialization);
    String serializedModel = writer.toString();
    if (serialization.equals(IMessageBus.SERIALIZATION_RDFXML)) {
      String[] splitted = serializedModel.split("\n");
      for (int i = 0; i < splitted.length; i++) {
        if (splitted[i].replace(" ", "").replace("\t", "").startsWith("xmlns:j.")) {
          serializedModel = serializedModel.replace(splitted[i] + "\n", "");
        }
      }
    }
    return serializedModel;
  }
  
  public static Model parseSerializedModel(String modelString, String serialization) throws RiotException {
    
    Model rdfModel = ModelFactory.createDefaultModel();
    
    RDFReader reader = rdfModel.getReader(serialization);
    InputStream is = new ByteArrayInputStream(modelString.getBytes(Charset.defaultCharset()));
    reader.read(rdfModel, is, IConfig.RESOURCE_NAMESPACE_VALUE);
    
    return rdfModel;
  }
  
  public static String getStringBody(final Message receivedMessage) {
    String result = null;
    try {
      result = receivedMessage.getBody(String.class);
    } catch (JMSException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return result;
  }
  
  public static Message waitForResult(Message requestMessage, JMSContext context, Topic topic) {
    String filter = "";
    try {
      filter = "JMSCorrelationID='" + requestMessage.getJMSCorrelationID() + "'";
    } catch (JMSException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    Message rcvMessage = context.createConsumer(topic, filter).receive(IMessageBus.TIMEOUT);
    
    if(rcvMessage == null){
      rcvMessage = context.createTextMessage(Response.Status.REQUEST_TIMEOUT.name());
      try {
        rcvMessage.setStringProperty(IMessageBus.METHOD_TYPE, IMessageBus.TYPE_ERROR);
      } catch (JMSException e) {
        LOGGER.log(Level.SEVERE, e.getMessage());
      }      
    }
    return rcvMessage;
  }
  
  public static String getSPARQLQuery(Message message) throws RuntimeException {
    String query = null;
    try {
      query = message.getBody(String.class);
    } catch (JMSException e) {
      throw new RuntimeException("SPARQL Query expected, but no sparql query found!");
    }
    if (query == null || query.isEmpty()) {
      throw new RuntimeException("SPARQL Query expected, but no sparql query found!");
    }
    return query;
  }
  
  public static String parseResultSetToJson(ResultSet resultSet) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ResultSetFormatter.outputAsJSON(baos, resultSet);
    String jsonString = "";
    try {
      jsonString = baos.toString(Charset.defaultCharset().toString());
      baos.close();
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
    }
    return jsonString;
  }
  
  public static void setCommonPrefixes(Model model) {
    model.removeNsPrefix("j.0");
    model.removeNsPrefix("j.1");
    model.removeNsPrefix("j.2");
    model.removeNsPrefix("j.3");
    model.removeNsPrefix("j.4");
    model.removeNsPrefix("j.5");
    
    model.setNsPrefix("wgs", "http://www.w3.org/2003/01/geo/wgs84_pos#");
    model.setNsPrefix("foaf", "http://xmlns.com/foaf/0.1/");
    model.setNsPrefix("omn", "http://open-multinet.info/ontology/omn#");
    model.setNsPrefix("owl", "http://www.w3.org/2002/07/owl#");
    model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    model.setNsPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
    model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
  }
  
 public static class ParsingException extends Exception {
    
    private static final long serialVersionUID = 8213556984621316215L;

    public ParsingException(String message){
      super(message);
    }
  }
 
 public static class TimeoutException extends RuntimeException {
   
   private static final long serialVersionUID = -5630226460026376892L;
   
   public TimeoutException(String message) {
     super("Timeout while waiting for a response: " + message);
   }
 }
  
}
