package recipeIntegration.DynamoStorage;

import java.util.Arrays;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


@DynamoDBTable(tableName = "RecipeHelperRecipeData")
public class RecipeHelperRecipeDataItem {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private String customerId;

	private RecipeHelperRecipeData recipeData; //recipe information

	@DynamoDBHashKey(attributeName = "CustomerId")
	public String getCustomerId() {
	
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	@DynamoDBAttribute(attributeName = "Data")
	@DynamoDBMarshalling(marshallerClass = RecipeHelperRecipeDataMarshaller.class)
	public RecipeHelperRecipeData getRecipeData() { //returns recipeURL
		return recipeData;
	}

	public void setRecipeData(RecipeHelperRecipeData recipeData) {
		this.recipeData = recipeData;
	}


	public static class RecipeHelperRecipeDataMarshaller implements DynamoDBMarshaller<RecipeHelperRecipeData> {

	@Override
	public String marshall(RecipeHelperRecipeData recipeData) {
		try {
			//System.out.println(OBJECT_MAPPER.writeValueAsString(recipeData));
			return OBJECT_MAPPER.writeValueAsString(recipeData.getRecipeName() + ":" + recipeData.getRecipeURL() + ":" + recipeData.getIngredients() + ":" + recipeData.getSteps()+ ":");
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Unable to marshall recipe data", e);
		}
	}

	@Override
	public RecipeHelperRecipeData unmarshall(Class<RecipeHelperRecipeData> clazz, String value) {
		RecipeHelperRecipeData recipe = RecipeHelperRecipeData.newInstance();
		recipe.setRecipeName(value);
		
		String[] recipeParts = value.split(":");
		recipe.setRecipeName(recipeParts[0]);
		recipe.setRecipeURL(recipeParts[1]);
		recipe.setIngredients(Arrays.asList(recipeParts[2]));
		System.out.println("INSIDE UNMARSHALL AND PRINTING STEPS. " + Arrays.asList(recipeParts[3]));
		recipe.setSteps(Arrays.asList(recipeParts[3]));
		
		return recipe;
		/*/
		try {
            return OBJECT_MAPPER.readValue(value, new TypeReference<RecipeHelper>() {
            });
        } catch (Exception e) {
            throw new IllegalStateException("Unable to unmarshall game data value", e);
        }
        /*/
	}
}

}
