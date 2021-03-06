package recipeIntegration;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.User;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@SuppressWarnings("unused")
class MainForTestin{

	/**
	 * This method was used for local testing and any set-up
	 * The first section is used to test locally by giving Dynamo and AWS false request, session, intents, etc
	 * as these are crucial for testing any code in this skill.
	 * 
	 * Next, there are some test lines for ingredient matching (using getFuzzyDistance)
	 * 
	 * The bottom sections were used for building the ingredient and recipe databases
	 * through multi-threading and processing methods. Each thread wrote to a different file, then
	 * each was processed to get rid of symbols, duplicates, or numerics. Then they were written to one file,
	 * MASTER_RECIPE.txt which can be found in Resources.
	 * 
	 * Below this is all the manipulation to create the INGREDIENT_LIST, which is a custom slot type. In the custom
	 * slot type, any ingredient that could ever be asked about should be listed. This is harder than it sounds, so only
	 * 20 pages were gone through. All ingredients were scraped, and then processed to remove word that clearly weren't 
	 * ingredients (such as measurements) and duplicates. 
	 * 
	 * THIS CLASS IS NOT NECESSARY FOR THE RUNNING OF THE PROGRAM THROUGH LAMBDA OR AWS.
	 *
	 * @param args Not used
	 */
	public static void notmain(String[] args) throws SpeechletException, IOException {
		
		//TODO this section of code allows for testing locally. Different intents can be entered
		
		/*/
		User newUser = User.builder().withUserId("dfs98989998").build();
		Session newSession = Session.builder().withUser(newUser).withSessionId("123").build(); 
		LaunchRequest newLaunchRequest = LaunchRequest.builder().withRequestId("321").build();
		
		Intent realIntent = Intent.builder().withName("GetIngredientOverview").build();
		IntentRequest newIntent = IntentRequest.builder().withIntent(realIntent).withRequestId("345").build();
		RecipeSpeechlet newRecipe = new RecipeSpeechlet();
		SpeechletResponse LaunchOutput = newRecipe.onLaunch(newLaunchRequest, newSession);
		SpeechletResponse LaunchOnIntent = newRecipe.onIntent(newIntent, newSession);
		
		int test = StringUtils.getFuzzyDistance("rolled oats", "oatmeal", Locale.ENGLISH);
		System.out.println(StringUtils.contains("all purpose flou", "flou"));
		int test2 = StringUtils.getFuzzyDistance("chocolate", "chocolate chips", Locale.ENGLISH);
		System.out.println(test );
		System.out.println(test2);
		
		
		
		
		//TODO This section allows for re-writing to the output files using multithreading  

		File input1 = new File ("/Users/hpedskis/Desktop/recipes_1.txt");
		File input2 = new File ("/Users/hpedskis/Desktop/recipes_2.txt");
		File input3 = new File ("/Users/hpedskis/Desktop/recipes_3.txt");
		File input4 = new File ("/Users/hpedskis/Desktop/recipes_4.txt");
		File input5 = new File ("/Users/hpedskis/Desktop/recipes_5.txt");
		File outputFile = new File ("/Users/hpedskis/Desktop/AllRecipes_Ingredients.txt");
		
		Multi T1 = new Multi(input1, outputFile);
		Multi T2 = new Multi(input2, outputFile);
		Multi T3 = new Multi(input3, outputFile);
		Multi T4 = new Multi(input4, outputFile);
		Multi T5 = new Multi(input5, outputFile);
		
		T1.start();
		T2.start();
		T3.start();
		T4.start();
		T5.start();
		/*/
		
		/*/
	 	File T1 = new File("src/Recipe_Outputs/Breakfast.txt");
	 	File T2 = new File("src/Recipe_Outputs/Chicken.txt");
	 	File T3 = new File("src/Recipe_Outputs/Dessert.txt");
	 	File T4 = new File("src/Recipe_Outputs/Healthy.txt");
	 	File T5 = new File("src/Recipe_Outputs/Holidays.txt");
	 	File T6 = new File("src/Recipe_Outputs/Magazine.txt");
	 	File T7 = new File("src/Recipe_Outputs/Main.txt");
	 	File T8 = new File("src/Recipe_Outputs/Quick.txt");
	 	File T9 = new File("src/Recipe_Outputs/Slow.txt");
	 	File T10 = new File("src/Recipe_Outputs/Trusted.txt");
	 	File T11= new File("src/Recipe_Outputs/Vegetarian.txt");
	 	File T12 = new File("src/Recipe_Outputs/Appetizers.txt");
     	
	 	
	 	Multi t1 = new Multi("http://allrecipes.com/recipes/78/breakfast-and-brunch/?page=", T1);
	 	Multi t2 = new Multi("http://allrecipes.com/recipes/201/meat-and-poultry/chicken/?page=", T2);
	 	Multi t3 = new Multi("http://allrecipes.com/recipes/79/desserts/?page=", T3);
	 	Multi t4 = new Multi("http://allrecipes.com/recipes/84/healthy-recipes/?page=", T4);
	 	Multi t5 = new Multi("http://allrecipes.com/recipes/85/holidays-and-events/?page=", T5);
	 	Multi t6 = new Multi("http://allrecipes.com/recipes/17235/everyday-cooking/allrecipes-magazine-recipes/?page=", T6);
	 	Multi t7 = new Multi("http://allrecipes.com/recipes/80/main-dish/?page=", T7);
	 	Multi t8 = new Multi("http://allrecipes.com/recipes/1947/everyday-cooking/quick-and-easy/?page=", T8);
	 	Multi t9 = new Multi("http://allrecipes.com/recipes/253/everyday-cooking/slow-cooker/?page=", T9);
	 	Multi t10 = new Multi("http://allrecipes.com/recipes/82/trusted-brands-recipes-and-tips/?page=", T10);
	 	Multi t11 = new Multi("http://allrecipes.com/recipes/87/everyday-cooking/vegetarian/?page=", T11);
	 	Multi t12 = new Multi("http://allrecipes.com/recipes/76/appetizers-and-snacks/?page=", T12);
	 	
	 	t1.start();
	    t2.start();
	    t3.start();
	    t4.start();
	    t5.start();
	    t6.start();
	    t7.start();
	    t8.start();
	    t9.start();
	    t10.start();
	    t11.start();
	    t12.start();
		
		/*/
		// TODO any manipulation to the ingredient files can be done in this section
		
		File FirstFixIngredients = new File ("/Users/hpedskis/Desktop/Recipes_FirstFix");
		File SecondFix = new File ("/Users/hpedskis/Desktop/Recipes_SecondFix");
		Scanner reader = new Scanner(FirstFixIngredients);
		BufferedWriter writer = new BufferedWriter (new FileWriter (SecondFix));
		HashSet<String> IngredientSet = new HashSet<String>();
		
		while(reader.hasNextLine()){
			IngredientSet.add(reader.nextLine());
		}
		for (String s: IngredientSet){
			writer.write(s);
			writer.newLine();
		}
		writer.close();

		/*/
		File currentRecipe = new File ("/Users/hpedskis/Desktop/GoogleSummerofCode/recipe-integration-skill/src/main/resources/MASTER_RECIPE.txt");
		File FixingRecipes = new File ("/Users/hpedskis/Desktop/GoogleSummerofCode/MASTER_RECIPE_BACKUP.txt");
		
		File masterTitles = new File ("/Users/hpedskis/Desktop/GoogleSummerofCode/MASTER_TITLE_BACKUP.txt");
		HashSet<String> IngredientsForRecipes = new HashSet<String>();
		Map<String, String> MapOfRecipes = new HashMap<String,String>();
		
		Scanner reader = new Scanner(currentRecipe);
		BufferedWriter writer = new BufferedWriter (new FileWriter (FixingRecipes));
		BufferedWriter writer2 = new BufferedWriter (new FileWriter (masterTitles));
		/*/
		/*/
		while(reader.hasNextLine()){
			MapOfRecipes.put(reader.nextLine(), reader.nextLine());
		}
		for (Map.Entry<String, String> Entry: MapOfRecipes.entrySet()){
			String recipeTitle = Entry.getKey();
			if (StringUtils.endsWith(recipeTitle, "VI")){
				recipeTitle = StringUtils.removeEnd(recipeTitle, "VI");
			}
			if (StringUtils.endsWith(recipeTitle, "IV")){
				recipeTitle = StringUtils.removeEnd(recipeTitle, "IV");
			}
			if (StringUtils.endsWith(recipeTitle, "I")){
				recipeTitle = StringUtils.removeEnd(recipeTitle, "I");
			}
			if (StringUtils.endsWith(recipeTitle, "II")){
				recipeTitle = StringUtils.removeEnd(recipeTitle, "II");
			}
			if (StringUtils.endsWith(recipeTitle, "III")){
				recipeTitle = StringUtils.removeEnd(recipeTitle, "III");
			}
			if (StringUtils.endsWith(recipeTitle, "V")){
				recipeTitle = StringUtils.removeEnd(recipeTitle, "V");
			}
			if (StringUtils.contains(recipeTitle, "'")){
				recipeTitle = StringUtils.replace(recipeTitle, "'",  "");
			}
			if (StringUtils.contains(recipeTitle, "-")){
				recipeTitle = StringUtils.replace(recipeTitle, "-",  " ");
			}
			if (StringUtils.contains(recipeTitle, ",")){
				recipeTitle = StringUtils.replace(recipeTitle, "," ,  "");
			}
			if (StringUtils.contains(recipeTitle, "(")){
				recipeTitle = recipeTitle.replaceAll("\\(.*?\\)","");
			}
			writer.write(StringUtils.lowerCase(recipeTitle));
			writer.newLine();
			writer.write(Entry.getValue());
			writer.newLine();
			
			writer2.write(recipeTitle);
			writer2.newLine();
			
		}
		/*/
		
		
	}
	
	
	
