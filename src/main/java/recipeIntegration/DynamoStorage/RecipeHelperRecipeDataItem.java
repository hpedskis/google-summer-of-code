package recipeIntegration.DynamoStorage;

import java.util.Arrays;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshaller;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * The class is used to connect the Dynamo tables to the recipe information needed for the skill. The table name 
 * is specified to be "RecipeHelperRecipeData" and the attributes that are listed in it include 
 * customerID, IngredientIndex, StepIndex, and the recipeData (which includes the title, URL, steps, and ingredients). 
 * Within each attribute, there are getters and setters. However, since the recipeData is more complicated than one data item, 
 * a Marshaller class is used.
 * 
 * The marshaller class tells Dynamo how to read the data item (this is the setter/ marshaller) and how to relay
 * the information back to the program (this is the getter/unmarshaller)
 *
 */
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
			recipe.setRecipeURL(recipeParts[1]);
			recipe.setIngredients(Arrays.asList(recipeParts[2]));
			recipe.setSteps(Arrays.asList(recipeParts[3]));

			return recipe;
		}
	}

}
