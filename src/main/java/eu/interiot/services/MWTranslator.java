package eu.interiot.services;

import eu.interiot.message.Message;
import eu.interiot.message.MessageMetadata;
import eu.interiot.message.MessagePayload;
import eu.interiot.message.managers.URI.URIManagerMessageMetadata;
import eu.interiot.message.metadata.PlatformMessageMetadata;
import eu.interiot.services.syntax.FIWAREv2Translator;
import eu.interiot.services.syntax.Sofia2Translator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
        
        // FIWARE
        
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
        
        // Translate from inter-IoT JSON-LD to Fiware
        spark.post("formatx/fiware", (request, response) -> {
        	
            String platformResponse="";
            
	         try{
		         String body = request.body();
		         logger.debug("Translate data from inter-IoT to Fiware...  " + body);
		         FIWAREv2Translator translator = new FIWAREv2Translator();
		         Message message = new Message(body);
		         // Translate JSON-LD message to Fiware format
		         platformResponse = translator.toFormatX(message.getPayload().getJenaModel());
		         
		         System.out.println(platformResponse);
		
	         } catch(Exception e){
	        	 response.status(400);
	             return e.getMessage();
	         }
	            
	         response.header("Content-Type", "application/json;charset=UTF-8");
	         response.status(200);
	         return platformResponse;
        });
        
        
        // UNIVERSAAL
        
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
        
        // Translate from inter-IoT JSON-LD to universAAL
        spark.post("formatx/universaal", (request, response) -> {
        	
            String platformResponse="";
            
	         try{
		         String body = request.body();
		         logger.debug("Translate data from inter-IoT to universAAL...  " + body);
		         Message message = new Message(body);
		         
		         // Translate JSON-LD message to universAAL format
		         Model event = message.getPayload().getJenaModel();
		     	 Writer turtle = new StringWriter();
		     	 event.write(turtle, "TURTLE");
		     	 platformResponse = turtle.toString();
		     	 turtle.close();
		         
		         System.out.println(platformResponse);
		
	         } catch(Exception e){
	        	 response.status(400);
	             return e.getMessage();
	         }
	            
	         response.header("Content-Type", "text/plain;charset=UTF-8");
	         response.status(200);
	         return platformResponse;
        });
        
        
        // SOFIA2
        
        // Translate SOFIA2 data to intermw JSON-LD
        spark.post("translate/sofia", (request, response) -> {
        	
            String platformResponse="";
            
	         try{
				 // Translate data to JSON-LD
		         String body = request.body();
		         logger.debug("Translate data from SOFIA2...  " + body);
		         Sofia2Translator translator = new Sofia2Translator();
		         Model transformedModel = translator.toJenaModelTransformed(body);
		
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
        
        // Translate from inter-IoT JSON-LD to SOFIA2
        spark.post("formatx/sofia", (request, response) -> {
        	
            String platformResponse="";
            
	         try{
		         String body = request.body();
		         logger.debug("Translate data from inter-IoT to SOFIA2...  " + body);
		         
		         Sofia2Translator translator = new Sofia2Translator();
		         Message message = new Message(body);
		         // Translate JSON-LD message to SOFIA2 format
		         platformResponse = translator.toFormatX(message.getPayload().getJenaModel());
		         
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
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonMessage = (ObjectNode) mapper.readTree(callbackMessage.serializeToJSONLD());
        ObjectNode context = (ObjectNode) jsonMessage.get("@context");
        context.remove("msg");
        context.put("@vocab", "http://inter-iot.eu/message/");
        jsonMessage.set("@context", context);
        return jsonMessage.toString();
    	
    }
 
    
}