	public static String splitAndFix (String line){
		
		ArrayList<String> BadWords = new ArrayList<String>(Arrays.asList("cups", "tablespoons", "teaspoons", "teaspoon", "cup", "tablespoon", "pound", "pounds", "ounces", "ounce", "cloves", "clove", "package", "packages", "stalks", "sprigs", "slices", "such", "or", "to", "taste", "and",
				"room", "temperature", "cold", "hot", "medium", "for", "coating", "mix", "can", "into", "cut", "chunk", "rinsed", "portions", "wet", "dry", "fresh", "sliced", "extra", "cans", "warmed", "cooled",
				"thinly", "chopped", "wide", "inch", "inches", "light", "pinches", "drained", "jar", "jars", "finely", "halved",  "lengthwise", "seeded", "cubes", "cubed", ""));
		
		ArrayList<String> SecondBadWords = new ArrayList<String> (Arrays.asList("frozen", "thawed", "diced", "boneless", "minced", "uncooked", "cooked", "skinless", "long", 
				"thick", "thin", "large", "medium", "small", "peeled", "cored", "seperated", "divided", "with", "juiced", "strips", "box", "boxed", "sifted", "seeded", "seedless",
				"pieces", "piece", "grated", "casing", "removed", "more", "as", "needed", "to", "taste", "chunk", "chunks"));
		String[] parts = line.split(" ");
		StringBuilder fixed = new StringBuilder();
		for (String s: parts){
			if (SecondBadWords.contains(s)){
				continue;
			}
			fixed.append(s + " ");
		}
		//String fixedString = parts.toString();
		return fixed.toString();
		
	}
	
	}
	
	













