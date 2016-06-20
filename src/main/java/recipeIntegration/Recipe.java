package recipeIntegration;

import java.util.ArrayList;

class Recipe {
	String name;
	private ArrayList<Ingredient> IngredientList;
	private ArrayList<Step> StepList;
	
	public Recipe(ArrayList<Ingredient> IngredientList, ArrayList<Step> StepList, String name){
		this.IngredientList = IngredientList;
		this.StepList = StepList;
		this.name = name;
	}


	public ArrayList<Ingredient> getIngredientList() {
		return IngredientList;
	}
	public int getIngredientListSize(){
		ArrayList<Ingredient> IngredientList = getIngredientList();
		int size = IngredientList.size();
		return size;
	}
	public int getStepListSize(){
		ArrayList<Step> StepList = getStepList();
		int size = StepList.size();
		return size;
	}

	public ArrayList<Step> getStepList() {
		return StepList;
	}


	public void setStepList(ArrayList<Step> stepList) {
		StepList = stepList;
	}
	
	public String getName(){
		return name;
	}
	
	public Ingredient getIngredient(String ingredientName){
	Ingredient theCorrectIngredient = null;
		for (int i =0; i< IngredientList.size(); i++){
			if (IngredientList.get(i).getName().contains(ingredientName)){
				theCorrectIngredient = IngredientList.get(i);
				return theCorrectIngredient;
			}
		}
		return theCorrectIngredient;
	}
}
