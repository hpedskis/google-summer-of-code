package recipeIntegration;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;



class RecipeSetup{

	public static Recipe RecipeBuilder() { //returns a chocolate chip cookie
			int Counter = 	0;
			
			ArrayList<Ingredient> INGREDIENT_LIST = new ArrayList<Ingredient>();
			ArrayList<Step> STEP_LIST = new ArrayList<Step>();
			/*/
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
			/*/
			//hard coded chocolate chip recipe for tesing
			Ingredient Ingredient1 = new Ingredient ("1 cup plus 2 tablespoons all-purpose flour", "1 cup plus 2 tablespoons all-purpose flour");
			INGREDIENT_LIST.add(Ingredient1);
			Ingredient Ingredient2 = new Ingredient("3/4 teaspoon kosher salt","3/4 teaspoon kosher salt");
			INGREDIENT_LIST.add(Ingredient2);
			Ingredient Ingredient3 = new Ingredient("1/2 teaspoon baking powder", "1/2 teaspoon baking powder");
			INGREDIENT_LIST.add(Ingredient3);
			Ingredient Ingredient4 = new Ingredient("3/4 (1 1/2 sticks) unsalted butter, room temperature", "3/4 (1 1/2 sticks) unsalted butter, room temperature");
			INGREDIENT_LIST.add(Ingredient4);
			Ingredient Ingredient5 = new Ingredient("3/4 cup (packed) light brown sugar", "3/4 cup (packed) light brown sugar");
			INGREDIENT_LIST.add(Ingredient5);
			Ingredient Ingredient6 = new Ingredient("1/4 cup sugar", "1/4 cup sugar");
			INGREDIENT_LIST.add(Ingredient6);
			Ingredient Ingredient7 = new Ingredient("1 large egg, room temperature", "1 large egg, room temperature");
			INGREDIENT_LIST.add(Ingredient7);
			Ingredient Ingredient8 = new Ingredient("1/2 teaspoon vanilla extract", "1/2 teaspoon vanilla extract");
			INGREDIENT_LIST.add(Ingredient8);
			Ingredient Ingredient9 = new Ingredient("1 cup semi sweet or bittersweet chocolate chips", "1 cup semi sweet or bittersweet chocolate chips");
			INGREDIENT_LIST.add(Ingredient9);
			
			Step Step1 = new Step( 0, "Arrange racks in upper and lower thirds of oven; preheat to 425°F");  
			STEP_LIST.add(Step1);
			Step Step2 = new Step( 1, "Line 2 baking sheets with parchment paper");  
			STEP_LIST.add(Step2);
			Step Step3 = new Step( 2, "Whisk flour, salt, and baking powder in a small bowl");  
			STEP_LIST.add(Step3);
			Step Step4 = new Step( 3, "Using an electric mixer on medium-high speed, beat butter and both sugars in a large bowl until well combined, 2–3 minutes");  
			STEP_LIST.add(Step4);
			Step Step5 = new Step( 4, "Add egg and vanilla; beat on medium-high speed until mixture is light and fluffy, 2–3 minutes");  
			STEP_LIST.add(Step5);
			Step Step6 = new Step( 5, "Add dry ingredients, reduce speed to low, and mix just to blend");  
			STEP_LIST.add(Step6);
			Step Step7 = new Step( 6, "Fold in chocolate chips");  
			STEP_LIST.add(Step7);
			Step Step8 = new Step( 7, "Spoon heaping tablespoonfuls of dough onto prepared baking sheets, spacing 1 1/2 inches apart");  
			STEP_LIST.add(Step8);
			Step Step9 = new Step( 8, "Bake, rotating pans halfway through, until edges are golden brown, 6–8 minutes");  
			STEP_LIST.add(Step9);
			Step Step10 = new Step( 9, "Transfer to wire racks and let cool");  
			STEP_LIST.add(Step10);
			
			
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
	
