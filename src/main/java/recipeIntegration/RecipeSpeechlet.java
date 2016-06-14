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
    private static final String LIST_OF_INGREDIENTS = "ingredient";//THIS NEEDS TO BE FULL OF ALL INGREDIENTS
    private static final String AMAZON_NUMBER = "number";
    
    //private static final String LIST_OF_RECIPES = "recipe";
    
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
       

        String repromptText = "For instructions on what you can say, please say help me.";

  
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
        
        CurrentRecipe = RecipeSetup.RecipeBuilder();
       
        /*/
        try {
			CurrentRecipe = RecipeSetup.RecipeBuilder();
		} catch (FileNotFoundException e) {
			 throw new SpeechletException("I didn't find that recipe");
		}
		/*/

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

    
    private SpeechletResponse getIngredientInfoTEST(Recipe CurrentRecipe, String ingredient){
    	ArrayList<String> Ingredient_Test = new ArrayList<String>(); //{"flour", "salt", "baking powder", "butter", "brown sugar", "sugar", "egg", "vanilla extract", "chocolate chips"};
    	Ingredient_Test.add("flour");
    	Ingredient_Test.add("salt");
    	Ingredient_Test.add("baking powder");
    	Ingredient_Test.add("butter");
    	Ingredient_Test.add("brown sugar");
    	Ingredient_Test.add("sugar");
    	Ingredient_Test.add("egg");
    	Ingredient_Test.add("vanilla extract");
    	Ingredient_Test.add("chocolate chips");
    	
    	int IngredientLocation = Ingredient_Test.indexOf(ingredient);
    	if (IngredientLocation == -1){
    		System.out.println("that ingredient wasn't found");
    	}
    	else{
    		String IngredientName = Ingredient_Test.get(IngredientLocation);
    		System.out.println("ingredient found " + IngredientName);
    		Ingredient ingredientObj = CurrentRecipe.getIngredient(IngredientName);
    		System.out.println("ingredient OBJ found " + ingredientObj.getName());
    		
    	}
    	
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText("Yes, you need it ");

        return SpeechletResponse.newTellResponse(outputSpeech);
    }
    private SpeechletResponse getIngredientInformation(Recipe CurrentRecipe, Intent intent) {
        Slot IngredientSlot = intent.getSlot(LIST_OF_INGREDIENTS); 
        
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
    	String outputText = "";
        
        if (IngredientSlot != null && IngredientSlot.getValue() != null) { //yes, you found ingredient
            String IngredientName = IngredientSlot.getValue(); //this should hold the ingredient VALUE
            
            Ingredient ingredient = CurrentRecipe.getIngredient(IngredientName); //this should hold butter Ingredient
            
            if (ingredient != null) { //it found specific ingredient  
            	String IngredientQuantity = ingredient.getQuantity();
                outputText = ("You need " + IngredientQuantity);
                
            } 
            else {
                outputText = ("You don't need " + IngredientName);
            }
        } else {
            // There was no item in the intent so return the help prompt.
            return getHelp();
        }
        outputSpeech.setText(outputText);
        return SpeechletResponse.newTellResponse(outputSpeech);
    }
    
    private SpeechletResponse getStepOverview (Recipe CurrentRecipe, Intent intent){
    	int NumberofSteps = CurrentRecipe.getStepListSize();
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
    			outputText = ("That step is out of range. There are only " + CurrentRecipe.getStepListSize() + " steps.");
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
