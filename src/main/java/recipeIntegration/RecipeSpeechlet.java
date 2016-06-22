package recipeIntegration;

import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
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
	//ties slots to variables. To test locally, each must be hardcoded, as it can't search slot values.
    private static final String LIST_OF_RECIPES = "recipe";
    private static final String AMAZON_NUMBER = "number";
    private static final String INGREDIENT_LIST = "ingredient";
    
    
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
                        + "how do I make Chicken Wraps, or What's step 2 for Rosemary-Ginger Cocktail ... Now, what can I help you with?";
       

        String repromptText = "For instructions on what you can say, please say help me.";

  
        return newAskResponse(speechOutput, repromptText);
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session) throws SpeechletException { 
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
       
        Slot RecipeNameSlot = intent.getSlot(LIST_OF_RECIPES);
        String recipeName =  RecipeNameSlot.getValue(); //	is this going to work?!?! TODO IGNORE CASE
        //String recipeName = "chinese style baby bok choy with mushroom sauce"; //for manual testing
        StringUtils.lowerCase(recipeName); //change to all lower case
        if (recipeName == null || recipeName == ""){
        	PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Passed in recipe slot was null or blank. Please try again");
        	
        }
        Recipe CurrentRecipe = null;
        try {
			CurrentRecipe = RecipeSetup.RecipeBuilder(recipeName); //if this returns null, re-prompt
		} catch (Exception e) {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("There was a problem connecting to that recipe link. Please try again.");
            return SpeechletResponse.newTellResponse(outputSpeech);
		}
        if (CurrentRecipe == null){
        	PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        	String outputText = "I couldn't find that recipe, please ask about a different one.";
        	outputSpeech.setText(outputText);
        	return SpeechletResponse.newTellResponse(outputSpeech);
        }
        
        if ("GetIngredientOverview".equals(intentName)) { 
        	System.out.println(CurrentRecipe.getName());
            return getIngredientOverview(CurrentRecipe, intent);
        } 
        else if ("GetIngredientInformation".equals(intentName)){
        	return getIngredientInformation(CurrentRecipe, intent);

        }
        else if ("GetStepList".equals(intentName)){
        	return getStepOverview(CurrentRecipe, intent);
        	
        }
       
        else if ("GetSpecificStep".equals(intentName)){
        	return getSpecificStep(CurrentRecipe, intent);
        	
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
        Slot IngredientSlot = intent.getSlot(INGREDIENT_LIST); //TODO fix this. 
        
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
    	String outputText = "";
        
        if (IngredientSlot != null && IngredientSlot.getValue() != null) { //yes, you found ingredient
    
            String IngredientName = IngredientSlot.getValue();
            StringUtils.lowerCase(IngredientName);
            
            Ingredient ingredient = CurrentRecipe.getIngredient(IngredientName); //this should hold butter Ingredient
            
            if (ingredient != null) { //it found specific ingredient  
            	String IngredientQuantity = ingredient.getQuantity();
                outputText = ("You need " + IngredientQuantity);
                
            } 
            else {
                outputText = ("You don't need " + IngredientName);
            }
        } else {
            outputText = "I couldn't find that ingredient";
        }
        outputSpeech.setText(outputText);
        return SpeechletResponse.newTellResponse(outputSpeech);
    }
    
    private SpeechletResponse getStepOverview (Recipe CurrentRecipe, Intent intent){
    	int NumberofSteps = CurrentRecipe.getStepListSize();
    	System.out.println(NumberofSteps);
    	ArrayList<Step> ListOfSteps = CurrentRecipe.getStepList();
    	PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
    	String outputText = "";
    	for (int i =0; i < NumberofSteps; i++){
    		Step CurrentStepObject = ListOfSteps.get(i);
    		outputText += ("Step " + (i + 1) + " is " + CurrentStepObject.getInstruction() + ". ");
    		System.out.println("Step " + (i + 1) + " is " + CurrentStepObject.getInstruction() + ". ");
    		
    	}
        outputSpeech.setText(outputText);
        return SpeechletResponse.newTellResponse(outputSpeech);
    }
    
    private SpeechletResponse getSpecificStep(Recipe CurrentRecipe, Intent intent){
    	Slot NumberofStep = intent.getSlot(AMAZON_NUMBER);
    	ArrayList<Step> ListofSteps = CurrentRecipe.getStepList();
    	
    	PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
    	String outputText = "";
    	
    	if (NumberofStep != null && NumberofStep.getValue() != null) { //found slot and found name value
    		String NumberName = NumberofStep.getValue(); //save string name
    		int StepNumber = Integer.parseInt(NumberName); //convert to number
    		if (StepNumber > CurrentRecipe.getStepListSize()){
    			outputText = ("The last step is step " + CurrentRecipe.getStepListSize() + " Please ask for a step within that range.");
    		}
    		else{
    			Step requestedStep = ListofSteps.get(StepNumber - 1); //proper index for the requested step
    			outputText = ("Step " + StepNumber + " is " + requestedStep.getInstruction());
    			
    		}	
    	}
    	else{
    		outputText = ("I'm sorry, I couldn't find that step number. There are only " + CurrentRecipe.getStepListSize() + " steps.");	
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
    		outputText += ("Ingredient " + (i+1) + " is " + CurrentIngredientObject.getName() + ". ");
    		System.out.println("Ingredient " + (i+1) + " is " + CurrentIngredientObject.getName() + ". "); 
    		
    		
    	}
        outputSpeech.setText(outputText);
        return SpeechletResponse.newTellResponse(outputSpeech);
    	
}

    
    private SpeechletResponse getHelp() {
        String speechOutput =
                "You can ask questions about cooking any recipe"
                        + "for example, ask what step one of Spiced Pecans is or  "
                        + "ask about an ingredient for Chicken Honey Nut Stir Fry ";
        String repromptText =
                "You can say things like, how much butter do I need for Quick Peanut Butter Cookies ?"
                        + " or, what's step 2 of Blackened Tuna?";
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
