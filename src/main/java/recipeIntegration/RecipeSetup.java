package recipeIntegration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.amazon.speech.speechlet.Session;

import recipeIntegration.DynamoStorage.RecipeHelper;
import recipeIntegration.DynamoStorage.RecipeHelperDao;

/**
 * The class is used to set up all recipes. This could be when the user uses the application for the very first time,
 * or when they want to cook a recipe other than the one previously set up.
 * 
 * It first matches the recipe to one in the database, either finding an exact match, or the closest match possible,
 * then connects to that reicipe's URL. After successfully connecting, all of the ingredients and steps are found
 * and saved to DynamoDB (which keeps track of what recipe the user is cooking). All of the initial formatting
 * for the ingredients, steps, and title are done here. 
 * 
 *
 */
class RecipeSetup {

	public static Map<String, String> MapOfRecipes = new HashMap<String, String>();

	// when a user passes in a string title, create a map from the text file,
	// find that recipe, or return that it wasn't found.

	
	 /**
     * attemps to find the recipe from the title passed in 
     * creates a map of all the names and URLs for easy searching.
     * passes the URL or null back to RecipeBuilder
     *
     * @param RecipeName, the string of the desired recipe 
     * 
     * @return the URL of the recipe, or null if the recipe was not found.
     */
	@SuppressWarnings("resource")
	public static String FindRecipe(String RecipeName)
			throws FileNotFoundException, UnsupportedEncodingException {

		RecipeName = StringUtils.lowerCase(RecipeName).trim();

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream is = loader.getResourceAsStream("MASTER_RECIPE.txt");
		Scanner reader = new Scanner(new InputStreamReader(is, "UTF-8"));

		//create a map
		while (reader.hasNextLine()) {
			String recipeTitle = reader.nextLine();
			String recipeInfo = reader.nextLine();
			MapOfRecipes.put(StringUtils.lowerCase(recipeTitle).trim(),
					recipeInfo);
		}
		//searches the map for the title and corresponding URL
		for (Map.Entry<String, String> Entry : MapOfRecipes.entrySet()) {
			if (StringUtils.equalsIgnoreCase(Entry.getKey(), RecipeName)) {
				return Entry.getValue();
			}
		}
		return null;

	}

	 /**
     * attempts to find a recipe with a similar name if the exact match couldn't be found
     * returns the URL to ReicpeBuilder
     *
     * @param RecipeName, the title of the desired recipe
     * 
     * @return the URL for the recipe that matched the input
     */
	public static String FindBackupRecipe(String RecipeName) {
		RecipeName = StringUtils.lowerCase(RecipeName).trim();
		for (Map.Entry<String, String> Entry : MapOfRecipes.entrySet()) {
			if (StringUtils.containsIgnoreCase(Entry.getKey(), RecipeName)) {
				return Entry.getValue();
			}
		}

		return null;
	}

	 /**
     * creates a recipe object by connecting to the URL and then sets
     * all information inside Dynamo and in recipeIngegration.DynamoStorage classes
     * Makes calls to FindRecipe and FindBackupRecipe
     *
     * @param session for this request
     * @param RecipeName for the recipe
     * @param recipe, the RecipeHelepr object
     * @param recipeHelperDao, so the information can be saved in Dynamo.
     * 
     * @return nothing, but saves all information for the session.
     */
	public static void RecipeBuilder(Session session, String RecipeName,
			RecipeHelper recipe, RecipeHelperDao recipeHelperDao)
			throws FileNotFoundException, UnsupportedEncodingException {
		String RecipeURL = null;
		RecipeURL = FindRecipe(RecipeName);
		if (RecipeURL == null) {
			RecipeURL = FindBackupRecipe(RecipeName); //exact match could not be found
		}

		List<String> INGREDIENT_LIST = new ArrayList<String>();
		List<String> STEP_LIST = new ArrayList<String>();

		Document InRecipe = null;
		try {
			InRecipe = Jsoup.connect("http://allrecipes.com/" + RecipeURL)
					.get();
		} catch (IOException e) {
			System.out.println("Cannot connect to recipe");
		}
		String RecipeTitle = InRecipe.select("h1.recipe-summary__h1").text();
		for (Element IngredientResult : InRecipe.select("li.checkList__line")) {
			String ingredient = IngredientResult.text();

			ingredient = ingredient.replace("ADVERTISEMENT", "");
			ingredient = ingredient.replace("Add all ingredients to list", "");

			INGREDIENT_LIST = formatIngredients(ingredient, INGREDIENT_LIST);
		}

		for (Element DirectionResult : InRecipe
				.select("div.directions--section__steps ol")) {
			String direction = DirectionResult.text();
			direction = direction.replace("ADVERTISEMENT", "");
			System.out.println("printing current direction " + direction);
			STEP_LIST = formatDirections(direction.trim(), STEP_LIST);

		}

		recipe.setRecipeName(RecipeTitle);
		recipe.setIngredients(INGREDIENT_LIST);
		recipe.setSteps(STEP_LIST);
		recipe.setRecipeURL(RecipeURL);
		

		recipeHelperDao.saveCurrentRecipe(recipe);

	}

	public static List<String> formatIngredients(String Ingredients,
			List<String> IngredientList) {
		if (!Ingredients.isEmpty()) {// if it isn't empty white space
			Ingredients = Ingredients.replace(",", "");
			IngredientList.add(Ingredients + ".");
		}
		return IngredientList;

	}

	public static List<String> formatDirections(String Directions,
			List<String> StepList) {
		String[] parts = Directions.split("\\.");
		for (int i = 0; i < parts.length; i++) {
			if (parts[i].length() > 3) {
				StepList.add(i, parts[i].trim() + ".");
			} else {
				//skip it
			}

		}
		return StepList;
	}
}
