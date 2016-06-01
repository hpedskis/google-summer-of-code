# google-summer-of-code
My summer project for Google Summer of Code

Currently, this code will run on an Alexa for the single recipe (Chocolate Chip Cookies)

To run this, you must have Apache Maven and an AWS account. These steps are taken from Amazon's java tutorials on setting up their sample skills,
but have been adapted to work for my skill. YOU DO NOT NEED AN AMAZON ECHO TO TEST THIS. You can see the output through the AWS Console.

AWS Lambda Setup

1. Go to the AWS Console and click on the Lambda link. Note: ensure you are in us-east or you wont be able to use Alexa with Lambda.
2. Click on the Create a Lambda Function or Get Started Now button.
3. Skip the blueprint
4. Name the Lambda Function "Recipe-Skill-Example". (although this name doesn't matter)
5. Select the runtime as Java 8
6. Go to the the samples/ directory containing pom.xml, and run 'mvn assembly:assembly -DdescriptorId=jar-with-dependencies package'. This will generate a zip file named "alexa-skills-kit-samples-1.0-jar-with-dependencies.jar" in the target directory.
Select Code entry type as "Upload a .ZIP file" and then upload the "alexa-skills-kit-samples-1.0-jar-with-dependencies.jar" file from the build directory to Lambda
Set the Handler as recipeIntegration.RecipeLambda::handleRequest (it is very important that this name is copied EXACTLY. Otherwise Lambda won't be able to upload your code).
7. Create a basic execution role and click create.
8. Leave the Advanced settings as the defaults.
9. Click "Next" and review the settings then click "Create Function"
10. Click the "Event Sources" tab and select "Add event source"
11. Set the Event Source type as Alexa Skills kit and Enable it now. Click Submit.
12. Copy the ARN from the top right to be used later in the Alexa Skill Setup.


Alexa Skill Setup

1. Go to the Alexa Console and click Add a New Skill.
2. Set "CookingHelper" as the skill name and "recipehelper" as the invocation name, this is what is used to activate your skill. For example you would say: "Alexa, ask recipehelper do I need butter"
3. Select the Lambda ARN for the skill Endpoint and paste the ARN copied from above. Click Next.
4. Copy the Intent Schema from the IntentScheme .txt
5. Copy the Sample Utterances from the included SampleUtterances.txt. Click Next.
6. Go back to the skill Information tab and copy the appId. Towards the top of the SpeechletLambda.java, there is a line supportedApplicationIds.add(...); which currently is hard coded with my application ID. Replace this with the app ID you were given, then update the lambda source zip file with this change and upload to lambda again, this step makes sure the lambda function only serves request from authorized source.
7. You are now able to start testing your sample skill! You should be able to go to the Echo webpage and see your skill enabled.
In order to test it, try to say some of the Sample Utterances from the Examples section below.
Your skill is now saved and once you are finished testing you can continue to publish your skill.

