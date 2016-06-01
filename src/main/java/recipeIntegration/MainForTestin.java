package recipeIntegration;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;

import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;


class MainForTestin{


	public static void notmain(String[] args) throws SpeechletException {
		Session newSession = Session.builder().withSessionId("123").build(); 
		LaunchRequest newLaunchRequest = LaunchRequest.builder().withRequestId("321").build();
		
		Intent realIntent = Intent.builder().withName("GetIngredientInformation").build();
		IntentRequest newIntent = IntentRequest.builder().withIntent(realIntent).withRequestId("345").build();
		RecipeSpeechlet newRecipe = new RecipeSpeechlet();
		SpeechletResponse LaunchOutput = newRecipe.onLaunch(newLaunchRequest, newSession);
		
		SpeechletResponse LaunchOnIntent = newRecipe.onIntent(newIntent, newSession);
		
	}



	}



