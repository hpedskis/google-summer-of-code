package recipeIntegration;

import java.util.ArrayList;

class Recipe {
	String name;
	private ArrayList<Ingredient> IngredientList;
	private ArrayList<Step> StepList;
	
	public Recipe(ArrayList<Ingredient> IngredientList, ArrayList<Step> StepList){
		this.IngredientList = IngredientList;
		this.StepList = StepList;
	}

	
	//once name has been found, find quantity for each ingredient
	public String findIngredientQuantity(String IngredientName){
		for (int i =0; i< IngredientList.size(); i++){
			if (IngredientList.get(i).getName().equals(IngredientName)){
				String IngredientQuantity = IngredientList.get(i).getQuantity();
				return IngredientQuantity;
			}
		}
		return "Could not find the quantity for " + IngredientName;
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
}
