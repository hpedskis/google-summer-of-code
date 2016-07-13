package recipeIntegration.DynamoStorage;

import java.util.List;

import com.amazon.speech.speechlet.Session;

public class RecipeHelper {

	private Session session;
	private RecipeHelperRecipeData recipeData;

	private RecipeHelper() {

	}

	public static RecipeHelper newInstance(Session session,
			RecipeHelperRecipeData recipeData) {
		System.out
				.println("step 7: inside RecipeHelper line 19 and creating a new instance.");
		RecipeHelper recipe = new RecipeHelper();
		recipe.setSession(session);
		recipe.setRecipeData(recipeData);
		System.out.println("just created a new recipeHelper object with data "
				+ recipe.getRecipeData());
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
		recipeData.setCurrentIngredient(0);

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
	
	public void setCurrentIngredient(int index){
		recipeData.setCurrentIngredient(index);
	}
	public int getCurrentIngredient(){
		return recipeData.getCurrentIngredient();
	}
}
