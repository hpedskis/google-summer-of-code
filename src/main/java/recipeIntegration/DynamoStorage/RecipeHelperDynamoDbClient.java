package recipeIntegration.DynamoStorage;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class RecipeHelperDynamoDbClient {
	
	 private final AmazonDynamoDBClient dynamoDBClient;
	 
	 public RecipeHelperDynamoDbClient(final AmazonDynamoDBClient dynamoDBClient) {
	        this.dynamoDBClient = dynamoDBClient;
	   }
	 
	 public RecipeHelperRecipeDataItem loadItem(final RecipeHelperRecipeDataItem tableItem) {
	        DynamoDBMapper mapper = createDynamoDBMapper();
	        RecipeHelperRecipeDataItem item = mapper.load(tableItem);
	        System.out.println("step 5: claiming to return the recipe helper data item " + item.getRecipeData());
	        if (StringUtils.containsIgnoreCase(item.getRecipeData().toString(), ("Recipe Name: \"null Recipe Url: null Ingredients: [[[]]] Directions: [[[]]]"))
	        		|| StringUtils.containsIgnoreCase(item.getRecipeData().toString(), "Recipe Name: \"null Recipe Url: null Ingredients: [[[]]] Directions: [[[].]]")){
	        	return null;
	        }
	        return item;
	    }
	 
	 public void saveItem(final RecipeHelperRecipeDataItem tableItem) {
	        DynamoDBMapper mapper = createDynamoDBMapper();
	        mapper.save(tableItem);
	    }
	 
	 private DynamoDBMapper createDynamoDBMapper() {
		 System.out.println("step 4: trying to create the dynamo mapper");
	        return new DynamoDBMapper(dynamoDBClient);
	    }

}
