# Hannah Pedersen's Google-Summer-Of-Code 2016 
#Alexa Recipe Integration Skill

##Concept/ Overview

This summer, for Google Summer of Code, I’ve created a skill for the Amazon Echo which will assist a user in cooking any of the recipes listed in MASTER_RECIPES.txt (All are open source and scraped from allrecipes.com using Jsoup and multi-threading). Within src/main, there are two folders, java/recipeIntegration and resources. resources has all of the .txt files needed for setting up the skill through AWS, including the intent schema, the slot types for ingredients and recipes, and the sample utterances. 

The file java/recipeIntegration houses all the code needed to run the program, including all of the file DynamoStorage, containing files for saving state. 
###A brief overview of the files in java/recipeIntegration is below:
	-*MainForTestin*: not necessary for running the program, but left in to show some of the background work in creating the recipe file, the recipe slot file, the ingredient slot file, and any setup required for local testing.  
	-*Multi*: Also not needed for running the program, but shows how multi-threading was used to grab all of the recipes from allrecipes.com
	-*RecipeHelperManager*: Includes all of the logic for answering any intents (questions or commands) from the user. This includes getting start and end responses, setting up a new recipe, and answering any questions about steps or ingredients of the current recipe.
	-*RecipeLamda*: A necessary override function for the Lambda to properly work. Sets up the application ID, and sets up everything needed to handle further requests. 
	-*RecipeSetup*: Called when the user hasn't set up a recipe yet or wants a new recipe. Searches for the recipe in the list, connects to the URL, and writes the ingredients and steps to DynamoDB.
	-*RecipeSpeechlet*: The first file to be called when any session is started. Sets the RequestID and SessionID, recognizes which intent the user would like, and passes that intent to the matching method inside RecipeHelperManager
	-*SpeechletLamda*: This is the launcher for the Lambda and AWS. Checks for any issues in launching, creating output to the app, or logging the information.

In order to save state, I used DynamoDB, a feature accessible through Amazon Developer Portal. A table is created
based on each customerID and the current recipe is saved, along with the ingredients, steps, etc. This way, even after the Echo times out, the recipe that the user is cooking will always be held until they reset it. 
###A brief overview of the files in DynamoStorage is below:
	-*RecipeHelper*: Sets up a new session, saving the customerID and the recipeData (which is all held in a different class). Most importantly, has all the getters and setters from the RecipeDataRecipeData
	-*RecipeHelperDao*: gets the current session based on the customerID at each start and sets up a new instance
	to be referenced. Also contains the saveCurrentRecipe method, referenced throughout RecipeHelperManager
	-*RecipeHelperDynamoDbClient*: loads items and saves items to the table. Also checks if the recipe is null/ if a new recipe needs to be set up.
	-*RecipeHelperRecipeData*: a class to hold all the data of the recipe. This includes the title, url, steps, and ingredients
	-*RecipeHelperRecipeDataItem*: has all the attributes which are listed in the table. The customerID, current step, current ingredient, and the RecipeHelperRecipeData are each different attributes (the RecipeHelperRecipeData has all the class information clumped together in one block). This class also has the DynamoDbMarshaller, which allows for the information to be written to the read from the table. 
	 
	

##Setup

To run this code, you must have Apache Maven and an AWS account (the AWS account is free and easy to set up). These steps are taken from Amazon's sample java skills, but I have adapted them to work for my skill.

**YOU DO NOT NEED AN AMAZON ECHO TO TEST THIS. You can see the output through the AWS Console. However, it will appear as if it’s failing through the Lambda console because of the Dynamo customerID/ applicationID requirements. **

###AWS Lambda Setup

