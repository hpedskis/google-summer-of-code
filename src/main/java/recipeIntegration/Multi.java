package recipeIntegration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * The class was used to create the recipe outputs, from allrecipes.com, using
 * multi-threading. Each category on the website was traversed by a different
 * thread. 20 pages of each category were taken and put into different text
 * files. All the logic for running the threads and processing them can be found
 * in MainForTestin.
 * 
 * Currently, this has been changed to create the ingredient list. the code from the recipe outputs
 * is formatted in comments below.
 * THIS CLASS IS NOT NECCESARY FOR RUNNING THE SKILL THROUGH LAMBDA OR AWS.
 *
 */
public class Multi extends Thread {

	public String categoryLink;
	public File inputFile;
	public File OutputFile;
	public HashSet<String> OutputMap;
	public BufferedWriter f;
	public Scanner reader;

	// each thread will do a different category,
	// synchronize code around category list

	public Multi(File inputFile, File OutputFile) {
		this.inputFile = inputFile;
		this.OutputFile = OutputFile;
		OutputMap = new HashSet<String>();
		try {
			reader = new Scanner(inputFile);
			f = new BufferedWriter(new FileWriter(OutputFile));
			System.out.println("just created new scanner and buffered writer");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		while (reader.hasNext()) {
			String name = reader.nextLine(); // skip the title
			System.out.println(name);
			String URL = reader.nextLine();
			Document RecipeDoc = null;
			try {
				RecipeDoc = Jsoup.connect("http://allrecipes.com/" + URL).get(); 
				System.out.println("just connected to link");
				for (Element IngredientResult : RecipeDoc.select("li.checkList__line")) {
					String ingredient = "";
					try{
						ingredient = IngredientResult.text();
						ingredient = ingredient.replace("ADVERTISEMENT", "");
						ingredient = ingredient.replace("Add all ingredients to list", "");
						OutputMap.add(ingredient);
						System.out.println("just added an ingredient to the map " + ingredient);
					} catch(Exception e){
						System.out.println("issue with one ingredient, moving on");
					}

				}
			} catch (Exception e) {
				System.out.println("There was an issue connecting to recipe " + name);
			}
			
		}
			for (String s: OutputMap) { 
				try { 
					f.write(s); 
					f.newLine(); 
				} catch (IOException e) {
						System.out.println("something went wrong when adding an ingredient"); 
				}
				
			 } try { 
				 f.close(); 
			} catch (IOException e) {
			 System.out.println("couldn't close file"); 
			}

	}
}

		/*
		 * / public Multi(String link, File OutputFile){ this.categoryLink =
		 * link; this.OutputFile = OutputFile; OutputMap = new
		 * HashSet<String>(); try { f = new BufferedWriter (new FileWriter
		 * (OutputFile)); } catch (IOException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); }
		 * 
		 * } /
		 */
		// @Override
		/*
		 * / public void run(){ Document RecipeDoc = null; for (int pageNumber =
		 * 1; pageNumber< 10; pageNumber ++){ try{ RecipeDoc =
		 * Jsoup.connect(categoryLink + pageNumber).get(); //should fetch each
		 * page number's recipes
		 * System.out.println("successfully entered category " + categoryLink);
		 * } catch (Exception e){ System.out.println(
		 * "There was an issue connection to page  from the current category.");
		 * } System.out.println("now inside page " + pageNumber); for (Element
		 * RecipeResult : RecipeDoc.select("article.grid-col--fixed-tiles a")){
		 * //each recipe on page String RecipeUrl = RecipeResult.attr("href");
		 * if (!(RecipeUrl.contains("/recipe/"))){ continue; } Document subDoc =
		 * null; try{ if(!(RecipeUrl.contains("http"))){ subDoc =
		 * Jsoup.connect("http://allrecipes.com/" + RecipeUrl).get(); } else{
		 * subDoc = Jsoup.connect(RecipeUrl).get(); } for (Element
		 * IngredientResult : subDoc.select("li.checkList__line")){ String
		 * ingredient = IngredientResult.text(); ingredient =
		 * ingredient.replace("ADVERTISEMENT", ""); ingredient =
		 * ingredient.replace("Add all ingredients to list", "");
		 * OutputMap.add(ingredient);
		 * 
		 * }
		 * 
		 * }catch (Exception e){ System.out.println("Unable to connect to " +
		 * RecipeUrl + " from within page "); }
		 * 
		 * 
		 * 
		 * 
		 * 
		 * }
		 * 
		 * //} for (String s: OutputMap) { try { f.write(s); f.newLine(); }
		 * catch (IOException e) {
		 * System.out.println("something went wrong when adding a recipe"); }
		 * //System.out.println("Key = " + entry.getKey() + ", Value = " +
		 * entry.getValue()); } try { f.close(); } catch (IOException e) { //
		 * TODO Auto-generated catch block
		 * System.out.println("couldn't close file"); } /
		 */



