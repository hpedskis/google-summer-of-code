package recipeIntegration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Multi  extends Thread{
	
	public String categoryLink;
	public File OutputFile;
	public HashSet<String> OutputMap;
	public BufferedWriter f;
	
	//each thread will do a different category, 
	//synchronize code around category list
	
	public Multi(String link, File OutputFile){
		this.categoryLink = link;
		this.OutputFile = OutputFile;
		OutputMap = new HashSet<String>();
     	try {
			f = new BufferedWriter (new FileWriter (OutputFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	@Override
	public void run(){
		Document RecipeDoc = null;
		for (int pageNumber = 1; pageNumber< 10; pageNumber ++){
    		try{
    		 RecipeDoc = Jsoup.connect(categoryLink + pageNumber).get(); //should fetch each page number's recipes
    		 System.out.println("successfully entered category " + categoryLink);
    		} catch (Exception e){
    			System.out.println("There was an issue connection to page  from the current category.");
    		}
    		System.out.println("now inside page " + pageNumber);
    		for (Element RecipeResult : RecipeDoc.select("article.grid-col--fixed-tiles a")){ //each recipe on page
    			String RecipeUrl = RecipeResult.attr("href");
    			if (!(RecipeUrl.contains("/recipe/"))){
    			continue;
    			}
    			Document subDoc = null;
    			try{
    				if(!(RecipeUrl.contains("http"))){
    					subDoc = Jsoup.connect("http://allrecipes.com/" + RecipeUrl).get();
    				}
    				else{
    					subDoc = Jsoup.connect(RecipeUrl).get();
    				}
    				for (Element IngredientResult : subDoc.select("li.checkList__line")){
            			String ingredient = IngredientResult.text();
            			ingredient = ingredient.replace("ADVERTISEMENT", "");
            			ingredient = ingredient.replace("Add all ingredients to list", "");
            			OutputMap.add(ingredient);
            
    				}
    				
        		}catch (Exception e){
        				System.out.println("Unable to connect to " + RecipeUrl + " from within page ");
        		}
        
				
		
				
				
    		}
		
		}
		for (String s: OutputMap) {
			try {
				f.write(s);
				f.newLine();
			} catch (IOException e) {
				System.out.println("something went wrong when adding a recipe");
			}
			//System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			}
		try {
			f.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("couldn't close file");
		}
		
		
	}
	
	
}


