package recipeIntegration;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import recipeIntegration.DynamoStorage.RecipeHelperDao;
import recipeIntegration.DynamoStorage.RecipeHelperDynamoDbClient;
import recipeIntegration.DynamoStorage.RecipeHelper;
import recipeIntegration.DynamoStorage.RecipeHelperRecipeData;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

/**
 * Manages all intents from the speechlet
 * 
 */
public class RecipeHelperManager {

	private final RecipeHelperDao recipeHelperDao;
	private static final String LIST_OF_RECIPES = "recipe";
	private static final String AMAZON_NUMBER = "number";
	private static final String INGREDIENT_LIST = "ingredient";
	
	 /**
     * Creates and returns a new Manager session to run the speechlet
     *
     * @param AmazonDynamoDBClient
     * @return a new RecipeHelperManager session
     */
	public RecipeHelperManager(final AmazonDynamoDBClient amazonDynamoDbClient) {
		RecipeHelperDynamoDbClient dynamoDbClient = new RecipeHelperDynamoDbClient(
				amazonDynamoDbClient);

		recipeHelperDao = new RecipeHelperDao(dynamoDbClient);
	}
	 /**
     * Sets up a new recipe. Passes to RecipeSetup and attempts to connect to reicpe link
     * 	Also handles wrong recipe title input, errors in connection, etc.
     *
     * @param intent for this request
     * @param session for this request
     * @param recipeHelper recipe object, which may or may not be attached to customerID
     * 
     * @return response for a new recipe
     */
	public SpeechletResponse setUpNewRecipe(Session session, Intent intent,
			RecipeHelper recipe) {
		Slot RecipeNameSlot = intent.getSlot(LIST_OF_RECIPES);
		String recipeName = RecipeNameSlot.getValue();
	
		//String recipeName = "chocolate chip cookies"; //TODO REMOVE BEFORE REAL TESTING
		if (recipeName == null || recipeName == "") {
			String speechText = "What do you want me to help you cook?";
			return getAskSpeechletResponse(speechText, speechText);

		}
		try {
			RecipeSetup.RecipeBuilder(session, recipeName, recipe,
					recipeHelperDao); // if this returns null, re-prompt
		} catch (Exception e) {
			String speechText = "Sorry, I couldnt' connect to that recipe. Ask about a different recipe?";
			return getAskSpeechletResponse(speechText, speechText);
		}
		if (recipe.getAllSteps() == null) { //TODO does this ever get triggered?
			String speechText = "Sorry, I couldnt' find anything similar to that recipe. Ask about a different recipe?";
			return getAskSpeechletResponse(speechText, speechText);
		}
		
		recipeHelperDao.saveCurrentRecipe(recipe);
		String speechText = "Now cooking" + recipe.getRecipeName();
		return getTellSpeechletResponse(speechText);
	}
	 /**
     * Creates and returns response for the launch of the app
     *
     * @param LaunchRequest request for session
     * @param session for this request
     * 
     * @return response for launch
     */
	public SpeechletResponse getLaunchResponse(LaunchRequest request,
			Session session) {
		String speechText, repromptText;
		RecipeHelper recipe = recipeHelperDao.getCurrentSession(session);
		String recipeName = recipe.getRecipeName().replaceAll("\\p{Punct}+", "");
		
		if (recipe == null || !(recipe.hasURL())) { // no previous recipe
			speechText = "Welcome to Cooking Helper. Please ask about a recipe you would like to cook.";
			repromptText = "You can say How do I cook pancakes? or what is step 3 for quick and easy pizza crust";
		} else {
			speechText = "I see that we previously were cooking "
					+ recipeName; // recipe saved in Dynamo
			repromptText = "to start a new recipe, say New Reicpe. Otherwise, you can ask about "
					+ recipeName;
		}
		return getAskSpeechletResponse(speechText, repromptText);
	}
	
	
	 /**
     * creates and returns the response for NewRecipeIntent
     *
     * @param session for this request
     * 
     * 
     * @return response for reseting the recipe
     */
	public SpeechletResponse getNewRecipeIntent(Session session) {
		RecipeHelper recipe = RecipeHelper.newInstance(session,
				RecipeHelperRecipeData.newInstance(), 0, 0);
		recipeHelperDao.saveCurrentRecipe(recipe);

		return getAskSpeechletResponse("What would you like me to help you cook?", "ask about a recipe I can help you cook");

	}
	
