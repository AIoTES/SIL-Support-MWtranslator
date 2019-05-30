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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import spark.Service;


public class MWTranslator {

    private int port;
    private Service spark;
    private final Logger logger = LoggerFactory.getLogger(MWTranslator.class);
    
    private final String FIWARE = "http://inter-iot.eu/FIWARE";
    private final String SOFIA = "http://inter-iot.eu/sofia2";
    private final String SOFIA_GAL = "http://inter-iot.eu/sofia2Gal";
    private final String UNIVERSAAL = "http://inter-iot.eu/UniversAAL";
    
    public MWTranslator(int port) {
        this.port = port;
    }

    public void start() throws Exception {
    	
        spark = Service.ignite().port(port);
        
        // FIWARE
        
        // Translate Fiware data to intermw JSON-LD
        spark.post("fiware/translate", (request, response) -> {
        	
            String platformResponse="";
            
	         try{
				 // Translate data to JSON-LD
		         String body = request.body();
		         logger.debug("Translate data from Fiware...  ");
		         FIWAREv2Translator translator2 = new FIWAREv2Translator();
		         Model transformedModel = translator2.toJenaModelTransformed(body);
		
		         // Create Inter-IoT message
		 	     platformResponse = createObservationMessage(transformedModel);
		
	         } catch(Exception e){
	        	 response.status(400);
	             return e.getMessage();
	         }
	            
	         response.header("Content-Type", "application/json;charset=UTF-8");
	         response.status(200);
	         return platformResponse;
        });
        
        // Translate from inter-IoT JSON-LD to Fiware
        spark.post("fiware/formatx", (request, response) -> {
        	
            String platformResponse="";
            
	         try{
		         String body = request.body();
		         logger.debug("Translate data from inter-IoT to Fiware...  ");
		         FIWAREv2Translator translator = new FIWAREv2Translator();
		         Message message = new Message(body);
		         // Translate JSON-LD message to Fiware format
		         platformResponse = translator.toFormatX(message.getPayload().getJenaModel());
		
	         } catch(Exception e){
	        	 response.status(400);
	             return e.getMessage();
	         }
	            
	         response.header("Content-Type", "application/json;charset=UTF-8");
	         response.status(200);
	         return platformResponse;
        });
        
        // Get platform type label for Fiware
        spark.get("fiware/type", (request, response) -> {
        	
            String types = "{\"types\":[\"" + FIWARE + "\"]}";
                
	        response.header("Content-Type", "application/json;charset=UTF-8");
	        response.status(200);
	        return types;
        });
        
        // UNIVERSAAL
        
        // Translate universAAL data to intermw JSON-LD
        spark.post("universaal/translate", (request, response) -> {	
        	
            String platformResponse="";
            
	         try{
		         // Transform data to JSON-LD
		         String event = request.body();
		         logger.debug("Translate data from universAAL...  ");
		 	     Model eventModel = ModelFactory.createDefaultModel();
		 	     eventModel.read(new ByteArrayInputStream(event.getBytes()), null, "TURTLE");
		 	     
		 	     // Create Inter-IoT message
		 	     platformResponse = createObservationMessage(eventModel);
		
	         } catch(Exception e){
	        	 response.status(400);
	             return e.getMessage();
	         }
                
	         response.header("Content-Type", "application/json;charset=UTF-8");
	         response.status(200);
	         return platformResponse;
        });
        
        // Translate from inter-IoT JSON-LD to universAAL
        spark.post("universaal/formatx", (request, response) -> {
        	
            String platformResponse="";
            
	         try{
		         String body = request.body();
		         logger.debug("Translate data from inter-IoT to universAAL...  ");
		         Message message = new Message(body);
		         
		         // Translate JSON-LD message to universAAL format
		         Model event = message.getPayload().getJenaModel();
		     	 Writer turtle = new StringWriter();
		     	 event.write(turtle, "TURTLE");
		     	 platformResponse = turtle.toString();
		     	 turtle.close();
		
	         } catch(Exception e){
	        	 response.status(400);
	             return e.getMessage();
	         }
	            
	         response.header("Content-Type", "text/plain;charset=UTF-8");
	         response.status(200);
	         return platformResponse;
        });
        
        // Get platform type label for universAAL
        spark.get("universaal/type", (request, response) -> {
        	
            String types = "{\"types\":[\"" + UNIVERSAAL + "\"]}";
                
	        response.header("Content-Type", "application/json;charset=UTF-8");
	        response.status(200);
	        return types;
        });
        
        // SOFIA2
        
        // Translate SOFIA2 data to intermw JSON-LD
        spark.post("sofia/translate", (request, response) -> {
        	
            String platformResponse="";
            
	         try{
				 // Translate data to JSON-LD
		         String body = request.body();
		         logger.debug("Translate data from SOFIA2...  ");
		         Sofia2Translator translator = new Sofia2Translator();
		         Model transformedModel = translator.toJenaModelTransformed(body);
		
		         // Create Inter-IoT message
		 	     platformResponse = createObservationMessage(transformedModel);
		
	         } catch(Exception e){
	        	 response.status(400);
	             return e.getMessage();
	         }
	            
	         response.header("Content-Type", "application/json;charset=UTF-8");
	         response.status(200);
	         return platformResponse;
        });
        
        // Translate from inter-IoT JSON-LD to SOFIA2
        spark.post("sofia/formatx", (request, response) -> {
        	
            String platformResponse="";
            
	         try{
		         String body = request.body();
		         logger.debug("Translate data from inter-IoT to SOFIA2...  ");
		         
		         Sofia2Translator translator = new Sofia2Translator();
		         Message message = new Message(body);
		         // Translate JSON-LD message to SOFIA2 format
		         platformResponse = translator.toFormatX(message.getPayload().getJenaModel());
		
	         } catch(Exception e){
	        	 response.status(400);
	             return e.getMessage();
	         }
	            
	         response.header("Content-Type", "application/json;charset=UTF-8");
	         response.status(200);
	         return platformResponse;
        });
        
        // Get platform type label for SOFIA2
        spark.get("sofia/type", (request, response) -> {
        	
            String types = "{\"types\":[\"" + SOFIA + "\",\"" + SOFIA_GAL + "\"]}";
                
	        response.header("Content-Type", "application/json;charset=UTF-8");
	        response.status(200);
	        return types;
        });
        
    }

    public void stop() {
        spark.stop();
    }

    public static void main(String[] args) throws Exception {
    	int port = 4568;
    	if (args.length > 0){
    		port = Integer.parseInt(args[0]);
    	}
    	new MWTranslator(port).start();
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
//        ObjectNode context = (ObjectNode) jsonMessage.get("@context");
        // TODO: to use @vocab instead of msg, replace also all "msg:XXX" tags by "InterIoT:message/XXX"
//        context.remove("msg");
//        context.put("@vocab", "http://inter-iot.eu/message/");
//        jsonMessage.set("@context", context);
        return jsonMessage.toString();
    	
    }
 
    
}
