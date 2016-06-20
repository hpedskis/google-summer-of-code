package recipeIntegration;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


class RecipeSetup{
	
	public static Map<String, String> MapOfRecipes = new HashMap<String,String>();
	
	//when a user passes in a string title, create a map from the text file, find that recipe, or return that it wasn't found.
	public static String FindRecipe(String RecipeName){ //takes a name, searches the map, returns the URL
		System.out.println("trying to find recipe...");
		//Thread.currentThread().getContextClassLoader().getResourceAsStream("MASTER_RECIPE.txt");
		InputStream in = RecipeSetup.class.getResourceAsStream("MASTER_RECIPE.txt");
		
		Scanner reader = null;
		try {
			reader = new Scanner(new InputStreamReader(in, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(reader.hasNextLine()){
			String recipeTitle = reader.nextLine();
			String recipeInfo = reader.nextLine();
			MapOfRecipes.put(StringUtils.lowerCase(recipeTitle), StringUtils.lowerCase(recipeInfo)); //put everything in map lower case
		}
		System.out.println("made map. I think ;)"); 
		reader.close();
		
		if (MapOfRecipes.get(RecipeName) != null){
			System.out.println(MapOfRecipes.get(RecipeName));
			return MapOfRecipes.get(RecipeName);
		}
		return null;
		
	}

	public static Recipe RecipeBuilder(String RecipeName) throws FileNotFoundException {
			String RecipeURL = FindRecipe(RecipeName);
			if (RecipeURL == null){
				System.out.println("couldnt find the recipe, so recipe builder failed");
				return null;
			}
			
			ArrayList<Ingredient> INGREDIENT_LIST = new ArrayList<Ingredient>();
			ArrayList<Step> STEP_LIST = new ArrayList<Step>();
			
				String RecipeTitle = RecipeName;
				System.out.println(RecipeTitle);
				Document InRecipe = null;
				try {
					InRecipe = Jsoup.connect("http://allrecipes.com/" + RecipeURL).get();
				} catch (IOException e) {
					System.out.println("Cannot connect to recipe");
				} 
				for (Element IngredientResult : InRecipe.select("li.checkList__line")){
        			String ingredient = IngredientResult.text();
        			
        			ingredient = ingredient.replace("ADVERTISEMENT", "");
        			ingredient = ingredient.replace("Add all ingredients to list", "");
        		
        			formatIngredients(ingredient, INGREDIENT_LIST);
        		}
				
        		for (Element DirectionResult : InRecipe.select("div.directions--section__steps ol")){
        			String direction = DirectionResult.text();
        			direction = direction.replace("ADVERTISEMENT", "");

        			formatDirections(direction.trim(), STEP_LIST);
       
        		}
	
			
			Recipe newRecipe = new Recipe(INGREDIENT_LIST, STEP_LIST, RecipeTitle);
			System.out.println("finished making recipe, returning");
			return newRecipe;

			}
		
			
			public static void formatIngredients(String Ingredients, ArrayList<Ingredient> IngredientList){
				if (!Ingredients.isEmpty()){//if it isn't empty white space
					Ingredient newIngredient = new Ingredient(Ingredients, Ingredients); 
					IngredientList.add(newIngredient);
					
				}
				
			}	
			public static void formatDirections(String Directions, ArrayList<Step> StepList){
				////if it isn't empty or just blank with a period
				System.out.println("inside firomat Directions");
					int CurrentStep = 0;
					String[] parts = Directions.split(Pattern.quote(".").trim());;
					for (int i =0; i< parts.length; i++){
						Step NewStep = new Step(CurrentStep++, parts[i]);
						System.out.println("added a new step at 'current step' " + CurrentStep + " which was " + parts[i]);
						if (!(parts[i].length() < 2)){//create a new step object 
						StepList.add(NewStep);
						
					}
				
		
				
			}
		
			}
}
				
		
		
		
	
