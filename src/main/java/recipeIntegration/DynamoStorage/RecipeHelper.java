package recipeIntegration.DynamoStorage;

import java.util.List;

import com.amazon.speech.speechlet.Session;


/**
 * This class brings together all of the information that is stored in the DynamoDB table. The current session, 
 * the RecipeHelperRecipeData item (which includes the name, URL, steps, and ingredients), the IngredientIndex
 * (the number of ingredients the user has heard) and the StepIndex (the number of steps the user has heard). 
 * The method to reset the recipe is also listed in here.
 * 
 * A large amount of getters and setters are here. For all of the information in the REcipeHelperRecipeData,
 * recipeData is used to get/set those items through the class RecipeHelperRecipeData. 
 *
 */
public class RecipeHelper {

	private Session session;
	private RecipeHelperRecipeData recipeData;
	private int IngredientIndex;
	private int StepIndex;
	
	private RecipeHelper() {

	}

	public static RecipeHelper newInstance(Session session,
			RecipeHelperRecipeData recipeData, int Index, int StepIndex) {
		RecipeHelper recipe = new RecipeHelper();
		recipe.setSession(session);
		recipe.setRecipeData(recipeData);
		recipe.setIngredientIndex(Index); //new instance, index in ingredients will be 0
		recipe.setStepIndex(StepIndex);
		return recipe;
	}

	protected void setSession(Session session) {
		this.session = session;
	}

	protected Session getSession() {
		return session;
	}

	protected RecipeHelperRecipeData getRecipeData() {
		return recipeData;
	}

	protected void setRecipeData(RecipeHelperRecipeData recipeData) {
		this.recipeData = recipeData;
	}

	public void resetRecipe() {
		recipeData.getIngredients().clear();
		recipeData.getSteps().clear();
		recipeData.setRecipeURL(null);
		recipeData.setRecipeName(null);
		IngredientIndex = 0;
		StepIndex = 0;

	}

	public boolean hasURL() {
		if (recipeData.getRecipeURL() == null) {
			return false;
		}
		return true;
	}

	public String getRecipeName() {
		String recipeName = recipeData.getRecipeName();
		recipeName = recipeName.replaceAll("\\p{Punct}+", "");
		return recipeName;
	}


	public List<String> getAllSteps() {
		return recipeData.getSteps();
	}

	public String getSpecificStep(int number) {
		return recipeData.getSteps().get(number);
	}

	public List<String> getAllIngredients() {
		return recipeData.getIngredients();
	}

	public String getSpecificIngredient(String ingredient) {
		return recipeData.fetchIngredient(ingredient);
		//return recipeData.getBestMatchingIngredient(ingredient);
	}

	public void setIngredients(List<String> Ingredients) {
		recipeData.setIngredients(Ingredients);
	}

	public void setSteps(List<String> Steps) {
		recipeData.setSteps(Steps);
	}

	public void setRecipeURL(String recipeURL) {
		recipeData.setRecipeURL(recipeURL);
	}

	public void setRecipeName(String RecipeName) {
		recipeData.setRecipeName(RecipeName);
	}
	
	public void setIngredientIndex(int index){
		this.IngredientIndex = index;
	}
	public int getIngredientIndex(){
		return IngredientIndex;
	}
	
	public void setStepIndex(int index){
		this.StepIndex = index;
	}
	public int getStepIndex(){
		return StepIndex;
	}
}
