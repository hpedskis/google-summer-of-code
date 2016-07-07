package recipeIntegration.DynamoStorage;

import java.util.ArrayList;
import java.util.Iterator;
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
    	System.out.println("inside getIngredients.");
    	System.out.println("before: " + Ingredients);
    	List<String> REALIngredients = new ArrayList<String>();
    	String[] IngredientParts = Ingredients.toString().split(",");
    	for (String Ingredient: IngredientParts){
    		REALIngredients.add(Ingredient);
    	}
    	System.out.println("after :" + REALIngredients);
    	System.out.println(REALIngredients.get(0));
    	return REALIngredients;
    }
    public String fetchIngredient(String Ingredient) { //get ONE ingredient
    	List<String> REALIngredients = getIngredients();
    	String theCorrectIngredient = null;
		for (int i =0; i< REALIngredients.size(); i++){
			if (REALIngredients.get(i).contains(Ingredient) || StringUtils.containsIgnoreCase(REALIngredients.get(i), Ingredient) ){
				theCorrectIngredient = REALIngredients.get(i);
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
    	
    	System.out.println("inside getSteps.");
    	System.out.println("before: " + Steps);
    	List<String> REALSteps = new ArrayList<String>();
    	String[] StepParts = Steps.toString().split("\\.");
    	System.out.println("did the split work? " + StepParts[0]);
    	for (String Ingredient: StepParts){
    		REALSteps.add(Ingredient + ".");
    	}
    	System.out.println("after :" + REALSteps);
    	//System.out.println("TEST 1: testing. testing. this is the first step only" + Steps.get(0));
    	System.out.println("testing. testing. this is the first step only" + REALSteps.get(0));
 
    	return REALSteps;
    	
    }
    

    public void setSteps(List<String> Steps) {
        this.Steps = Steps;
    }

    @Override
    public String toString() {
        return "Recipe Name: " + RecipeName + " Recipe Url: " + RecipeURL + " Ingredients: " + Ingredients + " Directions: " + Steps;
    }


	public String getRecipeURL() {
		return RecipeURL;
	}


	public void setRecipeURL(String recipeURL) {
		RecipeURL = recipeURL;
	}

}
