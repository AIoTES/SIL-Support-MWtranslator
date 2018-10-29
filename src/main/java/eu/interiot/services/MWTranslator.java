package eu.interiot.services;

import org.apache.jena.rdf.model.Model;

import eu.interiot.message.Message;
import eu.interiot.message.MessageMetadata;
import eu.interiot.message.MessagePayload;
import eu.interiot.message.managers.URI.URIManagerMessageMetadata;
import eu.interiot.message.metadata.PlatformMessageMetadata;
import eu.interiot.services.syntax.FIWAREv2Translator;
import spark.Service;


public class MWTranslator {

    private int port;
    private Service spark;

    
    public MWTranslator(int port) {
        this.port = port;
    }

    public void start() throws Exception {
    	
        spark = Service.ignite().port(port);
        
        spark.post("translate/fiware", (request, response) -> {
        	// Translate fiware data to intermw JSON-LD
        	
            String platformResponse="";
                  
            Message callbackMessage = new Message();
	         try{
				 // Metadata
		         PlatformMessageMetadata metadata = new MessageMetadata().asPlatformMessageMetadata();
		         metadata.initializeMetadata();
		         metadata.addMessageType(URIManagerMessageMetadata.MessageTypesEnum.OBSERVATION);
//		         metadata.setSenderPlatformId(new EntityID(platform.getPlatformId()));
//		         metadata.setConversationId(conversationId);
		         callbackMessage.setMetadata(metadata);
		         
		         String body = request.body();
		         FIWAREv2Translator translator2 = new FIWAREv2Translator();
		         Model transformedModel = translator2.toJenaModelTransformed(body);
		
		         //Finish creating the message
		         MessagePayload messagePayload = new MessagePayload(transformedModel);
		         callbackMessage.setPayload(messagePayload);
		         
		         System.out.println(callbackMessage.serializeToJSONLD());
		         platformResponse = callbackMessage.serializeToJSONLD();
		
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


 
    
}
