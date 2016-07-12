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
		System.out
				.println("step 2: inside recipe helper manager with the dynamo client");

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
		System.out.println("inside set up new recipe line 40 of manager");
		//String recipeName = "banana bread"; //TODO REMOVE BEFORE REAL TESTING
		if (recipeName == null || recipeName == "") {
			String speechText = "I didn't hear that reicpe name. What do you want me to help you cook?";
			return getAskSpeechletResponse(speechText, speechText);

		}
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		try {
			System.out.println("trying to build new recipe.");
			RecipeSetup.RecipeBuilder(session, recipeName, recipe,
					recipeHelperDao); // if this returns null, re-prompt
		} catch (Exception e) {
			String speechText = "Sorry, I couldnt' connect to that recipe. Ask about a different recipe?";
			return getAskSpeechletResponse(speechText, speechText);
		}
		if (recipe.getAllSteps() == null) {
			String speechText = "Sorry, I couldnt' find anything similar to that recipe. Ask about a different recipe?";
			return getAskSpeechletResponse(speechText, speechText);
		}
		recipeHelperDao.saveCurrentRecipe(recipe);

		outputSpeech.setText("Now cooking" + recipe.getRecipeName());
		return SpeechletResponse.newTellResponse(outputSpeech);
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

		if (recipe == null || !(recipe.hasURL())) { // no previous recipe
			speechText = "Welcome to Cooking Helper. Please ask about a recipe you would like to cook.";
			repromptText = "You can say How do I cook pancakes? or what is step 3 for quick and easy pizza crust";
		} else {
			speechText = "I see that we previously were cooking "
					+ recipe.getRecipeName(); // recipe saved in Dynamo
			repromptText = "to start a new recipe, say New Reicpe. Otherwise, you can ask about "
					+ recipe.getRecipeName();
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
				RecipeHelperRecipeData.newInstance());
		recipeHelperDao.saveCurrentRecipe(recipe);

		return getAskSpeechletResponse(
				"What would you like me to help you cook?",
				"You can ask about a specific recipe, its directions, or its ingredients");

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
					RecipeHelperRecipeData.newInstance());
			setUpNewRecipe(session, intent, recipe);
		}
		// if a recipe already exists or was just set up after that message....
		Slot IngredientSlot = intent.getSlot(INGREDIENT_LIST); 
		
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		String outputText = ""; // initialize

		if (IngredientSlot != null && IngredientSlot.getValue() != null) { 

			String IngredientName = IngredientSlot.getValue();
			StringUtils.lowerCase(IngredientName);

			String ingredient = recipe.getSpecificIngredient(IngredientName);

			if (ingredient != null) { // it found specific ingredient
				outputText = ("You need " + StringUtils.replace(ingredient,
						"[", ""));
				System.out.println("you need " + ingredient);

			} else {
				outputText = ("You don't need " + IngredientName);
				System.out.println("you don't need " + ingredient);
			}
		} else {
			System.out.println("I couldn't find that ingredient!!!!!!!!!");
			return getAskSpeechletResponse("I couldn't find that ingredient.",
					"How about you ask about a different ingredient");
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
		System.out.println("inside get ingredient overview... hooray");
		RecipeHelper recipe = recipeHelperDao.getCurrentSession(session);
		if (recipe == null || recipe.hasURL() == false) {
			recipe = RecipeHelper.newInstance(session,
					RecipeHelperRecipeData.newInstance());
			setUpNewRecipe(session, intent, recipe);
		}
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		String outputText = "";
		List<String> ingredients = recipe.getAllIngredients();
		int i = 1;
		for (String ingredient : ingredients) {
			System.out.println("Ingredient " + (Integer.toString(i)) + " is "
					+ ingredient.trim());
			outputText += "Ingredient " + (Integer.toString(i++)) + " is "
					+ ingredient + ".";	

		}

		outputSpeech.setText(outputText);
		return SpeechletResponse.newTellResponse(outputSpeech);

	}
	
	public SpeechletResponse getNextIngredient(Session session, Intent intent){
		RecipeHelper recipe = recipeHelperDao.getCurrentSession(session);
		if (recipe == null || recipe.hasURL() == false) {
			System.out.println("setting up a new recipe inside getNextIngredient");
			recipe = RecipeHelper.newInstance(session,
					RecipeHelperRecipeData.newInstance());
			setUpNewRecipe(session, intent, recipe);
		}
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		String outputText = "";
		List<String> ingredients = recipe.getAllIngredients();
		int IngrSize = ingredients.size();
		int IngredientIndex = recipe.getCurrentIngredient();
		if (recipe.getCurrentIngredient() >= IngrSize){
			outputText += "You've now reached the end of the ingredient list. I'll reset that for you";
			recipe.setCurrentIngredient(0);
		}
		String Ingredient = ingredients.get(IngredientIndex);
		if (IngredientIndex == 0){
			System.out.println("first ingredient is " + Ingredient);
			outputText += "the first ingredient is "
					+ Ingredient;
		}
		else{
			System.out.println("the next ingredient is " + Ingredient);
		outputText += "The next ingredient is "
				+ Ingredient;
		}
		IngredientIndex++;
		recipe.setCurrentIngredient(IngredientIndex);
		recipeHelperDao.saveCurrentRecipe(recipe);
		System.out.println("just tried to save the recipe");
		System.out.println("NOW the current ingredient index is " + recipe.getCurrentIngredient());
		outputSpeech.setText(outputText);
		return SpeechletResponse.newTellResponse(outputSpeech);
	}
	/*/
	public SpeechletResponse getNextStep(Session session, Intent intent){
		RecipeHelper recipe = recipeHelperDao.getCurrentSession(session);
		if (recipe == null || recipe.hasURL() == false) {
			recipe = RecipeHelper.newInstance(session,
					RecipeHelperRecipeData.newInstance());
			setUpNewRecipe(session, intent, recipe);
		}
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		String outputText = "";
		List<String> steps = recipe.getAllSteps();
		int CurrentStep = recipe.getCurrentIngredient(); //TODO
		String Ingredient = steps.get(CurrentStep);
		
		if (CurrentStep == 0){
			outputText += "the first step is "
					+ Ingredient;
		}
		else{
		outputText += "The next step is "
				+ Ingredient;
		}
		outputSpeech.setText(outputText);
		return SpeechletResponse.newTellResponse(outputSpeech);
	}
	/*/
	
	/**
     * Creates and returns response for the getStepOverview intent
     *
     * @param intent for this request
     * @param session for this request
     * 
     * @return response for getting the step overview.
     */
	public SpeechletResponse getStepOverview(Session session, Intent intent) {
		System.out.println("inside reicpe manager to get step overview.");
		RecipeHelper recipe = recipeHelperDao.getCurrentSession(session);
		if (recipe == null || recipe.hasURL() == false) {
			System.out
					.println("SUCCESS...name is flagged as null. creating new instance and setting up.");
			recipe = RecipeHelper.newInstance(session,
					RecipeHelperRecipeData.newInstance());
			setUpNewRecipe(session, intent, recipe);
		}

		System.out.println("made it out of getting current session again.");

		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		String outputText = "";
		System.out.println("about to read each step. GOOD THANGZ");
		List<String> steps = recipe.getAllSteps();
		int i = 1;
		for (String step : steps) {
			System.out.println("Step " + (Integer.toString(i)) + " is " + step
					+ ".");
			outputText += "Step " + (Integer.toString(i++)) + " is " + step;

		}

		outputSpeech.setText(outputText);
		return SpeechletResponse.newTellResponse(outputSpeech);
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
					RecipeHelperRecipeData.newInstance());
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
