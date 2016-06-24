package recipeIntegration;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
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
	
	@SuppressWarnings("resource")
	public static String FindRecipe(String RecipeName) throws FileNotFoundException, UnsupportedEncodingException{
		
		RecipeName = StringUtils.lowerCase(RecipeName).trim();
		//InputStream is = RecipeSetup.class.getResourceAsStream("/MASTER_RECIPE.txt");
		//File testf = new File( RecipeSetup.class.getResource( "/MASTER_RECIPE.txt" ).toURI() );
		//InputStream is = RecipeSetup.class.getResourceAsStream("/MASTER_RECIPE.txt");
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream is = loader.getResourceAsStream("MASTER_RECIPE.txt");
		Scanner reader = new Scanner(new InputStreamReader(is, "UTF-8"));
		//reader = new Scanner (testf);
		
		while(reader.hasNextLine()){
			String recipeTitle = reader.nextLine();
			String recipeInfo = reader.nextLine();
			MapOfRecipes.put(StringUtils.lowerCase(recipeTitle).trim(), recipeInfo);
		}
		if (MapOfRecipes.containsKey(RecipeName)){
			System.out.println("this recipe title IS a key inside the recipe map");
			if (MapOfRecipes.containsValue(RecipeName)){
				System.out.println("this value is also listed in this map and is " + MapOfRecipes.get(RecipeName));
				return MapOfRecipes.get(RecipeName);
			}
		}
		else{
			System.out.println("this recipe title isn't a value in your map. THIS IS THE ISSUE");
		}
		for (Map.Entry<String, String> Entry : MapOfRecipes.entrySet()){
			if (StringUtils.equalsIgnoreCase(Entry.getKey(), RecipeName)){
				System.out.println("found the exact match " + Entry.getKey());
				return Entry.getValue();
			}

		}
		return null;
		
	}
	
	public static String FindBackupRecipe(String RecipeName){
		RecipeName = StringUtils.lowerCase(RecipeName).trim();
		System.out.println("inside find backup with recipe name " + RecipeName);
		for (Map.Entry<String, String> Entry : MapOfRecipes.entrySet()){
			if (StringUtils.containsIgnoreCase(Entry.getKey(), RecipeName)){
				System.out.println("found something that contained search, " + Entry.getKey());
				return Entry.getValue();
			}
			
		}
		System.out.println("this recipe name also failed out of backup Recipe, returning null");
		return null;
	}

	public static Recipe RecipeBuilder(String RecipeName) throws FileNotFoundException, UnsupportedEncodingException {
			String RecipeURL = null;
			RecipeURL = FindRecipe(RecipeName);
			if (RecipeURL == null){
				System.out.println("I couldn't find that exact title you were looking for.");
				RecipeURL = FindBackupRecipe(RecipeName);
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
				System.out.println("inside format Directions");
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
				
		
		
		
	
