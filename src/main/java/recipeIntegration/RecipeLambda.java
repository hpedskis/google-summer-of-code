package recipeIntegration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import com.amazonaws.services.lambda.runtime.Context;


/**
 * The class is used for all the initial set up of the program.
 * It creates a new ReicpeSpeechlet, so intents can be heard and processed.
 *
 */
public class RecipeLambda extends SpeechletLambda {

	//private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@SuppressWarnings("unused")
	private static final Set<String> supportedApplicationIds;
	static {
		supportedApplicationIds = new HashSet<String>();
		System.setProperty(
				"com.amazon.speech.speechlet.servlet.disableRequestSignatureCheck",
				"true");

	}

	public RecipeLambda() {
		this.setSpeechlet(new RecipeSpeechlet());

	}

	@Override
	public void handleRequest(InputStream inputStream,
			OutputStream outputStream, Context context) throws IOException {
		super.handleRequest(inputStream, outputStream, context);
	}

}
