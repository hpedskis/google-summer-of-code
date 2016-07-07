package recipeIntegration.DynamoStorage;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


public class RecipeHelperRecipeData {
	private List<String> Ingredients;
    private List<String> Steps;
    private String RecipeName;
    private String RecipeURL;

    public RecipeHelperRecipeData() {
        // public no-arg constructor required for DynamoDBMapper marshalling
    }

    
    public static RecipeHelperRecipeData newInstance() {
    	RecipeHelperRecipeData newInstance = new RecipeHelperRecipeData();
        newInstance.setIngredients(new ArrayList<String>());
        newInstance.setSteps(new ArrayList<String>());
        newInstance.RecipeName = null;
        newInstance.RecipeURL = null;
        return newInstance;
    }

    public String getRecipeName(){
    	return RecipeName;
    }
    public void setRecipeName(String RecipeName){
    	this.RecipeName = RecipeName;
    }
    public List<String> getIngredients(){ //gets list of ingredients 
    	return Ingredients;
    }
    public String fetchIngredient(String Ingredient) { //get ONE ingredient
    	String theCorrectIngredient = null;
		for (int i =0; i< Ingredients.size(); i++){
			if (Ingredients.get(i).contains(Ingredient) || StringUtils.containsIgnoreCase(Ingredients.get(i), Ingredient) ){
				theCorrectIngredient = Ingredients.get(i);
				return theCorrectIngredient;
			}
		}
		return theCorrectIngredient;
	}
    @JsonIgnoreProperties
    public int getIngredientListSize(){
		return Ingredients.size();
    }
    public int getStepListSize(){
    	return Steps.size();
    }

    public void setIngredients(List<String> Ingredients) {
        this.Ingredients = Ingredients;
    }

    public List<String> getSteps() {
        return Steps;
    }

    public void setSteps(List<String> Steps) {
        this.Steps = Steps;
    }

    @Override
    public String toString() {
        return "Recipe Name: " + RecipeName + " Recipe Url: " + RecipeURL + " [RecipeHelperRecipeData " + Ingredients + "] Steps: " + Steps + "]";
    }


	public String getRecipeURL() {
		return RecipeURL;
	}


	public void setRecipeURL(String recipeURL) {
		RecipeURL = recipeURL;
	}

}
