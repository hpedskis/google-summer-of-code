package recipeIntegration;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.amazon.speech.speechlet.Session;

import recipeIntegration.DynamoStorage.RecipeHelper;
import recipeIntegration.DynamoStorage.RecipeHelperDao;


class RecipeSetup{
	
	public static Map<String, String> MapOfRecipes = new HashMap<String,String>();
	
	//when a user passes in a string title, create a map from the text file, find that recipe, or return that it wasn't found.
	
	@SuppressWarnings("resource")
	public static String FindRecipe(String RecipeName) throws FileNotFoundException, UnsupportedEncodingException{
		
		RecipeName = StringUtils.lowerCase(RecipeName).trim();

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream is = loader.getResourceAsStream("MASTER_RECIPE.txt");
		Scanner reader = new Scanner(new InputStreamReader(is, "UTF-8"));
		
		
		while(reader.hasNextLine()){
			String recipeTitle = reader.nextLine();
			String recipeInfo = reader.nextLine();
			MapOfRecipes.put(StringUtils.lowerCase(recipeTitle).trim(), recipeInfo);
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
				return Entry.getValue();
			}
			
		}
		
		return null;
	}

	public static void RecipeBuilder(Session session, String RecipeName, RecipeHelper recipe, RecipeHelperDao recipeHelperDao) throws FileNotFoundException, UnsupportedEncodingException {
		System.out.println("INSIDE RECIPE BUILDER");	
		String RecipeURL = null;
			RecipeURL = FindRecipe(RecipeName);
			if (RecipeURL == null){
				RecipeURL = FindBackupRecipe(RecipeName); //what about if this returns null?
			}
			
			List<String> INGREDIENT_LIST = new ArrayList<String>();
			List<String> STEP_LIST = new ArrayList<String>();
			
				String RecipeTitle = RecipeName;
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
        		
        			INGREDIENT_LIST = formatIngredients(ingredient, INGREDIENT_LIST);
        		}
				
				
        		for (Element DirectionResult : InRecipe.select("div.directions--section__steps ol")){
        			String direction = DirectionResult.text();
        			direction = direction.replace("ADVERTISEMENT", "");
        			System.out.println("printing current direction " + direction);
        			STEP_LIST = formatDirections(direction.trim(), STEP_LIST);
       
        		}
        		
        		
        		recipe.setRecipeName(RecipeTitle);
        		recipe.setIngredients(INGREDIENT_LIST);
        		recipe.setSteps(STEP_LIST);
        		System.out.println("testing if ingredient list properly saved " + recipe.getAllIngredients());
        		recipe.setRecipeURL(RecipeURL);
        		//System.out.println("testing ingr-list... ingredient 2 is " + INGREDIENT_LIST.get(1));
        		
        		recipeHelperDao.saveCurrentRecipe(recipe);


			}
		
			
			public static List<String> formatIngredients(String Ingredients, List<String> IngredientList){
				//System.out.println("inside formatIngredients");
				if (!Ingredients.isEmpty()){//if it isn't empty white space 
					//System.out.println("adding an ingredient " + Ingredients);
					IngredientList.add(Ingredients);
					
				}
				return IngredientList;
				
			}	
			public static List<String> formatDirections(String Directions, List<String> StepList){
				System.out.println("FORMATING DIRECTIONS. BELOW IS WHAT IS GOING WRONG");
					String[] parts = Directions.split("\\.");
					System.out.println(parts.toString());
					for (int i =0; i< parts.length; i++){
						System.out.println("CURRENT PART: " + parts[i]);
						System.out.println("CURRENT PART LENGTH: " + parts[i].length());
						if (parts[i].length() > 3){
							System.out.println(parts[i] + ".");
							StepList.add(i, parts[i].trim() + ".");
						}
						else{
							System.out.println("just skipped a step, "+ parts[i]);
						}
				
					}
					return StepList;
			}
}
				
		
		
		
	