	public SpeechletResponse getWhatNext (Session session, Intent intent){
		String outputSpeech = "Did you mean Next Step or Next Ingredient?";
		String repromptSpeech = "Please say Next Step or Next Ingredient";
		return getAskSpeechletResponse(outputSpeech, repromptSpeech );
	}
	
	
	 /**
     * Creates and returns response for the getIngredientInformation intent
     * Creates a new recipe if necessary
     *
     * @param intent for this request
     * @param session for this request
     * 
     * @return response for getting ingredient information 
     */
	public SpeechletResponse getIngredientInformation(Session session,
			Intent intent) {
		RecipeHelper recipe = recipeHelperDao.getCurrentSession(session);
		if (recipe == null || recipe.hasURL() == false) {
			recipe = RecipeHelper.newInstance(session,
					RecipeHelperRecipeData.newInstance(), 0, 0);
			setUpNewRecipe(session, intent, recipe);
		}
		// if a recipe already exists or was just set up after that message....
		Slot IngredientSlot = intent.getSlot(INGREDIENT_LIST); 
		
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		String outputText = ""; // initialize

		if (IngredientSlot != null && IngredientSlot.getValue() != null) { 

			String IngredientName = StringUtils.lowerCase(IngredientSlot.getValue());

			String TrueIngredient = recipe.getSpecificIngredient(IngredientName);

			if (TrueIngredient != null) { // it found specific ingredient
				outputText = ("You need " + TrueIngredient);

			} else {
				outputText = ("You don't need " + IngredientName);
			}
		} else {
			return getAskSpeechletResponse("I couldn't find that ingredient.",
					"Ask about a different ingredient");
		}
		outputSpeech.setText(outputText);
		return getTellSpeechletResponse(outputText);
	}
	
	
	/**
     * Creates and returns response for the getIngredientOverview intent
     *
     * @param intent for this request
     * @param session for this request
     * 
     * @return response for getting ingredient overview
     */
	public SpeechletResponse getIngredientOverview(Session session,
			Intent intent) {
		RecipeHelper recipe = recipeHelperDao.getCurrentSession(session);
		if (recipe == null || recipe.hasURL() == false) {
			recipe = RecipeHelper.newInstance(session,
					RecipeHelperRecipeData.newInstance(), 0, 0);
			setUpNewRecipe(session, intent, recipe);
		}
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		String outputText = "";
		List<String> ingredients = recipe.getAllIngredients();
		int i = 1;
		for (String ingredient : ingredients) {
			ingredient = ingredient.replaceAll("\\.", " ");
			System.out.println("Ingredient " + (Integer.toString(i)) + " is "
					+ ingredient.trim());
			outputText += "Ingredient " + (Integer.toString(i++)) + " is "
					+ ingredient + ".";	

		}
		outputSpeech.setText(outputText);
		return SpeechletResponse.newTellResponse(outputSpeech);

	}
	
	/**
     * Checks what ingredients have been heard and then returns the next 
     *
     * @param intent for this request
     * @param session for this request
     * 
     * @return the correct ingredient to be listed
     */
	public SpeechletResponse getNextIngredient(Session session, Intent intent){
		RecipeHelper recipe = recipeHelperDao.getCurrentSession(session);
		if (recipe == null || recipe.hasURL() == false) {
			recipe = RecipeHelper.newInstance(session,
					RecipeHelperRecipeData.newInstance(), 0, 0);
			setUpNewRecipe(session, intent, recipe);
		}
		List<String> ingredients = recipe.getAllIngredients();
		int IngrSize = ingredients.size(); //the number of ingredients there are
		int IngredientIndex = recipe.getIngredientIndex(); //index of what ingredient you've heard
		String CurrentIngredient = ingredients.get(IngredientIndex);
		if (IngredientIndex == 0){
			String outputSpeech = FirstIngredientResponse(CurrentIngredient, IngredientIndex, recipe);
			return getAskSpeechletResponse(outputSpeech, "you can ask for the next ingredient or any other questions");
		}
		if (IngredientIndex == (IngrSize - 1)){
			String outputSpeech = lastIngredientResponse(CurrentIngredient, IngredientIndex, recipe);
			return getAskSpeechletResponse(outputSpeech, "you can ask for the next ingredient or any other questions");
		}
		else{
			String outputSpeech = otherIngredientResponse(CurrentIngredient, IngredientIndex, recipe);
			return getAskSpeechletResponse(outputSpeech, "you can ask for the next ingredient or any other questions");
			
		}
	}
	
