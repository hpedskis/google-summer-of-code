package recipeIntegration.DynamoStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * This class has the specifics of the RecipeHelperData that is stored in the DynamoDB table.
 * New instances of the RecipeHelperData (ingredients,steps,name,URL) can be made here.
 * 
 * All of the logic for getting and setting is also listed for each of the four items in the data (ingredient
 * array, step array, name, and url)
 * 
 * getting and setting the name and url is simple and straight forward. When getting the entire list of ingredients 
 * or steps, there is some clean up. For some reason, every time the item is re-saved in Dynamo, more '/' and '[' are produced.
 * I never really figured out what was causing this, so I just make sure to strip the returned item of these.
 * 
 * For getting a specific ingredient, I have a double search method. I go through first and look for the exact match,
 * if this fails I implement fuzzy searching. 
 *
 */

public class RecipeHelperRecipeData {
	private List<String> Ingredients;
	private List<String> Steps;
	private String RecipeName;
	private String RecipeURL;

	public RecipeHelperRecipeData() {
		// public no-arg constructor required for DynamoDBMapper marshalling
	}
	

	public static RecipeHelperRecipeData newInstance() {
		RecipeHelperRecipeData newInstance = new RecipeHelperRecipeData();
		newInstance.setIngredients(new ArrayList<String>());
		newInstance.setSteps(new ArrayList<String>());
		newInstance.setRecipeName(null);
		newInstance.setRecipeURL(null);
		return newInstance;
	}
	public String getRecipeName() {
		return RecipeName;
	}

	public void setRecipeName(String RecipeName) {
		this.RecipeName = RecipeName;
	}
	
	public String getRecipeURL() {
		return RecipeURL;
	}

	public void setRecipeURL(String recipeURL) {
		RecipeURL = recipeURL;
	}

	public List<String> getIngredients() { // gets list of ingredients
		List<String> REALIngredients = new ArrayList<String>();
		String[] IngredientParts = Ingredients.toString().split("\\.");
		for (String Ingredient : IngredientParts) {
			Ingredient = Ingredient.replaceAll(",", "");
			Ingredient = Ingredient.replaceAll("\\[", "");
			Ingredient = Ingredient.replaceAll("\\]", "");
			if(!(Ingredient.length() < 4)){
				REALIngredients.add(Ingredient.trim() + ".");
			}
		
		}
		return REALIngredients;
	}
	public List<String> getSteps() {
		List<String> REALSteps = new ArrayList<String>();
		String[] StepParts = Steps.toString().split("\\.");
		for (String Step : StepParts) {
			Step = Step.replace("\\[", "");
			Step = Step.replace("\\]", "");
			Step = Step.replaceAll(",", "");
			if(!(Step.length() < 4)){
				REALSteps.add(Step.trim() + ".");
			}
		}
		return REALSteps;

	}


	public String getBestMatchingIngredient(String Ingredient){
		List<String> IngredientList = getIngredients(); //get ingredients from current recipe
		int BestMatch = 0;
		int BestMatchIndex = 0;
		String BestIngredientMatch = null;
		StringUtils.removeEnd(Ingredient, "s"); //get rid of plurals.
		for (int i = 0; i < IngredientList.size(); i++) {
			if (StringUtils.equalsIgnoreCase(IngredientList.get(i), Ingredient)){
				BestIngredientMatch = IngredientList.get(i);
				return BestIngredientMatch;
			}
		}
		for (int i = 0; i < IngredientList.size(); i++){
			int temp = StringUtils.getFuzzyDistance(IngredientList.get(i), Ingredient, Locale.ENGLISH);
			if (temp > BestMatch){ 
				BestMatch = temp;
				BestMatchIndex = i;
			}
		}
		
		BestIngredientMatch = IngredientList.get(BestMatchIndex);
		if (!(StringUtils.contains(BestIngredientMatch, Ingredient))){
			return null;
		}
		return BestIngredientMatch;  
		
	}
	
	
	public void setIngredients(List<String> Ingredients) {
		this.Ingredients = Ingredients;
	}


	public void setSteps(List<String> Steps) {
		this.Steps = Steps;
	}

	@Override
	public String toString() {
		return "Recipe Name: " + RecipeName + " Recipe Url: " + RecipeURL
				+ " Ingredients: " + Ingredients + " Directions: " + Steps;
	}


}
