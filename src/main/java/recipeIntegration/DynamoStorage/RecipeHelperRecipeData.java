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
	private int CurrentIngredient;

	public RecipeHelperRecipeData() {
		// public no-arg constructor required for DynamoDBMapper marshalling
	}

	public static RecipeHelperRecipeData newInstance() {
		RecipeHelperRecipeData newInstance = new RecipeHelperRecipeData();
		newInstance.setIngredients(new ArrayList<String>());
		newInstance.setSteps(new ArrayList<String>());
		newInstance.RecipeName = null;
		newInstance.RecipeURL = null;
		newInstance.CurrentIngredient = 0; //first ingredient hasn't been heard
		return newInstance;
	}
	
	public int getCurrentIngredient(){
		return CurrentIngredient;
	}
	public void setCurrentIngredient(int index){
			this.CurrentIngredient = index;
		
	}

	public String getRecipeName() {
		return RecipeName;
	}

	public void setRecipeName(String RecipeName) {
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
				REALIngredients.add(Ingredient + ".");
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
				REALSteps.add(Step + ".");
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

	@JsonIgnoreProperties
	public int getIngredientListSize() {
		return Ingredients.size();
	}

	public int getStepListSize() {
		return Steps.size();
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
				+ " Ingredients: " + Ingredients + " Directions: " + Steps + " Current Ingredient Index " + CurrentIngredient;
	}

	public String getRecipeURL() {
		return RecipeURL;
	}

	public void setRecipeURL(String recipeURL) {
		RecipeURL = recipeURL;
	}

}
