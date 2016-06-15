package recipeIntegration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Multi  extends Thread{
	
	public String categoryLink;
	public File OutputFile;
	public Map<String, String> OutputMap;
	public BufferedWriter f;
	
	//each thread will do a different category, 
	//synchronize code around category list
	
	public Multi(String link, File OutputFile){
		this.categoryLink = link;
		this.OutputFile = OutputFile;
		OutputMap = new HashMap<String,String>();
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
		for (int pageNumber = 1; pageNumber< 31; pageNumber ++){
    		try{
    		 RecipeDoc = Jsoup.connect(categoryLink + pageNumber).get(); //should fetch each page number's recipes
    		 System.out.println("successfully entered category " + categoryLink);
    		} catch (Exception e){
    			System.out.println("There was an issue connection to page " + pageNumber + " from the current category.");
    		}
    		System.out.println("now inside page " + pageNumber );
    		for (Element RecipeResult : RecipeDoc.select("article.grid-col--fixed-tiles a")){ //each recipe on page
    			String RecipeUrl = RecipeResult.attr("href");
    			if (!(RecipeUrl.contains("/recipe/"))){
    			//System.out.println("skipping");
    			continue;
    			}
    			else if (RecipeUrl.contains("ii") || RecipeUrl.contains("iii")){
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
        		//grab and print title from each recipe
    				Elements title = subDoc.select("h1.recipe-summary__h1");
    				String stringTitle = title.text();
    				OutputMap.put(stringTitle, RecipeUrl);
    				System.out.println("successfully added a recipe");
    				
        		}catch (Exception e){
        				System.out.println("Unable to connect to " + RecipeUrl + " from within page " + pageNumber);
        		}
        
				
		
				
				
    		}
		
		}
		for (Map.Entry<String, String> entry : OutputMap.entrySet()) {
			try {
				f.write(entry.getKey());
				f.newLine();
				f.write(entry.getValue());
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


