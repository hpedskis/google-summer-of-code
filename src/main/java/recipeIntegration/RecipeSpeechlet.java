package recipeIntegration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;


/**
 * 
 * This class is the first called when a new session is started  by the User through AWS. The needed components,
 * the application ID, requestID, sessionID, and intent are all saved to create a session. The intent (request from
 * the user) is placed into one of the twelve recognized intents, which makes a call to RecipeHelperManager for 
 * the logic and response. 
 * 
 */
public class RecipeSpeechlet implements Speechlet {

	private static final Logger log = LoggerFactory
			.getLogger(RecipeSpeechlet.class);

	private AmazonDynamoDBClient amazonDynamoDBClient;

	private RecipeHelperManager recipeHelperManager;

	@Override
	// Once session starts, request and session ID are created
	public void onSessionStarted(final SessionStartedRequest request,
			final Session session) throws SpeechletException {
		log.info("onSessionStarted requestId={}, sessionId={}",
				request.getRequestId(), session.getSessionId());

		initializeComponents();

	}

	private void initializeComponents() {
		if (amazonDynamoDBClient == null) {
			amazonDynamoDBClient = new AmazonDynamoDBClient();

			recipeHelperManager = new RecipeHelperManager(amazonDynamoDBClient);

		}
	}

	@Override
	public SpeechletResponse onLaunch(final LaunchRequest request,
			final Session session) throws SpeechletException {

		log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
				session.getSessionId());

		//initializeComponents(); //TODO REMOVE BEFORE TESTING THROUGH AWS, BUT NEEDED FOR LOCAL TESTING
		return recipeHelperManager.getLaunchResponse(request, session);
	}

	@Override
	public SpeechletResponse onIntent(IntentRequest request, Session session)
			throws SpeechletException {
		log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
				session.getSessionId());

		initializeComponents(); // TODO needed for testing through AWS but remove for local testing

		Intent intent = request.getIntent();

		String intentName = (intent != null) ? intent.getName() : null;

		if ("GetIngredientOverview".equals(intentName)) {
			return recipeHelperManager.getIngredientOverview(session, intent);
			
		} else if ("ResetRecipe".equals(intentName)) {
			return recipeHelperManager.getNewRecipeIntent(session);
			
		} else if ("SecondMenu".equals(intentName)) {
		return recipeHelperManager.getSecondaryLaunchRequest(session, intent);
		
		} else if ("GetIngredientInformation".equals(intentName)) {
			return recipeHelperManager
					.getIngredientInformation(session, intent);
		
		} else if("GetNextIngredient".equals(intentName)){
			return recipeHelperManager.getNextIngredient(session, intent);
					
		} else if("GetNextStep".equals(intentName)){
			return recipeHelperManager.getNextStep(session, intent);
			
		} else if ("WhatNext".equals(intentName)){
			return recipeHelperManager.getWhatNext(session, intent);
			
		} else if ("GetStepList".equals(intentName)) {
			return recipeHelperManager.getStepOverview(session, intent);

		} else if ("GetSpecificStep".equals(intentName)) {
			return recipeHelperManager.getSpecificStep(session, intent);

		} else if ("AMAZON.HelpIntent".equals(intentName)) {
			return getHelp();
		} else if ("AMAZON.StopIntent".equals(intentName)) {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Goodbye");

			return SpeechletResponse.newTellResponse(outputSpeech);
		} else if ("AMAZON.CancelIntent".equals(intentName)) {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Goodbye");

			return SpeechletResponse.newTellResponse(outputSpeech);
		} else {
			throw new SpeechletException("Invalid Intent");
		}
	}

	@Override
	public void onSessionEnded(final SessionEndedRequest request,
			final Session session) throws SpeechletException {
		log.info("onSessionEnded requestId={}, sessionId={}",
				request.getRequestId(), session.getSessionId());

	}

	private SpeechletResponse getHelp() {
		String speechOutput = "You can ask questions about cooking any recipe"
				+ "for example, ask for step one of Spiced Pecans  or  "
				+ "ask about an ingredient for Chicken Honey Nut Stir Fry ";
		String repromptText = "You can say things like, how much butter do I need for Quick Peanut Butter Cookies ?"
				+ " or what is step 2 of Blackened Tuna?";
		return newAskResponse(speechOutput, repromptText);
	}

	private static SpeechletResponse newAskResponse(String stringOutput,
			String repromptText) {
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(stringOutput);

		PlainTextOutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech();
		repromptOutputSpeech.setText(repromptText);
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(repromptOutputSpeech);

		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);

	}

}
