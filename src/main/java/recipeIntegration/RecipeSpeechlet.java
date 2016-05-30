package recipeIntegration;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
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

public class RecipeSpeechlet implements Speechlet{
	
	
	private static final Logger log = LoggerFactory.getLogger(RecipeSpeechlet.class);

    /**
     * The key to get the item from the intent.
     */
    private static final String ITEM_SLOT = "Ingredient";//THIS NEEDS TO BE FULL OF ALL INGREDIENTS
    
    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
       

        String speechOutput =
                "Welcome to Cooking Helper. You can ask a question like, "
                        + "what's the first step? or how much butter do I need? ... Now, what can I help you with?";
        // If the user either does not reply to the welcome message or says
        // something that is not understood, they will be prompted again with this text.
        System.out.println("you did this right so far");

        String repromptText = "For instructions on what you can say, please say help me.";

        // Here we are prompting the user for input
        return newAskResponse(speechOutput, repromptText);
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
        Recipe CurrentRecipe;
       
        try {
			CurrentRecipe = RecipeSetup.RecipeBuilder();
		} catch (FileNotFoundException e) {
			 throw new SpeechletException("I didn't find that recipe");
		}

        if ("GetIngredientOverview".equals(intentName)) { 

            return getIngredientOverview(CurrentRecipe, intent);
        } 
        else if ("GetIngredientInformation".equals(intentName)){
        	return getIngredientInformation(CurrentRecipe, intent);
        }
        else if ("GetStepList".equals(intentName)){
        	return getStepOverview(CurrentRecipe, intent);
        	
        }
       
        else if ("GetSpecificStep".equals(intentName)){
        	throw new NotImplementedException("This hasn't been set up yet");
        	
        }
        
        else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelp();
        } 
        else if ("AMAZON.StopIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye");

            return SpeechletResponse.newTellResponse(outputSpeech);
        } 
        else if ("AMAZON.CancelIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye");

            return SpeechletResponse.newTellResponse(outputSpeech);
        } 
        else {
            throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

    }

    private SpeechletResponse getIngredientInformation(Recipe CurrentRecipe, Intent intent) {
        Slot IngredientSlot = intent.getSlot(ITEM_SLOT); 
        if (IngredientSlot != null && IngredientSlot.getValue() != null) { //yes, you found ingredient
            String IngredientName = IngredientSlot.getValue();
            
            if (IngredientName != null) {
            	String IngredientQuantity = CurrentRecipe.findIngredientQuantity(IngredientName);
                PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
                outputSpeech.setText("Yes, you need " + IngredientQuantity);

                return SpeechletResponse.newTellResponse(outputSpeech);
                
            } else {
        
                String speechOutput =
                        "You don't need " + IngredientSlot 
                                + ". What else can I help with?";
                String repromptSpeech = "What else can I help with?";
                return newAskResponse(speechOutput, repromptSpeech);
            }
        } else {
            // There was no item in the intent so return the help prompt.
            return getHelp();
        }
    }
    
    private SpeechletResponse getStepOverview (Recipe CurrentRecipe, Intent intent){
    	int NumberofSteps = CurrentRecipe.getStepListSize();
    	ArrayList<Step> ListOfSteps = CurrentRecipe.getStepList();
    	PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
    	String outputText = "";
    	for (int i =0; i < NumberofSteps; i++){
    		Step CurrentStepObject = ListOfSteps.get(i);
    		outputText += ("Step " + (i + 1) + "is " + CurrentStepObject.getInstruction() + ". ");
    		
    		
    	}
        outputSpeech.setText(outputText);
        return SpeechletResponse.newTellResponse(outputSpeech);
    }
    
    private SpeechletResponse getIngredientOverview(Recipe CurrentRecipe, Intent intent){
    	int NumberofIngredients = CurrentRecipe.getIngredientListSize();
    	ArrayList<Ingredient> ListofIngredients = CurrentRecipe.getIngredientList();
    	PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
    	String outputText = "";
    	for (int i =0; i < NumberofIngredients; i++){
    		Ingredient CurrentIngredientObject = ListofIngredients.get(i);
    		outputText += ("Ingredient " + (i+1) + "is " + CurrentIngredientObject.getName() + ". ");
    		
    		
    	}
        outputSpeech.setText(outputText);
        return SpeechletResponse.newTellResponse(outputSpeech);
    }

    
    private SpeechletResponse getHelp() {
        String speechOutput =
                "You can ask questions about cooking a Chocolate Chip Cookie"
                        + "for example, ask for the steps to cook one, or "
                        + "ask about a quantity of an ingredient";
        String repromptText =
                "You can say things like, how much butter do I need?"
                        + " or, what's step 2?";
        return newAskResponse(speechOutput, repromptText);
    }

    
    private SpeechletResponse newAskResponse(String stringOutput, String repromptText) {
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(stringOutput);


        PlainTextOutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech();
        repromptOutputSpeech.setText(repromptText);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptOutputSpeech);

        return SpeechletResponse.newAskResponse(outputSpeech, reprompt);

    }
    
 

}