1. Go to the AWS Console and click on the Lambda link. Note: ensure you are in us-east or you wont be able to use Alexa with Lambda.
2. Click on the Create a Lambda Function or Get Started Now button.
3. Skip the blueprint
4. Name the Lambda Function "Recipe-Skill-Example". (although this name doesn't matter)
5. Select the runtime as Java 8
6. Go to the the google-summer-of-code/ directory containing pom.xml, and run 'mvn assembly:assembly -DdescriptorId=jar-with-dependencies package'. This will generate a zip file named "Recipe-Skill-Example-1.0-jar-with-dependencies.jar" in the target directory.
7. Select Code entry type as "Upload a .ZIP file" and then upload the "Recipe-Skill-Example-1.0-jar-with-dependencies.jar" file from the target directory to Lambda
8. Set the Handler as recipeIntegration.RecipeLambda::handleRequest (it is very important that this name is copied EXACTLY. Otherwise Lambda won't be able to upload your code).
9. Create a "Basic with DynamoDB" role and click create.
10. Leave the Advanced settings as the defaults.
11. Click "Next" and review the settings then click "Create Function"
12. Click the "Event Sources" tab and select "Add event source"
13. Set the Event Source type as Alexa Skills kit and Enable it now. Click Submit.
14. Copy the ARN from the top right to be used later in the Alexa Skill Setup.


###AWS DynamoDB Setup
1. Go to the AWS Console and click on DynamoDB link, under “Database”. Note: ensure you are in us-east (same as your Lambda)
2. Click on CreateTable: set “RecipeHelperRecipeData” as the table name, use “CustomerID” for the primary key/ partition key field. Keep the drop down box set to  “string”.
3. Continue the steps with the default settings to finish the setup of DynamoDB table.

###Alexa Skill Setup

1. Go to the Alexa Console and click Add a New Skill.
2. Set "CookingHelper" as the skill name and "recipehelper" as the invocation name, this is what is used to activate your skill. For example you would say: "Alexa, ask recipehelper do I need butter" or "Alexa, ask recipehelper how do I make cookies
3. Select the Lambda ARN for the skill Endpoint and paste the ARN copied from above. Click Next.
4. Copy the Intent Schema from the IntentScheme.txt into the Intent Scheme field
5. Create a custom slot type, named INGREDIENT_LIST and copy the values from the attachment
6. Create a custom slot type, named RECIPE_LIST and copy the values from the attachment
7. Copy the Sample Utterances from the included SampleUtterances.txt. (keep in mind that, at the moment, these are the only ways to access these intents. In the 'enter utterance' field of testing, you must keep the wording exact, but see notes in last step)
8. Go back to the skill Information tab and copy the appId. Towards the top of the SpeechletLambda.java, there is a line supportedApplicationIds.add(...); which currently is hard coded with my application ID. Replace this with the app ID you were given, then update the lambda source zip file with this change and upload to lambda again, this step makes sure the lambda function only serves request from authorized source.
9. You are now able to start testing your sample skill! In the Service Simulator area, you can type in utterances. Do not copy the utterances exactly. There cannot be any punctuation or real numbers. Anything in curly brackets should be replaced with a string.

##Some examples: 
        -what are the ingredients
	-what is the next step
        -do I need butter
        -what is step three
        -what are the steps to make banana bread
        
##Problems, Loose ends:

The main problem in this skill arises from the specific ingredient intent. Every ingredient that could be asked about is assumed to be listed in the INGREDIENT_LIST.txt (Ingredient slot type). This obviously is impossible. In my list, there are some mistakes, since I could have spent an entire summer processing it to be simple, efficient, and full of a wider variety of ingredients. For example, if Butter Bits is listed instead of Butter and you ask about the quantity of butter needed for cookies, it will return that you do not need Butter Bits. This is because it searches for 'butter' in the list, finds butter bits, and then compares this to the recipe, which just lists butter. I played around with this a lot, but when I started relying too much on fuzzy search and similar methods, it would never return that you didn't need an ingredient, instead finding something with the closest proximity (when asking about needing pig, it would return the quantity of chocolate you needed for some recipes). I've gotten it working relativley well, but I know that in some recipes, this will be an issue. It works well for simple things such as pancakes, cookies, etc but for more advanced recipes with more advanced ingredients, there could be bugs.

My code could probably be more efficient, as I'm just a beginner and often my solution clearly could be optimized. However, I'm still pretty happy with how this summer project has turned out and I hope that this skill could help someone in the future create an even better cooking skill :) 
        

