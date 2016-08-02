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
 * Manages all intents (requests from the user) passed in from RecipeSpeechlet. 
 * 
 * All logic is done here and Alexa's outputs are created for setting up a new recipe,
 * getting the launch request, reseting a recipe, correcting mistakes in intents of further steps/ingredients,
 * getting ingredient quantity information, getting all ingredients, getting the next ingredient, getting the 
 * next step, getting all the steps, getting a secondary menu, or getting any specific steps.
 * 
 * Methods to create either ask responses (where Alexa continues to listen) or tell responses (which end the session)
 * are also in this class. 
 * 
 */
public class RecipeHelperManager {

	private final RecipeHelperDao RECIPE_HELPER_DAO;
	
	//These final strings are matched to the Custom Slot Types, set up in Amazon Developer Portal
	private static final String LIST_OF_RECIPES = "recipe";
	private static final String AMAZON_NUMBER = "number";
	private static final String INGREDIENT_LIST = "ingredient";
	
	 /**
     * Creates and returns a new Manager session to run RecipeSpeechlet
     *
     * @param AmazonDynamoDBClient
     * @return a new RecipeHelperManager session
     */
	public RecipeHelperManager(final AmazonDynamoDBClient amazonDynamoDbClient) {
		RecipeHelperDynamoDbClient dynamoDbClient = new RecipeHelperDynamoDbClient(
				amazonDynamoDbClient);
		RECIPE_HELPER_DAO = new RecipeHelperDao(dynamoDbClient);
	}
	
	
	 /**
     * Sets up a new recipe. This method calls the RecipeSetup class to connect to the recipe and
     * gather all recipe information (ingredients, name, steps).
     * 
     * Also handles wrong recipe title input, errors in connection, etc.
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
		//String recipeName = "chocolate chip cookies"; //TODO USED FOR LOCAL TESTING. CHANGE VALUE HERE.
		
		if (recipeName == null || recipeName == "") {
			String speechText = "Say 'Help me cook' and then a recipe you want to cook";
			return getAskSpeechletResponse(speechText, speechText);
		}
		try {
			RecipeSetup.RecipeBuilder(session, recipeName, recipe,
					RECIPE_HELPER_DAO); // if this returns null, re-prompt
		} catch (Exception e) {
			String speechText = "Sorry, I couldn't find that recipe or there was a problem connecting to it. Ask me to help you cook a different recipe?";
			return getAskSpeechletResponse(speechText, speechText);
		}
		
		if (recipe.getAllSteps() == null) { //one more back up check to make sure recipe correctly transfered.
			String speechText = "Sorry, I couldnt' find anything similar to that recipe. Ask me to help you cook a different recipe?";
			return getAskSpeechletResponse(speechText, speechText);
		}
		
		RECIPE_HELPER_DAO.saveCurrentRecipe(recipe);
		String speechText = "Now cooking" + recipe.getRecipeName(); //TODO is this triggered?
		return getTellSpeechletResponse(speechText);
	}
	
	
	 /**
     * Creates and returns response for the launch of the skill
     *
     * @param LaunchRequest request for session
     * @param session for this request
     * 
     * @return Either a welcome menu, or a reminder of what recipe was being cooked if a session wasn't reset.
     */
	public SpeechletResponse getLaunchResponse(LaunchRequest request,
			Session session) {
		String speechText, repromptText;
		RecipeHelper recipe = RECIPE_HELPER_DAO.getCurrentSession(session);
		
		try{
		if (recipe.getRecipeName() == null || recipe == null) { // no previous recipe
			speechText = "Welcome to Recipe Helper. Say Help me cook and then a recipe name. Then, you can ask about ingredients or steps.";
			repromptText = "You can say what are the ingredients for pancakes, or, what is the first step for quick and easy pizza crust";
		} else {
			String recipeName = recipe.getRecipeName().replaceAll("\\p{Punct}+", "");
			speechText = "I see that we previously were cooking "
					+ recipeName; // recipe saved in Dynamo
			repromptText = "to start a new recipe, say new recipe. Otherwise, you can ask about "
					+ recipeName;
		}
		}catch(Exception e){ //if something went wrong (with getting the name, etc)
			speechText = "Welcome to Cooking Helper. Please ask about a recipe you would like to cook.";
			repromptText = "You can say help me cook pancakes. or what is the first step for quick and easy pizza crust";
		}
		return getAskSpeechletResponse(speechText, repromptText);
	}
	
	
	 /**
     * creates and returns the response for NewRecipeIntent
     *
     * @param session for this request
     * 
     * @return a prompt to start cooking something new if the recipe was reset.
     */
	public SpeechletResponse getNewRecipeIntent(Session session) {
		RecipeHelper recipe = RecipeHelper.newInstance(session,
				RecipeHelperRecipeData.newInstance(), 0, 0);
		RECIPE_HELPER_DAO.saveCurrentRecipe(recipe);

		return getAskSpeechletResponse("What would you like me to help you cook?", "ask about a recipe I can help you cook");

	}
	
