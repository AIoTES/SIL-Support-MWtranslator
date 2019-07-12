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
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

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
            FIWAREv2Translator translator2 = new FIWAREv2Translator();
            
	         try{
				 // Translate data to JSON-LD
		         String body = request.body();
		         logger.debug("Translate data from Fiware...  ");
		         
		         JsonParser parser = new JsonParser();
		         JsonElement element = parser.parse(body);
		         		         
		         if(element instanceof JsonArray){
		        	// Array translation
		        	 Gson gson = new Gson();
		        	 JsonArray input = element.getAsJsonArray();
		        	 JsonArray output = new JsonArray();
		        	 for(int i=0; i<input.size(); i++){
		        		 Model transformedModel = translator2.toJenaModelTransformed(input.get(i).getAsJsonObject().toString());
				     		
				         // Create Inter-IoT message
		        		 String observation = createObservationMessage(transformedModel);
		        		 // Add message to output array
				 	     output.add(parser.parse(observation).getAsJsonObject());
		        	 }
		        	 platformResponse = gson.toJson(output);
		         }else{
		        	// Single element translation
		        	 Model transformedModel = translator2.toJenaModelTransformed(body);
		     		
			         // Create Inter-IoT message
			 	     platformResponse = createObservationMessage(transformedModel);
		         }            
		
	         } catch(Exception e){
	        	 response.status(400);
	        	 e.printStackTrace();
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
		         
		         JsonParser parser = new JsonParser();
		         JsonElement element = parser.parse(body);
		         		         
		         if(element instanceof JsonArray){
		        	// Array translation
		        	 Gson gson = new Gson();
		        	 JsonArray input = element.getAsJsonArray();
		        	 JsonArray output = new JsonArray();
		        	 for(int i=0; i<input.size(); i++){
		        		 Message message = new Message(input.get(i).getAsJsonObject().toString());
		        		 String translatedData = translator.toFormatX(message.getPayload().getJenaModel());	
				         
		        		 // Add message to output array
				 	     output.add(parser.parse(translatedData).getAsJsonObject());
		        	 }
		        	 platformResponse = gson.toJson(output);
		        	 
		         }else{
		        	// Single element translation
		        	 Message message = new Message(body);
			         // Translate JSON-LD message to Fiware format
			         platformResponse = translator.toFormatX(message.getPayload().getJenaModel());
		         }
		        		
	         } catch(Exception e){
	        	 response.status(400);
	        	 e.printStackTrace();
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
		         
		         JsonParser parser = new JsonParser();
		         
		         try{
		        	 JsonElement element = parser.parse(event);
		        	 // Array translation
		        	 Gson gson = new Gson();
		        	 JsonArray input = element.getAsJsonArray();
		        	 JsonArray output = new JsonArray();
		        	 for(int i=0; i<input.size(); i++){
		        		 Model eventModel = ModelFactory.createDefaultModel();
				 	     eventModel.read(new ByteArrayInputStream(input.get(i).getAsString().getBytes()), null, "TURTLE");
				 	     
				         // Create Inter-IoT message
		        		 String observation = createObservationMessage(eventModel);
		        		 // Add message to output array
				 	     output.add(parser.parse(observation).getAsJsonObject());
		        	 }
		        	 platformResponse = gson.toJson(output);
		         }catch(com.google.gson.JsonSyntaxException ex){
		        	// Single element translation
		        	 Model eventModel = ModelFactory.createDefaultModel();
			 	     eventModel.read(new ByteArrayInputStream(event.getBytes()), null, "TURTLE");
			 	     
			 	     // Create Inter-IoT message
			 	     platformResponse = createObservationMessage(eventModel);
		         }
		         
		         		         
//		         if(element instanceof JsonArray){
//		        	// Array translation
//		        	
//		         }else{
//		        	
//		         }
		
	         } catch(Exception e){
	        	 response.status(400);
	        	 e.printStackTrace();
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
		         
		         JsonParser parser = new JsonParser();
		         JsonElement element = parser.parse(body);
		         		         
		         if(element instanceof JsonArray){
		        	// Array translation
		        	 Gson gson = new Gson();
		        	 JsonArray input = element.getAsJsonArray();
		        	 JsonArray output = new JsonArray();
		        	 for(int i=0; i<input.size(); i++){
		        		 Message message = new Message(input.get(i).getAsJsonObject().toString());
				         
				         // Translate JSON-LD message to universAAL format
				         Model event = message.getPayload().getJenaModel();
				         Writer turtle = new StringWriter();
				     	 event.write(turtle, "TURTLE");
				     	 platformResponse = turtle.toString();
		        		 String translatedData = turtle.toString();	
		        		 turtle.close();
		        		 
		        		 // Add message to output array
				 	     output.add(translatedData);
		        	 }
		        	 platformResponse = gson.toJson(output);
		        	 response.header("Content-Type", "application/json;charset=UTF-8");
		         }else{
		        	// Single element translation
		        	 Message message = new Message(body);
			         
			         // Translate JSON-LD message to universAAL format
			         Model event = message.getPayload().getJenaModel();
			         Writer turtle = new StringWriter();
			     	 event.write(turtle, "TURTLE");
			     	 platformResponse = turtle.toString();
			     	 turtle.close();
			     	 response.header("Content-Type", "text/plain;charset=UTF-8");
		         }
		        	
	         } catch(Exception e){
	        	 response.status(400);
	        	 e.printStackTrace();
	             return e.getMessage();
	         }
	         
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
		         
		         JsonParser parser = new JsonParser();
		         JsonElement element = parser.parse(body);
		         		         
		         if(element instanceof JsonArray){
		        	// Array translation
		        	 Gson gson = new Gson();
		        	 JsonArray input = element.getAsJsonArray();
		        	 JsonArray output = new JsonArray();
		        	 for(int i=0; i<input.size(); i++){
		        		 Model transformedModel = translator.toJenaModelTransformed(input.get(i).getAsJsonObject().toString());
				     		
				         // Create Inter-IoT message
		        		 String observation = createObservationMessage(transformedModel);
		        		 // Add message to output array
				 	     output.add(parser.parse(observation).getAsJsonObject());
		        	 }
		        	 platformResponse = gson.toJson(output);
		         }else{
		        	// Single element translation
		        	 Model transformedModel = translator.toJenaModelTransformed(body);
		     		
			         // Create Inter-IoT message
			 	     platformResponse = createObservationMessage(transformedModel);
		         }
		         	
	         } catch(Exception e){
	        	 response.status(400);
	        	 e.printStackTrace();
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
		         
		         JsonParser parser = new JsonParser();
		         JsonElement element = parser.parse(body);
		         		         
		         if(element instanceof JsonArray){
		        	// Array translation
		        	 Gson gson = new Gson();
		        	 JsonArray input = element.getAsJsonArray();
		        	 JsonArray output = new JsonArray();
		        	 for(int i=0; i<input.size(); i++){
		        		 Message message = new Message(input.get(i).getAsJsonObject().toString());
		        		 String translatedData = translator.toFormatX(message.getPayload().getJenaModel());	
				         
		        		 // Add message to output array
				 	     output.add(parser.parse(translatedData).getAsJsonObject());
		        	 }
		        	 platformResponse = gson.toJson(output);
		        	 
		         }else{
		        	// Single element translation
		        	 Message message = new Message(body);
			         // Translate JSON-LD message to SOFIA2 format
			         platformResponse = translator.toFormatX(message.getPayload().getJenaModel());
		         }
		
	         } catch(Exception e){
	        	 response.status(400);
	        	 e.printStackTrace();
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
