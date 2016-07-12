package org.qTeam.api.tripletStoreAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jena.atlas.web.HttpException;
import org.qTeam.api.tripletStoreAccessor.TripletStoreAccessor.ResourceRepositoryException;
import org.qTeam.api.tripletStoreAccessor.QueryExecuter;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.sparql.engine.http.QueryExceptionHTTP;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;

public class QueryExecuter {
  
  private static Logger LOGGER = Logger.getLogger(QueryExecuter.class.toString());
  
  private static Map<String, String> missedNsPrefixes = new HashMap<>();

  protected static final String SESAME_SERVICE = "http://localhost:8080/openrdf-sesame/repositories/QTeam";
  protected static final String SESAME_SERVICE_DATA = SESAME_SERVICE + "/statements";

  static {
    missedNsPrefixes.put("wgs", "http://www.w3.org/2003/01/geo/wgs84_pos#");
    missedNsPrefixes.put("omn", "http://open-multinet.info/ontology/omn#");
    missedNsPrefixes.put("av", "http://federation.av.tu-berlin.de/about#");
    missedNsPrefixes.put("foaf", "http://xmlns.com/foaf/0.1/");
    missedNsPrefixes.put("dc", "http://purl.org/dc/elements/1.1/");
    missedNsPrefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");
    missedNsPrefixes.put("motorgarage", "http://open-multinet.info/ontology/resource/motorgarage#");
    missedNsPrefixes.put("motor", "http://open-multinet.info/ontology/resource/motor#");
  }
  
  public static ResultSet executeSparqlSelectQuery(String queryString) throws ResourceRepositoryException {
    ResultSet rs = null;
    try{
      QueryExecution qe = QueryExecutionFactory.sparqlService(SESAME_SERVICE, queryString);
      LOGGER.log(Level.INFO,qe.toString());
      rs = qe.execSelect();
    } catch(QueryExceptionHTTP | QueryParseException e){
      throw new ResourceRepositoryException(e.getMessage());
    }
    return rs;
  }
  
  public static boolean executeSparqlAskQuery(String queryString) throws ResourceRepositoryException {
	  boolean result = false;
	  try{
		  QueryExecution qe = QueryExecutionFactory.sparqlService(SESAME_SERVICE, queryString);
		  result = qe.execAsk();
	  } catch(QueryExceptionHTTP | QueryParseException e){
	      throw new ResourceRepositoryException(e.getMessage());
	    }
	  return result;
  }
  
  public static Model executeSparqlConstructQuery(String queryString) throws ResourceRepositoryException {
    Model resultModel = null;
    try{
      QueryExecution qe = QueryExecutionFactory.sparqlService(SESAME_SERVICE, queryString);
      resultModel = qe.execConstruct();
    } catch(QueryExceptionHTTP | QueryParseException e){
      throw new ResourceRepositoryException(e.getMessage());
    }
    correctNsPrefixes(resultModel);
    return resultModel;
  }
  
  public static Model executeSparqlDescribeQuery(String queryString) throws ResourceRepositoryException {
    Model resultModel = null;
    try{
      QueryExecution qe = QueryExecutionFactory.sparqlService(SESAME_SERVICE, queryString);
      resultModel = qe.execDescribe();
    } catch(QueryExceptionHTTP | QueryParseException e){
      throw new ResourceRepositoryException(e.getMessage());
    }
    correctNsPrefixes(resultModel);
    return resultModel;
  }
  
  public static void executeSparqlUpdateQuery(String queryString) throws ResourceRepositoryException{
    UpdateRequest req = UpdateFactory.create(queryString);
    UpdateProcessor up = UpdateExecutionFactory.createRemote(req, SESAME_SERVICE);
    try{
      up.execute();
    } catch (HttpException e) {
      throw new ResourceRepositoryException(e.getMessage());
    }
  }
  
  public static void correctNsPrefixes(Model model) {
    // todo: find a better way to set our own common known prefixes (e.g. omn, wgs, ...)
    Map<String, String> nsPrefix = model.getNsPrefixMap();
    for (Map.Entry<String, String> entry : nsPrefix.entrySet()) {
      String currentKey = entry.getKey();
      if (currentKey.toString().startsWith("j.")) {
        model.removeNsPrefix(currentKey);
        for (Map.Entry<String, String> staticEntry : missedNsPrefixes.entrySet()) {
          if (currentKey.toString().equals(staticEntry.getValue().toString())) {
            model.setNsPrefix(staticEntry.getKey().toString(), staticEntry.getValue().toString());
          }
        }
      }
    }
  }
  
  public static void removeProblematicNsPrefixes(Model model) {
    Map<String, String> nsPrefix = model.getNsPrefixMap();
    for (Map.Entry<String, String> entry : nsPrefix.entrySet()) {
      String currentKey = entry.getKey();
      if (currentKey.toString().startsWith("j.")) {
        model.removeNsPrefix(currentKey);
      }
    }
  }
  
}
