package org.qTeam.api.core;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Map;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class OntologyModelUtil {
  
  public static Model loadModel(String filename, String serialization) {
    Model model = ModelFactory.createDefaultModel();
    
    InputStream in = FileManager.get().open(filename);
    if (in == null) {
      throw new IllegalArgumentException("Ontology File: " + filename + " not found");
    }
    
    model.read(in, null, serialization);
    
    return model;
  }
  
  public static String toString(Model model) {
      StringWriter out = new StringWriter();
      RDFDataMgr.write(out, model, Lang.NT);
      return out.toString();
  }
  public static String getLocalNamespace() {
    Config config = new Config();
    return config.getProperty(IConfig.LOCAL_NAMESPACE);

  }
  public static String getResourceNamespace(){
    Config config = new Config();
    return config.getProperty(IConfig.RESOURCE_NAMESPACE);
  }
  
  public static String[] getNamespaceAndLocalname(String uri, Map<String, String> prefixes) throws IllegalArgumentException {
    String[] namespaceAndLocalname = new String[2];
    if(uri.startsWith("http://")){
      if(uri.contains("#")){
        String[] splitted = uri.split("#");
        if(splitted.length != 2){
          throw new IllegalArgumentException("Unsupported URI: "+uri);
        }
        else{
          String namespace = splitted[0]+"#";
          namespaceAndLocalname[0] = namespace;
          namespaceAndLocalname[1] = splitted[1];
        }
      }
      else{
        String namespace = uri.substring(0, uri.lastIndexOf("/")+1);
        String localname = uri.substring(uri.lastIndexOf("/")+1, uri.length());
        if(namespace.isEmpty() || localname.isEmpty()){
          throw new IllegalArgumentException("Unsupported URI: "+uri);
        }
        namespaceAndLocalname[0] = namespace;
        namespaceAndLocalname[1] = localname;
      }
    }
    else if(uri.contains(":")){
      String[] splitted = uri.split(":");
      if(splitted.length != 2){
        throw new IllegalArgumentException("Unsupported URI: "+uri);
      }
      else{
        String prefix = splitted[0];
        String namespace = prefixes.get(prefix);
        if(namespace == null){
          throw new IllegalArgumentException("No namespace found for prefix: "+prefix);
        }
        namespaceAndLocalname[0] = namespace;
        namespaceAndLocalname[1] = splitted[1];
      }
    }
    else{
      throw new IllegalArgumentException("Unsupported instance URI: "+uri);
    }
    return namespaceAndLocalname;
  }
  
}
