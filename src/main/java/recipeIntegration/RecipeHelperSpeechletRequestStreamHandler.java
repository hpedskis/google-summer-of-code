package recipeIntegration;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public final class RecipeHelperSpeechletRequestStreamHandler extends //THIS IS COMPLETE
SpeechletRequestStreamHandler{
	private static final Set<String> supportedApplicationIds;

    static {
        System.setProperty("com.amazon.speech.speechlet.servlet.disableRequestSignatureCheck", "true");
    	
        supportedApplicationIds = new HashSet<String>();
        supportedApplicationIds.add("amzn1.echo-sdk-ams.app.5c367d99-3850-4c8c-8774-0c9ecfd1c80e");
    }
    

    public RecipeHelperSpeechletRequestStreamHandler() {
        super(new RecipeSpeechlet(), supportedApplicationIds);
    }

}