	/**
	 * Handles the issues of a user saying "next" (which could apply to ingredient pagination
	 * or step pagination)
	 *
	 * @param session for this request
	 * @param the intent from the user
	 */
	public SpeechletResponse getWhatNext (Session session, Intent intent){
		String outputSpeech = "Did you mean Next Step or Next Ingredient?";
		String repromptSpeech = "Please say Next Step or Next Ingredient";
		return getAskSpeechletResponse(outputSpeech, repromptSpeech ); 
	}
	
	
	 /**
     * Creates and returns response for the getIngredientInformation intent
     * Creates a new recipe if none has been set up
     *
     * @param intent for this request
     * @param session for this request
     * 
     * @return response about the ingredient the user asked about. Will respond that the user
     * needs the ingredient, doesn't need the ingredient, or that the ingredient wasn't found. 
     */
	public SpeechletResponse getIngredientInformation(Session session,
			Intent intent) {
		RecipeHelper recipe = RECIPE_HELPER_DAO.getCurrentSession(session);
		
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
     * Creates and returns response for the getIngredientOverview intent.
     * Creates a new recipe if one hasn't been set up yet.
     *
     * @param intent for this request
     * @param session for this request
     * 
     * @return Creates an output which reads all of the ingredients from the current recipe
     */
	public SpeechletResponse getIngredientOverview(Session session,
			Intent intent) {
		RecipeHelper recipe = RECIPE_HELPER_DAO.getCurrentSession(session);
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
			ingredient = ingredient.replaceAll("\\.", " "); //remove all extra periods.
			outputText += "Ingredient " + (Integer.toString(i++)) + " is "
					+ ingredient + ".     ";	

		}
		outputSpeech.setText(outputText);
		return SpeechletResponse.newTellResponse(outputSpeech);

	}
	
