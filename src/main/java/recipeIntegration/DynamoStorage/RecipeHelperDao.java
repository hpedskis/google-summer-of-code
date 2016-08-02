package recipeIntegration.DynamoStorage;

import com.amazon.speech.speechlet.Session;


/**
 * Gets the current session (table information) for the customerID being used. If RecipeHelperDynamoDbClient
 * returns null, a new recipe will be setup else where. If it isn't null, a new instance of RecipeHelper
 *  will be created for reference, and the recipeData, ingredientIndex, StepIndex, etc. will be recalled.
 *  
 *  The method to save the current recipe is also in this class. It simply sets the attributes and saves.
 *
 */
public class RecipeHelperDao {
	private final RecipeHelperDynamoDbClient dynamoDbClient;

	public RecipeHelperDao(RecipeHelperDynamoDbClient dynamoDbClient) {
		this.dynamoDbClient = dynamoDbClient;
	}

	public RecipeHelper getCurrentSession(Session session) {
		RecipeHelperRecipeDataItem item = new RecipeHelperRecipeDataItem();
		item.setCustomerId(session.getUser().getUserId());
		try {
			item = dynamoDbClient.loadItem(item);
		} catch (Exception e) {
			System.out.println("dynamo failed to load");
		}
		if (item == null) {
			return null;
		}
		int currentStep = item.getIngredientIndex();
		item.setIngredientIndex(currentStep);
		return RecipeHelper.newInstance(session, item.getRecipeData(), item.getIngredientIndex(), item.getStepIndex());
	}

	public void saveCurrentRecipe(RecipeHelper recipe) {
		RecipeHelperRecipeDataItem item = new RecipeHelperRecipeDataItem();
		item.setCustomerId(recipe.getSession().getUser().getUserId());
		item.setIngredientIndex(recipe.getIngredientIndex());
		item.setRecipeData(recipe.getRecipeData());
		item.setStepIndex(recipe.getStepIndex());

		dynamoDbClient.saveItem(item);
	}
}
