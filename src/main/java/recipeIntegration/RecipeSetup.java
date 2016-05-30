package recipeIntegration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;



class RecipeSetup{

	public static Recipe RecipeBuilder() throws FileNotFoundException{ //returns a chocolate chip cookie
			int Counter = 	0;
			
			ArrayList<Ingredient> INGREDIENT_LIST = new ArrayList<Ingredient>();
			ArrayList<Step> STEP_LIST = new ArrayList<Step>();
			
			File Cookies = new File ("src/main/java/recipeIntegration/ChocChip.txt");
			Scanner reader = new Scanner(Cookies);
			@SuppressWarnings("unused")
			String firstLine = reader.nextLine();//holds "INGREDIENTS" string from top of txt file
			boolean inDirections = false;
			while (reader.hasNextLine() && !inDirections) {
				String Ingredient = reader.nextLine(); 
				//System.out.println("currently, Ingredient String is holding " + Ingredient);
				if(Ingredient.equalsIgnoreCase("PREPARATION")){
					inDirections = true;
					//System.out.println("inDirections changed to true, next while loop should operate");
					break;//if the txt file has moved into preparation
				}
				else{
				formatIngredients(Ingredient, INGREDIENT_LIST);
				}
				
			}
			reader.useDelimiter("\\.");
			while (reader.hasNextLine() && inDirections){
				String Direction = reader.next();
				//System.out.println("currently, Direction String is holding " + Direction);
				formatDirections(Direction, Counter++, STEP_LIST);
			}
			
			reader.close();
			Recipe newRecipe = new Recipe(INGREDIENT_LIST, STEP_LIST);
			return newRecipe;

			}
		
			
			public static void formatIngredients(String Ingredients, ArrayList<Ingredient> IngredientList){
				if (!Ingredients.isEmpty()){//if it isn't empty white space
					Ingredient newIngredient = new Ingredient(Ingredients, Ingredients); 
					IngredientList.add(newIngredient);
					//ITEM_SLOT.add
					//TODO figure out a way to split up quantity and name, then create an ingredient object
				}
				
			}
			
			public static void formatDirections(String Directions, int Place, ArrayList<Step> StepList){
				if (!(Directions.length() < 3)){ //if it isn't empty or just blank with a period
					Step NewStep = new Step(Place, Directions); //create a new step object 
					StepList.add(NewStep); //add to step list and increment Place
				}
				
			}
			
			


		
		
		
		
		

	}
	