	/**
     * A series of methods to handle the different ingredient indexes and incraments as necessary.
     *
     * @param currentIngredient for this request
     * @param IngredientIndex the index of ingredients they have or haven't heard
     * @param recipe which holds the information for the session
     * 
     * @return the correct outputSpeech
     */
	public String FirstIngredientResponse(String CurrentIngredient, int IngredientIndex, RecipeHelper recipe){
		String outputSpeech = "the first ingredient is " + CurrentIngredient.trim();
		recipe.setIngredientIndex(++IngredientIndex);
		recipeHelperDao.saveCurrentRecipe(recipe);
		return outputSpeech;
	}
	public String lastIngredientResponse(String CurrentIngredient, int IngredientIndex, RecipeHelper recipe){
		String outputSpeech = "The next ingredient is " + CurrentIngredient.trim()+ ". " + "You've reached the end of the ingredient list, I'll reset that.";
		recipe.setIngredientIndex(0);
		recipeHelperDao.saveCurrentRecipe(recipe);
		return outputSpeech;
	}
	public String otherIngredientResponse(String CurrentIngredient, int IngredientIndex, RecipeHelper recipe){
		String outputSpeech = "The next ingredient is " + CurrentIngredient.trim();
		recipe.setIngredientIndex(++IngredientIndex);
		recipeHelperDao.saveCurrentRecipe(recipe);
		return outputSpeech;
	}
	
	
	/**
     * Checks what steps have been heard and then returns the next 
     *
     * @param intent for this request
     * @param session for this request
     * 
     * @return the correct step to be listed
     */
	public SpeechletResponse getNextStep(Session session, Intent intent){
		RecipeHelper recipe = recipeHelperDao.getCurrentSession(session);
		if (recipe == null || recipe.hasURL() == false) {
			recipe = RecipeHelper.newInstance(session,
					RecipeHelperRecipeData.newInstance(), 0, 0);
			setUpNewRecipe(session, intent, recipe);
		}
		List<String> steps = recipe.getAllSteps();
		int StepSize = steps.size(); //the number of ingredients there are
		int StepIndex = recipe.getStepIndex(); //index of what ingredient you've heard
		String CurrentStep = steps.get(StepIndex);
		CurrentStep = CurrentStep.replaceAll("\\[", "");
		if (StepIndex == 0){
			String outputSpeech = FirstStepResponse(CurrentStep, StepIndex, recipe);
			return getAskSpeechletResponse(outputSpeech, "you can ask for the next step or any other questions");
		}
		if (StepIndex == (StepSize - 1)){
			String outputSpeech = LastStepResponse(CurrentStep, StepIndex, recipe);
			return getAskSpeechletResponse(outputSpeech, "you can ask for the next step or any other questions");
		}
		else{
			String outputSpeech = OtherStepResponse(CurrentStep, StepIndex, recipe);
			return getAskSpeechletResponse(outputSpeech, "you can ask for the next step or any other questions");
			
		}
	}
	
	/**
     * A series of methods to handle the different step indexes and incraments as necessary.
     *
     * @param currentStep for this request
     * @param StepIndex the index of ingredients they have or haven't heard
     * @param recipe which holds the information for the session
     * 
     * @return the correct outputSpeech
     */
	public String FirstStepResponse(String CurrentStep, int StepIndex, RecipeHelper recipe){
		String outputSpeech = "the first step is " + CurrentStep.trim();
		recipe.setStepIndex(++StepIndex);
		recipeHelperDao.saveCurrentRecipe(recipe);
		return outputSpeech;
	}
	public String LastStepResponse(String CurrentStep, int StepIndex, RecipeHelper recipe){
		String outputSpeech = "The next step is " + CurrentStep.trim()+ ". " + "You've reached the end of the step list, I'll reset that.";
		recipe.setStepIndex(0);
		recipeHelperDao.saveCurrentRecipe(recipe);
		return outputSpeech;
	}
	public String OtherStepResponse(String CurrentStep, int StepIndex, RecipeHelper recipe){
		String outputSpeech = "The next Step is " + CurrentStep.trim();
		recipe.setStepIndex(++StepIndex);
		recipeHelperDao.saveCurrentRecipe(recipe);
		return outputSpeech;
	}
	