	/**
     * Checks what ingredients have been heard and then returns the next ingredient.
     * Creates a new recipe if one hasn't been set up yet.
     * 
     * This method makes calls to different methods depending on what ingredient they need.
     *
     * @param intent for this request
     * @param session for this request
     * 
     * @return the correct ingredient to be listed
     */
	public SpeechletResponse getNextIngredient(Session session, Intent intent){
		RecipeHelper recipe = RECIPE_HELPER_DAO.getCurrentSession(session);
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
			return getTellSpeechletResponse(outputSpeech);
		}
		if (IngredientIndex == (IngrSize - 1)){ //last ingredient 
			String outputSpeech = lastIngredientResponse(CurrentIngredient, IngredientIndex, recipe);
			return getTellSpeechletResponse(outputSpeech);
		}
		else{
			String outputSpeech = otherIngredientResponse(CurrentIngredient, IngredientIndex, recipe);
			return getTellSpeechletResponse(outputSpeech);
			
		}
	}
	
	/**
     * A series of methods to handle the different ingredient indexes and increments as necessary.
     *
     * @param currentIngredient for this request
     * @param IngredientIndex the index of ingredients they have or haven't heard
     * @param recipe which holds the information for the session
     * 
     * @return the correct outputSpeech to be returned in getNextIngredient
     */
	public String FirstIngredientResponse(String CurrentIngredient, int IngredientIndex, RecipeHelper recipe){
		String outputSpeech = "the first ingredient is " + CurrentIngredient.trim();
		recipe.setIngredientIndex(++IngredientIndex);
		RECIPE_HELPER_DAO.saveCurrentRecipe(recipe);
		return outputSpeech;
	}
	public String lastIngredientResponse(String CurrentIngredient, int IngredientIndex, RecipeHelper recipe){
		String outputSpeech = "The next ingredient is " + CurrentIngredient.trim()+ ". " + "You've reached the end of the ingredient list, I'll reset that.";
		recipe.setIngredientIndex(0);
		RECIPE_HELPER_DAO.saveCurrentRecipe(recipe);
		return outputSpeech;
	}
	public String otherIngredientResponse(String CurrentIngredient, int IngredientIndex, RecipeHelper recipe){
		String outputSpeech = "The next ingredient is " + CurrentIngredient.trim();
		recipe.setIngredientIndex(++IngredientIndex);
		RECIPE_HELPER_DAO.saveCurrentRecipe(recipe);
		return outputSpeech;
	}
	
	
	/**
     * Checks what steps have been heard and then returns the next 
     *  Creates a new recipe if one hasn't been set up yet.
     * This method makes calls to different methods depending on what step they need.
     *
     * @param intent for this request
     * @param session for this request
     * 
     * @return the correct step to be listed
     */
	public SpeechletResponse getNextStep(Session session, Intent intent){
		RecipeHelper recipe = RECIPE_HELPER_DAO.getCurrentSession(session);
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
			return getTellSpeechletResponse(outputSpeech);
		}
		if (StepIndex == (StepSize - 1)){
			String outputSpeech = LastStepResponse(CurrentStep, StepIndex, recipe);
			return getTellSpeechletResponse(outputSpeech);
		}
		else{
			String outputSpeech = OtherStepResponse(CurrentStep, StepIndex, recipe);
			return getTellSpeechletResponse(outputSpeech);
			
		}
	}
	
	/**
     * A series of methods to handle the different step indexes and increments as necessary.
     *
     * @param currentStep for this request
     * @param StepIndex the index of ingredients they have or haven't heard
     * @param recipe which holds the information for the session
     * 
     * @return the correct outputSpeech to be returned to getNextStep
     */
	public String FirstStepResponse(String CurrentStep, int StepIndex, RecipeHelper recipe){
		String outputSpeech = "the first step is " + CurrentStep.trim();
		recipe.setStepIndex(++StepIndex);
		RECIPE_HELPER_DAO.saveCurrentRecipe(recipe);
		return outputSpeech;
	}
	public String LastStepResponse(String CurrentStep, int StepIndex, RecipeHelper recipe){
		String outputSpeech = "The next step is " + CurrentStep.trim()+ ". " + "You've reached the end of the step list, I'll reset that.";
		recipe.setStepIndex(0);
		RECIPE_HELPER_DAO.saveCurrentRecipe(recipe);
		return outputSpeech;
	}
	public String OtherStepResponse(String CurrentStep, int StepIndex, RecipeHelper recipe){
		String outputSpeech = "The next Step is " + CurrentStep.trim();
		recipe.setStepIndex(++StepIndex);
		RECIPE_HELPER_DAO.saveCurrentRecipe(recipe);
		return outputSpeech;
	}
	
	/**
     * Creates and returns response for the getStepOverview intent
     * Creates a new recipe if one hasn't been set up yet.
     *
     * @param intent for this request
     * @param session for this request
     * 
     * @return reads all the steps for the current recipe.
     */
	public SpeechletResponse getStepOverview(Session session, Intent intent) {
		RecipeHelper recipe = RECIPE_HELPER_DAO.getCurrentSession(session);
		if (recipe == null || recipe.hasURL() == false) {
			recipe = RecipeHelper.newInstance(session,
					RecipeHelperRecipeData.newInstance(), 0 , 0); 
			setUpNewRecipe(session, intent, recipe);
		}

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		String outputText = "";
		List<String> steps = recipe.getAllSteps();
		int i = 1;
		for (String step : steps) {
			step = step.replaceAll("\\.", ""); //remove all periods
			step = step.replaceAll("\\[", ""); //remove any leading brackets
			outputText += "Step " + (Integer.toString(i++)) + " is " + step.trim() + ".    ";

		}

		outputSpeech.setText(outputText);
		return SpeechletResponse.newTellResponse(outputSpeech);
	}
	
	/**
     * Secondary menu. this is used if the user passes in only a recipe name without further intent
     *
     * @param intent for this request
     * @param session for this request
     * 
     * @return tells the user the specific recipe saved for them and lists further options.
     */
	public SpeechletResponse getSecondaryLaunchRequest(Session session, Intent intent){
		RecipeHelper recipe = RecipeHelper.newInstance(session, RecipeHelperRecipeData.newInstance(), 0, 0);
		Slot RecipeNameSlot = intent.getSlot(LIST_OF_RECIPES);
		String recipeName = RecipeNameSlot.getValue();
		
		try {
			RecipeSetup.RecipeBuilder(session, recipeName, recipe,
					RECIPE_HELPER_DAO); // if this returns null, re-prompt
		} catch (Exception e) {
			String speechText = "Sorry, I couldnt' connect to that recipe. Ask about a different recipe?";
			return getAskSpeechletResponse(speechText, speechText);
		}
		RECIPE_HELPER_DAO.saveCurrentRecipe(recipe);
		String outputSpeech = "now cooking " + recipe.getRecipeName() + "You can now ask recipe helper for steps or ingredients.";
		return getTellSpeechletResponse(outputSpeech);
		
	}
	
	
	/**
     * Creates and returns response for the getSpecificStep intent
     * Creates a new recipe if one hasn't been set up yet.
     *
     * @param intent for this request
     * @param session for this request
     * 
     * @return response for getting a specific step asked for.
     */
	public SpeechletResponse getSpecificStep(Session session, Intent intent) {
		RecipeHelper recipe = RECIPE_HELPER_DAO.getCurrentSession(session);
		if (recipe == null || recipe.hasURL() == false) {
			recipe = RecipeHelper.newInstance(session,
					RecipeHelperRecipeData.newInstance(), 0, 0);
			setUpNewRecipe(session, intent, recipe);
		}
		Slot RequestedStep = intent.getSlot(AMAZON_NUMBER); 
		List<String> ListofSteps = recipe.getAllSteps();

		if (RequestedStep != null && RequestedStep.getValue() != null) { 
			String NumberName = RequestedStep.getValue(); 
			int StepNumber = Integer.parseInt(NumberName); 
			if (StepNumber > ListofSteps.size()) {
				String outputText = ("The last step is step "
						+ Integer.toString(ListofSteps.size()) + " Please ask for a step within that range.");
				return getAskSpeechletResponse(outputText, "You can ask about another step or an ingredient");
			} else {
				String Direction = ListofSteps.get(StepNumber - 1); 
				Direction = Direction.replaceAll("\\[", "");
				String outputText = ("Step " + StepNumber + " is " + Direction);
				recipe.setStepIndex(StepNumber); //set index to match step asked for TODO test
				RECIPE_HELPER_DAO.saveCurrentRecipe(recipe);
				return getTellSpeechletResponse(outputText);

			}
		} else {
			String outputText = ("I'm sorry, I couldn't find that step number. There are only "
					+ Integer.toString(ListofSteps.size()) + " steps.");
			return getAskSpeechletResponse(outputText, "you can ask about another step or an ingredient");
		}
		
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
