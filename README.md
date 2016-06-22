# google-summer-of-code
My summer project for Google Summer of Code

Currently, this code will run on an Alexa any of the recipes listed in the MASTER_TITLE file.
There are a few bugs and it isn’t the most user friendly, but I’m working on those changes now.

To run this, you must have Apache Maven and an AWS account (the AWS account is free and easy to set up). These steps are taken from Amazon's java tutorials on setting up their sample skills, but I have adapted them to work for my skill. 

**YOU DO NOT NEED AN AMAZON ECHO TO TEST THIS. You can see the output through the AWS Console. **

AWS Lambda Setup

1. Go to the AWS Console and click on the Lambda link. Note: ensure you are in us-east or you wont be able to use Alexa with Lambda.
2. Click on the Create a Lambda Function or Get Started Now button.
3. Skip the blueprint
4. Name the Lambda Function "Recipe-Skill-Example". (although this name doesn't matter)
5. Select the runtime as Java 8
6. Go to the the google-summer-of-code/ directory containing pom.xml, and run 'mvn assembly:assembly -DdescriptorId=jar-with-dependencies package'. This will generate a zip file named "Recipe-Skill-Example-1.0-jar-with-dependencies.jar" in the target directory.
7. Select Code entry type as "Upload a .ZIP file" and then upload the "Recipe-Skill-Example-1.0-jar-with-dependencies.jar" file from the target directory to Lambda
8. Set the Handler as recipeIntegration.RecipeLambda::handleRequest (it is very important that this name is copied EXACTLY. Otherwise Lambda won't be able to upload your code).
9. Create a basic execution role and click create.
10. Leave the Advanced settings as the defaults.
11. Click "Next" and review the settings then click "Create Function"
12. Click the "Event Sources" tab and select "Add event source"
13. Set the Event Source type as Alexa Skills kit and Enable it now. Click Submit.
14. Copy the ARN from the top right to be used later in the Alexa Skill Setup.


Alexa Skill Setup

1. Go to the Alexa Console and click Add a New Skill.
2. Set "CookingHelper" as the skill name and "recipehelper" as the invocation name, this is what is used to activate your skill. For example you would say: "Alexa, ask recipehelper do I need oyster sauce for chinese style baby bok choy with mushroom sauce” or "Alexa, ask recipehelper how do I make cookies”
3. Select the Lambda ARN for the skill Endpoint and paste the ARN copied from above. Click Next.
4. Copy the Intent Schema from the IntentScheme.txt into the Intent Scheme field
5. Create a custom slot type, named INGREDIENT_LIST and copy the values from the attachment
6. Create a custom slot type, named RECIPE_LIST and copy the values from the attachment
7. Copy the Sample Utterances from the included SampleUtterances.txt. (keep in mind that, at the moment, these are the only ways to access these intents. In the 'enter utterance' field of testing, you must keep the wording exact, but see notes in last step)
8. Go back to the skill Information tab and copy the appId. Towards the top of the SpeechletLambda.java, there is a line supportedApplicationIds.add(...); which currently is hard coded with my application ID. Replace this with the app ID you were given, then update the lambda source zip file with this change and upload to lambda again, this step makes sure the lambda function only serves request from authorized source.
9. You are now able to start testing your sample skill! In the Service Simulator area, you can type in utterances. Do not copy the utterances exactly. Each must start with recipehelper and there cannot be any punctuation or real numbers. Anything in curly brackets should be replaced with a string.

Some examples: 
        recipehelper what are the ingredients for Chicken Wraps
        recipehelper do I need butter for Bacon Wrapped Bratwurst 
        recipehelper what is step three for pancakes
        recipehelper what are the steps to make pecan pie
        