	/**
     * Creates and returns response for the getStepOverview intent
     *
     * @param intent for this request
     * @param session for this request
     * 
     * @return response for getting the step overview.
     */
	public SpeechletResponse getStepOverview(Session session, Intent intent) {
		RecipeHelper recipe = recipeHelperDao.getCurrentSession(session);
		if (recipe == null || recipe.hasURL() == false) {
			recipe = RecipeHelper.newInstance(session,
					RecipeHelperRecipeData.newInstance(), 0 , 0); //default ingredient index
			setUpNewRecipe(session, intent, recipe);
		}

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		String outputText = "";
		List<String> steps = recipe.getAllSteps();
		int i = 1;
		for (String step : steps) {
			step = step.replaceAll("\\.", "");
			step = step.replaceAll("[", "");
			System.out.println("Step " + (Integer.toString(i)) + " is " + step
					+ ".");
			outputText += "Step " + (Integer.toString(i++)) + " is " + step.trim() + "   ";

		}

		outputSpeech.setText(outputText);
		return SpeechletResponse.newTellResponse(outputSpeech);
	}
	
	/**
     * Creates and returns response for a secondary menu
     *
     * @param intent for this request
     * @param session for this request
     * 
     * @return affirming what recipe alexa is helping, as well as further options
     */
	public SpeechletResponse getSecondaryLaunchRequest(Session session, Intent intent){
		RecipeHelper recipe = RecipeHelper.newInstance(session, RecipeHelperRecipeData.newInstance(), 0, 0);
		Slot RecipeNameSlot = intent.getSlot(LIST_OF_RECIPES);
		String recipeName = RecipeNameSlot.getValue();
		
		try {
			RecipeSetup.RecipeBuilder(session, recipeName, recipe,
					recipeHelperDao); // if this returns null, re-prompt
		} catch (Exception e) {
			String speechText = "Sorry, I couldnt' connect to that recipe. Ask about a different recipe?";
			return getAskSpeechletResponse(speechText, speechText);
		}
		recipeHelperDao.saveCurrentRecipe(recipe);
		String outputSpeech = "now cooking " + recipe.getRecipeName();
		String repromptSpeech = "Ask me for the ingredients or the steps of this recipe";
		return getAskSpeechletResponse(outputSpeech, repromptSpeech);
		
	}
	
	
	/**
     * Creates and returns response for the getSpecificStep intent
     *
     * @param intent for this request
     * @param session for this request
     * 
     * @return response for getting a specific step
     */
	public SpeechletResponse getSpecificStep(Session session, Intent intent) {
		RecipeHelper recipe = recipeHelperDao.getCurrentSession(session);
		if (recipe == null || recipe.hasURL() == false) {
			System.out
					.println("SUCCESS...name is flagged as null. creating new instance and setting up.");
			recipe = RecipeHelper.newInstance(session,
					RecipeHelperRecipeData.newInstance(), 0, 0);
			setUpNewRecipe(session, intent, recipe);
		}
		Slot RequestedStep = intent.getSlot(AMAZON_NUMBER); 
		List<String> ListofSteps = recipe.getAllSteps();

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		String outputText = "";

		if (RequestedStep != null && RequestedStep.getValue() != null) { 
			String NumberName = RequestedStep.getValue(); 
			int StepNumber = Integer.parseInt(NumberName); 
			if (StepNumber > ListofSteps.size()) {
				outputText = ("The last step is step "
						+ Integer.toString(ListofSteps.size()) + " Please ask for a step within that range.");
			} else {
				String Direction = ListofSteps.get(StepNumber - 1); 
				Direction = Direction.replaceAll("\\[", "");
				outputText = ("Step " + StepNumber + " is " + Direction);

			}
		} else {
			outputText = ("I'm sorry, I couldn't find that step number. There are only "
					+ Integer.toString(ListofSteps.size()) + " steps.");
		}
		outputSpeech.setText(outputText);
		return SpeechletResponse.newTellResponse(outputSpeech);
	}

	
	
	private SpeechletResponse getAskSpeechletResponse(String speechText,
			String repromptText) {
		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("Session");
		card.setContent(speechText);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);

		// Create re-prompt
		PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
		repromptSpeech.setText(repromptText);
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(repromptSpeech);

		return SpeechletResponse.newAskResponse(speech, reprompt, card);
	}

	private SpeechletResponse getTellSpeechletResponse(String speechText) {
		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("Session");
		card.setContent(speechText);

		// Create the plain text output.
		PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
		speech.setText(speechText);

		return SpeechletResponse.newTellResponse(speech, card);
	}

}
