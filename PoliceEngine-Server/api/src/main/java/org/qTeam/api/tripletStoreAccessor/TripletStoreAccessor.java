package org.qTeam.api.tripletStoreAccessor;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDFS;

import info.openmultinet.ontology.Parser;
import info.openmultinet.ontology.exceptions.InvalidModelException;
import info.openmultinet.ontology.vocabulary.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.AccessTimeout;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.jena.atlas.web.HttpException;
import org.qTeam.api.core.IMessageBus;
import org.qTeam.api.core.MessageUtil;
import org.qTeam.api.core.MessageUtil.ParsingException;
import org.qTeam.api.tripletStoreAccessor.QueryExecuter;
//import org.fiteagle.api.tripletStoreAccessor.ResourceRepositoryException;
import org.qTeam.api.tripletStoreAccessor.TripletStoreAccessor.ResourceRepositoryException;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Node_Variable;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.DatasetAccessor;
import com.hp.hpl.jena.query.DatasetAccessorFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.sparql.core.BasicPattern;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.modify.request.QuadAcc;
import com.hp.hpl.jena.sparql.modify.request.UpdateDeleteWhere;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.Template;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.update.UpdateProcessor;
import com.hp.hpl.jena.update.UpdateRequest;
import com.hp.hpl.jena.vocabulary.RDF;

public class TripletStoreAccessor {
  
  private static Logger LOGGER = Logger.getLogger(TripletStoreAccessor.class.toString());

  private final static String FUNCTIONAL_PROPERTY = "http://www.w3.org/2002/07/owl#FunctionalProperty";
  


  public static void removePropertyValue(Resource subject, Resource predicate) throws ResourceRepositoryException{
    String existingValue = "<"+subject.getURI()+"> <"+predicate.getURI()+"> ?anyObject .";

    String updateString = "DELETE { "+existingValue+" }" + "WHERE { "+existingValue+" }";

    QueryExecuter.executeSparqlUpdateQuery(updateString);
  }
  

  private static DatasetAccessor getTripletStoreAccessor() throws ResourceRepositoryException {
    DatasetAccessor accessor = DatasetAccessorFactory.createHTTP(QueryExecuter.SESAME_SERVICE_DATA);
    if (accessor == null) {
      throw new ResourceRepositoryException("Could not connect to fuseki service at:" + QueryExecuter.SESAME_SERVICE_DATA);
    }
    return accessor;
  }
  
  public static Model getResourcesAsModel() throws ResourceRepositoryException {

      Query query = QueryFactory.create();
      query.setQueryDescribeType();
      query.addResultVar("resource");
      query.addResultVar("p");
      LOGGER.log(Level.SEVERE, "Working on GetResources in TripletStoreAccesor");

      Node node = new Node_Variable("http://www.q-team.org/Ontology#Country");
      Model model = getNodesAndLinks(query, node);
      model.add(getNodesAndLinks(query, Omn_resource.Link.asNode()));
      model.add(getDiskImages());
      Model locationModel = getLocations(model);
      model.add(locationModel);
      model.setNsPrefixes(getNsPrefixMappings());

      return model;
  }

  public static Model getInfrastructure() throws ResourceRepositoryException {

      Query query = QueryFactory.create();
      query.setQueryConstructType();
      query.addResultVar("infrastructure");

      Triple tripleForPattern = new Triple(new Node_Variable("infrastructure"),new Node_Variable("o"),new Node_Variable("p"));
      BasicPattern constructPattern = new BasicPattern();
      constructPattern.add(tripleForPattern);
      query.setConstructTemplate(new Template(constructPattern));

      ElementGroup whereClause = new ElementGroup();
      whereClause.addTriplePattern(new Triple(new Node_Variable("infrastructure"), RDF.type.asNode(), Omn_federation.Infrastructure.asNode()));
      whereClause.addTriplePattern(tripleForPattern);
      query.setQueryPattern(whereClause);




        LOGGER.log(Level.INFO, query.serialize());
  //    Query query =  QueryFactory.create(queryString);
      Model model = executeConstruct(query);


    return model;
  }

