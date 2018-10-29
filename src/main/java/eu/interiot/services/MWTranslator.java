package eu.interiot.services;

import eu.interiot.message.Message;
import eu.interiot.message.MessageMetadata;
import eu.interiot.message.MessagePayload;
import eu.interiot.message.managers.URI.URIManagerMessageMetadata;
import eu.interiot.message.metadata.PlatformMessageMetadata;
import eu.interiot.services.syntax.FIWAREv2Translator;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.Service;


public class MWTranslator {

    private int port;
    private Service spark;
    private final Logger logger = LoggerFactory.getLogger(MWTranslator.class);
    
    public MWTranslator(int port) {
        this.port = port;
    }

    public void start() throws Exception {
    	
        spark = Service.ignite().port(port);
        
        // Translate Fiware data to intermw JSON-LD
        spark.post("translate/fiware", (request, response) -> {
        	
            String platformResponse="";
            
	         try{
				 // Translate data to JSON-LD
		         String body = request.body();
		         logger.debug("Translate data from Fiware...  " + body);
		         FIWAREv2Translator translator2 = new FIWAREv2Translator();
		         Model transformedModel = translator2.toJenaModelTransformed(body);
		
		         // Create Inter-IoT message
		 	     platformResponse = createObservationMessage(transformedModel);
		         System.out.println(platformResponse);
		
	         } catch(Exception e){
	        	 response.status(400);
	             return e.getMessage();
	         }
	            
	         response.header("Content-Type", "application/json;charset=UTF-8");
	         response.status(200);
	         return platformResponse;
        });
        
        // Translate universAAL data to intermw JSON-LD
        spark.post("translate/universaal", (request, response) -> {	
        	
            String platformResponse="";
            
	         try{
		         // Transform data to JSON-LD
		         String event = request.body();
		         logger.debug("Translate data from universAAL...  " + event);
		 	     Model eventModel = ModelFactory.createDefaultModel();
		 	     eventModel.read(new ByteArrayInputStream(event.getBytes()), null, "TURTLE");
		 	     
		 	     // Create Inter-IoT message
		 	     platformResponse = createObservationMessage(eventModel);
		         System.out.println(platformResponse);
		
	         } catch(Exception e){
	        	 response.status(400);
	             return e.getMessage();
	         }
                
	         response.header("Content-Type", "application/json;charset=UTF-8");
	         response.status(200);
	         return platformResponse;
        });
        

    }

    public void stop() {
        spark.stop();
    }

    public static void main(String[] args) throws Exception {
    	new MWTranslator(4568).start();
    }

    private String createObservationMessage(Model model) throws IOException{
    	
    	Message callbackMessage = new Message();
    	// Metadata
        PlatformMessageMetadata metadata = new MessageMetadata().asPlatformMessageMetadata();
        metadata.initializeMetadata();
        metadata.addMessageType(URIManagerMessageMetadata.MessageTypesEnum.OBSERVATION);
//        metadata.setSenderPlatformId(new EntityID(platform.getPlatformId()));
//        metadata.setConversationId(conversationId);
        callbackMessage.setMetadata(metadata);
        
        //Finish creating the message
        MessagePayload messagePayload = new MessagePayload(model);
        callbackMessage.setPayload(messagePayload);    
    	
    	return callbackMessage.serializeToJSONLD();
    	
    }
 
    
}
