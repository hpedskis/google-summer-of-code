package recipeIntegration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RecipeLambda extends SpeechletLambda{
	
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
    	
        System.setProperty("com.amazon.speech.speechlet.servlet.disableRequestSignatureCheck", "true");
    	
    }
    
    public RecipeLambda() {
    	this.setSpeechlet(new RecipeSpeechlet());
        
    }
    
    @Override
    public void handleRequest(InputStream inputStream,
            OutputStream outputStream, Context context) throws IOException
    {
        super.handleRequest(inputStream, outputStream, context);
    }

}
