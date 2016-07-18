package recipeIntegration.DynamoStorage;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
		//RecipeName = RecipeName.replace("\"", "");
		this.RecipeName = RecipeName;
	}

	public List<String> getIngredients() { // gets list of ingredients
		System.out.println("inside getIngredients.");
		List<String> REALIngredients = new ArrayList<String>();
		String[] IngredientParts = Ingredients.toString().split("\\.");
		for (String Ingredient : IngredientParts) {
			System.out.println("raw ingredient from IngredientParts " + Ingredient);
			Ingredient = Ingredient.replaceAll(",", "");
			Ingredient = Ingredient.replaceAll("\\[", "");
			Ingredient = Ingredient.replaceAll("\\]", "");
			if(!(Ingredient.length() < 4)){
				System.out.println("adding an ingredinet " + Ingredient);
				REALIngredients.add(Ingredient.trim() + ".");
			}
		
		}
		return REALIngredients;
	}
	public List<String> getSteps() {
		System.out.println("inside getSteps.");
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

	public String fetchIngredient(String Ingredient) { // get ONE ingredient
		List<String> REALIngredients = getIngredients();
		String theCorrectIngredient = null;
		for (int i = 0; i < REALIngredients.size(); i++) {
			if (StringUtils.containsIgnoreCase(REALIngredients.get(i), Ingredient)) {
				theCorrectIngredient = REALIngredients.get(i);
				return theCorrectIngredient;
			}
		}
		return theCorrectIngredient;
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

	public String getRecipeURL() {
		return RecipeURL;
	}

	public void setRecipeURL(String recipeURL) {
		RecipeURL = recipeURL;
	}

}