    private static Model executeConstruct(Query query) {
        QueryExecution queryExecution = QueryExecutionFactory.sparqlService(QueryExecuter.SESAME_SERVICE, query);


        return queryExecution.execConstruct();
    }


    public static String getResources() throws ResourceRepositoryException {

        Query query = QueryFactory.create();
        query.setQueryDescribeType();
        query.addResultVar("resource");
        query.addResultVar("p");
        LOGGER.log(Level.SEVERE, "Working on GetResources in TripletStoreAccesor");

        Model model = getNodesAndLinks(query, Omn.Resource.asNode());
        model.add(getNodesAndLinks(query, Omn_resource.Link.asNode()));
        model.add(getDiskImages());
        Model locationModel = getLocations(model);
        model.add(locationModel);
        model.setNsPrefixes(getNsPrefixMappings());

        String serializedAnswer = MessageUtil.serializeModel(model,IMessageBus.SERIALIZATION_TURTLE);
        LOGGER.log(Level.SEVERE, "Serialzed Answer from TripletStoreAccessor GetResources:");
        LOGGER.log(Level.SEVERE, serializedAnswer);

        return serializedAnswer;
    }
    
    private static Model getDiskImages() throws ResourceRepositoryException{
    	Query query = QueryFactory.create();
        query.setQueryDescribeType();
        query.addResultVar("resource");
        query.addResultVar("p");

        
        ElementGroup whereClause = new ElementGroup();
        whereClause.addTriplePattern(new Triple(new Node_Variable("resource"), RDF.type.asNode(), Omn_domain_pc.DiskImage.asNode()));
       // whereClause.addTriplePattern(tripleForPattern);
//        whereClause.addTriplePattern(new Triple(new Node_Variable("resource"), new Node_Variable("p"), new Node_Variable("o")));
        query.setQueryPattern(whereClause);

        QueryExecution execution =  QueryExecutionFactory.sparqlService(QueryExecuter.SESAME_SERVICE, query);
        Model model = execution.execDescribe();
        
        return model;
    }

    private static Model getLocations(Model model) {
    	
        ResIterator resIterator = model.listResourcesWithProperty(Omn_lifecycle.canImplement);
        Model locationModel =  ModelFactory.createDefaultModel();
        while(resIterator.hasNext()) {
            Resource adapter = resIterator.nextResource();
            StmtIterator stmtIterator = adapter.listProperties(Omn_resource.hasLocation);

            while(stmtIterator.hasNext()){
                Statement statement = stmtIterator.nextStatement();
                //TODO getAll Resources in one request
                Model location = getResource(statement.getObject().asResource().getURI());
                locationModel.add(location);
            }



        }

        return locationModel;


    }

    public static Model getNodesAndLinks(Query query, Node node) throws ResourceRepositoryException {
      ElementGroup whereClause = new ElementGroup();
      whereClause.addTriplePattern(new Triple(new Node_Variable("resource"), Omn_lifecycle.canImplement.asNode(), new Node_Variable("p")));
     // whereClause.addTriplePattern(tripleForPattern);
      whereClause.addTriplePattern(new Triple(new Node_Variable("p"), RDFS.subClassOf.asNode(), node));
      query.setQueryPattern(whereClause);

      QueryExecution execution =  QueryExecutionFactory.sparqlService(QueryExecuter.SESAME_SERVICE, query);
      Model model = execution.execDescribe();
      
      return model;
    }

    public static boolean exists(String uri) {
        Triple triple = new Triple(ResourceFactory.createResource(uri).asNode(), new Node_Variable("p"), new Node_Variable("o"));
        return executeAskQuery(triple);
    }
    
    public static boolean exists(Resource resource, Property property, RDFNode rdfNode){
      Triple triple = new Triple(resource.asNode(), property.asNode(), rdfNode.asNode());
      return executeAskQuery(triple);
    }
    
