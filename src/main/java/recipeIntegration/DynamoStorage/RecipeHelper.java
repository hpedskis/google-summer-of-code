package recipeIntegration.DynamoStorage;

import java.util.List;

import com.amazon.speech.speechlet.Session;

//creates the whole recipe object, including sesssion information, all the recipeData, 
//and the index for ingredients and steps
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
		System.out.println("just created a new recipeHelper object with data " + recipe.getRecipeData());
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
		//recipeData.setCurrentIngredient(0);
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
