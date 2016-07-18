package recipeIntegration.DynamoStorage;

import com.amazon.speech.speechlet.Session;

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
			System.out.println("item is null. failed to load from table");
			return null;
		}
		System.out
				.println("step 6: item wasn't flagged as null. creating new instance");
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
