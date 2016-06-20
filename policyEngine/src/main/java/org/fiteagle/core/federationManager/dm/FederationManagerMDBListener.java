package org.fiteagle.core.federationManager.dm;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSContext;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Topic;

import info.openmultinet.ontology.exceptions.InvalidModelException;
import org.fiteagle.api.core.IMessageBus;
import org.fiteagle.api.core.MessageFilters;
import org.fiteagle.api.core.MessageUtil;
import org.fiteagle.api.tripletStoreAccessor.TripletStoreAccessor;
import org.fiteagle.api.tripletStoreAccessor.TripletStoreAccessor.ResourceRepositoryException;
import org.fiteagle.core.federationManager.FederationManager;

import com.hp.hpl.jena.rdf.model.Model;

@MessageDriven(name = "FederationManagerMDBListener", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = IMessageBus.TOPIC_CORE),
        @ActivationConfigProperty(propertyName = "messageSelector", propertyValue = MessageFilters.FILTER_FEDERATION_MANAGER),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge")})
public class FederationManagerMDBListener implements MessageListener {

    private static Logger LOGGER = Logger.getLogger(FederationManagerMDBListener.class.toString());

    @Inject
    private JMSContext context;
    @javax.annotation.Resource(mappedName = IMessageBus.TOPIC_CORE_NAME)
    private Topic topic;

    @Inject
    FederationManager federationManager;

    @PostConstruct
    private void initIfNecessary() {

        federationManager.runSetup();

    }

    public void onMessage(final Message message) {
        String messageType = MessageUtil.getMessageType(message);
        String serialization = MessageUtil.getMessageSerialization(message);
        LOGGER.log(Level.INFO, "Received a " + messageType + " message");

        if (messageType != null) {
            if (messageType.equals(IMessageBus.TYPE_GET)) {
                handleGet(serialization, MessageUtil.getJMSCorrelationID(message));
            }
            if (messageType.equals(IMessageBus.TYPE_CREATE)) {
                String rdfString = MessageUtil.getStringBody(message);
                Model messageModel = MessageUtil.parseSerializedModel(rdfString, serialization);
                handleCreate(messageModel);
            }
            if (messageType.equals(IMessageBus.TYPE_DELETE)) {
                String rdfString = MessageUtil.getStringBody(message);
                Model messageModel = MessageUtil.parseSerializedModel(rdfString, serialization);
                handleDelete(messageModel);
            }
        }
    }

    private void handleDelete(Model messageModel) {
        try {
            TripletStoreAccessor.deleteModel(messageModel);

        } catch (ResourceRepositoryException e) {
            e.printStackTrace();
        } catch (InvalidModelException e) {
            e.printStackTrace();
        }
    }

    private void handleCreate(Model messageModel) {
        try {
            TripletStoreAccessor.addModel(messageModel);
        } catch (ResourceRepositoryException e) {
            e.printStackTrace();
        } catch (InvalidModelException e) {
            e.printStackTrace();
        }
    }

    private void handleGet(String serialization, String requestID) {
        Model federationModel = null;
        try {
            federationModel = TripletStoreAccessor.getInfrastructure();
        } catch (ResourceRepositoryException e) {
            Message message = MessageUtil.createErrorMessage(e.getMessage(), requestID, context);
            context.createProducer().send(topic, message);
            return;
        }

        Message message = MessageUtil.createRDFMessage(federationModel, IMessageBus.TYPE_INFORM, null, serialization, requestID, context);
        context.createProducer().send(topic, message);
    }

}