    private static boolean executeAskQuery(Triple triple){
      Query query = QueryFactory.create();
      query.setQueryAskType();
      ElementGroup whereclause = new ElementGroup();
      whereclause.addTriplePattern(triple);
      query.setQueryPattern(whereclause);
      QueryExecution execution = QueryExecutionFactory.sparqlService(QueryExecuter.SESAME_SERVICE, query);
      
      return execution.execAsk();
    }
    

    public static Model getResource(String uri) {
        Query query =  QueryFactory.create();
        query.setQueryDescribeType();
        query.addDescribeNode(ResourceFactory.createResource(uri).asNode());

        LOGGER.log(Level.INFO, query.serialize());
        QueryExecution queryExecution = QueryExecutionFactory.sparqlService(QueryExecuter.SESAME_SERVICE, query);
        Model model = queryExecution.execDescribe();
        return model;
    }


    public static void addModel(Model model) throws ResourceRepositoryException, InvalidModelException {

        DatasetAccessor datasetAccessor = getTripletStoreAccessor();
        try {
            datasetAccessor.add(model);
        } catch (HttpException e) {
            throw new ResourceRepositoryException(e.getMessage());
        }
    }

    public static void deleteModel(Model model) throws ResourceRepositoryException, InvalidModelException {

        StmtIterator stmtIterator = model.listStatements();
        List<Quad> quadList = new LinkedList<>();
        while (stmtIterator.hasNext()){
            Statement statement = stmtIterator.nextStatement();
            Quad quad = new Quad(Quad.defaultGraphIRI,statement.asTriple());
            quadList.add(quad);
        }
        deleteStatementList(quadList);
    }

    public static void deleteTriple(Triple statement){
        List<Quad> quadList = new LinkedList<>();
        Quad quad = new Quad(Quad.defaultGraphIRI,statement);
        quadList.add(quad);
        deleteStatementList(quadList);
    }

    private static void deleteStatementList(List<Quad> quadList) {
        QuadAcc quadAcc = new QuadAcc(quadList);
        UpdateDeleteWhere updateDeleteInsert = new UpdateDeleteWhere(quadAcc);
        UpdateRequest request = new UpdateRequest(updateDeleteInsert);
        UpdateProcessor qexec= UpdateExecutionFactory.createRemoteForm(request, QueryExecuter.SESAME_SERVICE_DATA);
        qexec.execute();
    }

    public static void updateModel(Model model) throws ResourceRepositoryException, InvalidModelException {

        StmtIterator stmtIterator = model.listStatements();
        while(stmtIterator.hasNext()){
            Statement statement = stmtIterator.nextStatement();
            Property property = statement.getPredicate();
            Resource type = null;
            Statement typeStatement = property.getProperty(RDF.type);
            if(typeStatement != null ){
               RDFNode rdfNode =  typeStatement.getObject();
                type = rdfNode.asResource();
            }
            if(type != null && type.equals(OWL.FunctionalProperty)){
                Triple triple = new Triple(statement.getSubject().asNode(), statement.getPredicate().asNode(), new Node_Variable("o"));

                deleteTriple(triple);
            }
        }
        addModel(model);
    }
    
    public static Model getDataCenters(){
    	Query query = QueryFactory.create();
        query.setQueryDescribeType();
        query.addResultVar("resource");
        Triple tripleForPattern = new Triple(new Node_Variable("resource"),new Node_Variable("o"),new Node_Variable("p"));
        Node node = ModelFactory.createDefaultModel().getResource("http://www.q-team.org/Ontology#Data_center").asNode();
        ElementGroup whereClause = new ElementGroup();
        whereClause.addTriplePattern(new Triple(new Node_Variable("resource"), RDF.type.asNode(), node));
        whereClause.addTriplePattern(tripleForPattern);
        query.setQueryPattern(whereClause);

        QueryExecution execution =  QueryExecutionFactory.sparqlService(QueryExecuter.SESAME_SERVICE, query);
        Model model = execution.execDescribe();
    	return model;
    }
    
