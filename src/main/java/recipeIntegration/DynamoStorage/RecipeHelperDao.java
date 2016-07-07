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
        System.out.println("step 3: trying to get current session inside recipe helper dao.");
        try{
        item = dynamoDbClient.loadItem(item);
        }
        catch (Exception e){
        	System.out.println("momenterily failing. going to create recipe");
        	return null;
        }
        if (item.getRecipeData() == null) {
        	System.out.println("item is null. failed to load from table");
            return null;
        }
        System.out.println("step 6: item wasn't flagged as null. creating new instance");
        return RecipeHelper.newInstance(session, item.getRecipeData());
    }
    
    public void saveCurrentRecipe(RecipeHelper recipe) {
    	RecipeHelperRecipeDataItem item = new RecipeHelperRecipeDataItem();
        item.setCustomerId(recipe.getSession().getUser().getUserId());
        item.setRecipeData(recipe.getRecipeData());

        dynamoDbClient.saveItem(item);
    }
}
