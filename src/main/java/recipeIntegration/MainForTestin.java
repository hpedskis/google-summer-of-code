package recipeIntegration;
import java.io.BufferedWriter;
import java.io.File;

import java.io.FileWriter;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

class MainForTestin{


	public static void main(String[] args) throws SpeechletException, IOException {
		
		
		Session newSession = Session.builder().withSessionId("123").build(); 
		LaunchRequest newLaunchRequest = LaunchRequest.builder().withRequestId("321").build();
		
		Intent realIntent = Intent.builder().withName("GetIngredientInformation").build();
		IntentRequest newIntent = IntentRequest.builder().withIntent(realIntent).withRequestId("345").build();
		RecipeSpeechlet newRecipe = new RecipeSpeechlet();
		SpeechletResponse LaunchOutput = newRecipe.onLaunch(newLaunchRequest, newSession);
		
		SpeechletResponse LaunchOnIntent = newRecipe.onIntent(newIntent, newSession);
	
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

	}	
}