    public static Model getAllSlots(){
    	Query query = QueryFactory.create();
        query.setQueryDescribeType();
        query.addResultVar("resource");
        Triple tripleForPattern = new Triple(new Node_Variable("resource"),new Node_Variable("o"),new Node_Variable("p"));
        Node node = ModelFactory.createDefaultModel().getResource("http://www.q-team.org/Ontology#Slot").asNode();
        ElementGroup whereClause = new ElementGroup();
        whereClause.addTriplePattern(new Triple(new Node_Variable("resource"), RDF.type.asNode(), node));
        whereClause.addTriplePattern(tripleForPattern);
        query.setQueryPattern(whereClause);

        QueryExecution execution =  QueryExecutionFactory.sparqlService(QueryExecuter.SESAME_SERVICE, query);
        Model model = execution.execDescribe();
    	return model;
    }
    
    public static Model getRules(){
    	Query query = QueryFactory.create();
        query.setQueryDescribeType();
        query.addResultVar("resource");
        Triple tripleForPattern = new Triple(new Node_Variable("resource"),new Node_Variable("o"),new Node_Variable("p"));
        Node node = ModelFactory.createDefaultModel().getResource("http://www.q-team.org/Ontology#Rule").asNode();
        ElementGroup whereClause = new ElementGroup();
        whereClause.addTriplePattern(new Triple(new Node_Variable("resource"), RDF.type.asNode(), node));
        whereClause.addTriplePattern(tripleForPattern);
        query.setQueryPattern(whereClause);

        QueryExecution execution =  QueryExecutionFactory.sparqlService(QueryExecuter.SESAME_SERVICE, query);
        Model model = execution.execDescribe();
        
    	return model;
    }
    
    public static Model getCountrys(){
    	Query query = QueryFactory.create();
        query.setQueryDescribeType();
        query.addResultVar("resource");
        Triple tripleForPattern = new Triple(new Node_Variable("resource"),new Node_Variable("o"),new Node_Variable("p"));
        Node node = ModelFactory.createDefaultModel().getResource("http://www.q-team.org/Ontology#Country").asNode();
        ElementGroup whereClause = new ElementGroup();
        whereClause.addTriplePattern(new Triple(new Node_Variable("resource"), RDF.type.asNode(), node));
        whereClause.addTriplePattern(tripleForPattern);
        query.setQueryPattern(whereClause);

        QueryExecution execution =  QueryExecutionFactory.sparqlService(QueryExecuter.SESAME_SERVICE, query);
        Model model = execution.execDescribe();
        
    	return model;
    }
    
    public static Model getAPIs(){
    	Query query = QueryFactory.create();
        query.setQueryDescribeType();
        query.addResultVar("resource");
        Triple tripleForPattern = new Triple(new Node_Variable("resource"),new Node_Variable("o"),new Node_Variable("p"));
        
        ElementGroup whereClause = new ElementGroup();
        whereClause.addTriplePattern(new Triple(new Node_Variable("resource"), Omn_resource.hasInterface.asNode(), new Node_Variable("p")));
        whereClause.addTriplePattern(tripleForPattern);
        query.setQueryPattern(whereClause);

        QueryExecution execution =  QueryExecutionFactory.sparqlService(QueryExecuter.SESAME_SERVICE, query);
        Model model = execution.execDescribe();
        
    	return model;
    }



    public static class ResourceRepositoryException extends Exception {

    private static final long serialVersionUID = 8213556984621316215L;

    public ResourceRepositoryException(String message){
      super(message);
    }
  }

  public static void addResource(Resource resource) throws ResourceRepositoryException {
    DatasetAccessor accessor = TripletStoreAccessor.getTripletStoreAccessor();
    accessor.add(resource.getModel());
  }

  public static Model getGraph() throws ResourceRepositoryException {
    DatasetAccessor accessor = TripletStoreAccessor.getTripletStoreAccessor();
    return accessor.getModel();
  }
  
  public static Map<String, String> getNsPrefixMappings() throws ResourceRepositoryException{
    return getGraph().getNsPrefixMap();
  }
}
