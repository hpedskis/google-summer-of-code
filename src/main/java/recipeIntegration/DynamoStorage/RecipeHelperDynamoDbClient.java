package recipeIntegration.DynamoStorage;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;


/**
 * The class creates a DynamoDBClient using the CustomerID. Then it checks if the recipe is null or not
 * when an item is trying to be loaded. This check is done at the beginning of each intent or launch. If it is null,
 * a new recipe will be set up. Otherwise, it will return the RecipeHelperDataItem (and the current recipe).
 *
 */
public class RecipeHelperDynamoDbClient {

	private final AmazonDynamoDBClient dynamoDBClient;

	public RecipeHelperDynamoDbClient(final AmazonDynamoDBClient dynamoDBClient) {
		this.dynamoDBClient = dynamoDBClient;
	}

	public RecipeHelperRecipeDataItem loadItem(
			final RecipeHelperRecipeDataItem tableItem) {
		DynamoDBMapper mapper = createDynamoDBMapper();
		RecipeHelperRecipeDataItem item = mapper.load(tableItem);
		if (StringUtils
				.containsIgnoreCase(
						item.getRecipeData().toString(),
						("Recipe Name: \"null Recipe Url: null Ingredients: [[]] Directions: [[]]"))
				|| StringUtils
						.containsIgnoreCase(item.getRecipeData().toString(),
								"Recipe Name: \"null Recipe Url: null Ingredients: [[.]] Directions: [[.]]")) {
			return null;
		}
		return item;
	}

	public void saveItem(final RecipeHelperRecipeDataItem tableItem) {
		DynamoDBMapper mapper = createDynamoDBMapper();
		mapper.save(tableItem);
	}

	private DynamoDBMapper createDynamoDBMapper() {
		return new DynamoDBMapper(dynamoDBClient);
	}

}
