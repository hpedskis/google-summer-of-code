package recipeIntegration.DynamoStorage;

import java.util.Arrays;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@DynamoDBTable(tableName = "RecipeHelperRecipeData")
public class RecipeHelperRecipeDataItem {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private String customerId;
	private int IngredientIndex;
	private int StepIndex;

	private RecipeHelperRecipeData recipeData; // recipe information

	@DynamoDBHashKey(attributeName = "CustomerId")
	public String getCustomerId() {

		return customerId;
	}
	
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	@DynamoDBAttribute(attributeName = "IngredientIndex")
	public int getIngredientIndex(){
		return IngredientIndex;
	}
	
	public void setIngredientIndex(int index){
		this.IngredientIndex = index;
	}
	
	@DynamoDBAttribute(attributeName = "StepIndex")
	public int getStepIndex(){
		return StepIndex;
	}
	
	public void setStepIndex(int index){
		this.StepIndex = index;
	}

	@DynamoDBAttribute(attributeName = "Data")
	@DynamoDBMarshalling(marshallerClass = RecipeHelperRecipeDataMarshaller.class)
	public RecipeHelperRecipeData getRecipeData() { 
		return recipeData;
	}

	public void setRecipeData(RecipeHelperRecipeData recipeData) {
		this.recipeData = recipeData;
	}

	public static class RecipeHelperRecipeDataMarshaller implements
			DynamoDBMarshaller<RecipeHelperRecipeData> {

		@Override
		public String marshall(RecipeHelperRecipeData recipeData) {
			try {
				System.out.println("THIS IS WHAT'S THE NAME DURING MARSHALLING " + recipeData.getRecipeName());
				return OBJECT_MAPPER.writeValueAsString(recipeData.getRecipeName()
						+ ":"
						+ recipeData.getRecipeURL()
						+ ":"
						+ recipeData.getIngredients()
						+ ":"
						+ recipeData.getSteps() 
						+ ":");
			} catch (JsonProcessingException e) {
				throw new IllegalStateException(
						"Unable to marshall recipe data", e);
			}
		}

		@Override
		public RecipeHelperRecipeData unmarshall(
				Class<RecipeHelperRecipeData> clazz, String value) {
			RecipeHelperRecipeData recipe = RecipeHelperRecipeData
					.newInstance();
			recipe.setRecipeName(value);

			String[] recipeParts = value.split(":");
			recipe.setRecipeName(recipeParts[0]);
			System.out.println("THIS IS THE NAME IT'S UNMARSHALLING " + recipe.getRecipeName());
			recipe.setRecipeURL(recipeParts[1]);
			System.out.println(recipe.getRecipeURL());
			recipe.setIngredients(Arrays.asList(recipeParts[2]));
			recipe.setSteps(Arrays.asList(recipeParts[3]));

			return recipe;
		}
	}

}
