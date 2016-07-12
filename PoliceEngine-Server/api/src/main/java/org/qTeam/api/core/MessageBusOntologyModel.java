package org.qTeam.api.core;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class MessageBusOntologyModel {
    
  public static final Property maxInstances;
  public static final Property endTime;  
  public static final Resource classAdapter;
  public static final Property startTime;
  
    static{
        Model fiteagle = OntologyModelUtil.loadModel("ontologies/fiteagle/ontology.ttl", "TURTLE");
        
        maxInstances = fiteagle.getProperty("http://open-multinet.info/ontology/omn#maxInstances");
        endTime = fiteagle.getProperty("http://open-multinet.info/ontology/omn#endTime");
        classAdapter = fiteagle.getResource("http://open-multinet.info/ontology/omn#Adapter");
        startTime = fiteagle.getProperty("http://open-multinet.info/ontology/omn#startTime");

    }
    
}
