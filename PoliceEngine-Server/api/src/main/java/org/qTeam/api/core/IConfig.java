package org.qTeam.api.core;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface IConfig {
  
  public final static String HOME = System.getProperty("user.home");
  public final static String KEY_HOSTNAME = "hostname";
  public final static String DEFAULT_HOSTNAME = "localhost";
  public final static String FITEAGLE_DIRECTORY = ".fiteagle";
  public final static String FITEAGLE_FILE_NAME = "fiteagle.properties";
  public final static String LOCAL_NAMESPACE = "Local_namespace";
  public final static String EXTENSION = ".properties";
  
  public final static Path HOME_PATH = Paths.get(HOME);
  public final static Path PROPERTIES_DIRECTORY = HOME_PATH.resolve(FITEAGLE_DIRECTORY);
    public final  String RESOURCE_NAMESPACE = "Resource_namespace" ;
    public final String  LOCAL_NAMESPACE_VALUE = "http://".concat(IConfig.DEFAULT_HOSTNAME ).concat("/");
    public final String RESOURCE_NAMESPACE_VALUE = IConfig.LOCAL_NAMESPACE_VALUE.concat("resource/");
    public final String TOPOLOGY_NAMESPACE_VALUE = IConfig.RESOURCE_NAMESPACE_VALUE.concat("topology/");
    public final String RESERVATION_NAMESPACE_VALUE = IConfig.RESOURCE_NAMESPACE_VALUE.concat("reservation/");
}
