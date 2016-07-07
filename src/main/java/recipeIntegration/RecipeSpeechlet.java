package recipeIntegration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class RecipeSpeechlet implements Speechlet{
	
	private static final Logger log = LoggerFactory.getLogger(RecipeSpeechlet.class);
    
    private AmazonDynamoDBClient amazonDynamoDBClient;
    
    private RecipeHelperManager recipeHelperManager;

    
    @Override
    //Once session starts, request and session ID are created
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        
        initializeComponents();

    }
    
    private void initializeComponents() {
        if (amazonDynamoDBClient == null) {
            amazonDynamoDBClient = new AmazonDynamoDBClient();
            System.out.println("STEP 1: initializing components. created a new Dynamo client");
           
            recipeHelperManager = new RecipeHelperManager(amazonDynamoDBClient);
            
        }
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
    	
    	
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        
        //initializeComponents(); //TODO REMOVE BEFORE REAL TESTING
        return recipeHelperManager.getLaunchResponse(request, session);
    }

    @Override

    public SpeechletResponse onIntent(IntentRequest request, Session session) throws SpeechletException { 
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session.getSessionId());
        
        initializeComponents(); //TODO needed for real testing!!!!!
        System.out.println("inside onIntent at the very top. step # ??");
        
        Intent intent = request.getIntent();
        
        String intentName = (intent != null) ? intent.getName() : null; 
       
        
        if ("GetIngredientOverview".equals(intentName)) {
        	System.out.println("inside on intent. GETTING INGREDIENT OVERVIEW");
            return recipeHelperManager.getIngredientOverview(session, intent);
        } 
        else if ("ResetRecipe".equals(intentName)){
        	return recipeHelperManager.getNewRecipeIntent(session);
        }
        else if ("GetIngredientInformation".equals(intentName)){
        	System.out.println("inside on intent. GETTING INGREDIENT INFORMATION");
        	return recipeHelperManager.getIngredientInformation(session, intent);

        }
        else if ("GetStepList".equals(intentName)){
        	System.out.println("inside on intent. getting step list intent.");
        	return recipeHelperManager.getStepOverview(session, intent);
        	
        }
        else if ("GetSpecificStep".equals(intentName)){
        	return recipeHelperManager.getSpecificStep(session, intent);
        	
        }
        else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelp();
        } 
        else if ("AMAZON.StopIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye");

            return SpeechletResponse.newTellResponse(outputSpeech);
        } 
        else if ("AMAZON.CancelIntent".equals(intentName)) {
            PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
            outputSpeech.setText("Goodbye");

            return SpeechletResponse.newTellResponse(outputSpeech);
        } 
        else {
            throw new SpeechletException("Invalid Intent");
        }
    }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

    }
    
    private SpeechletResponse getHelp() {
        String speechOutput =
                "You can ask questions about cooking any recipe"
                        + "for example, ask what step one of Spiced Pecans is or  "
                        + "ask about an ingredient for Chicken Honey Nut Stir Fry ";
        String repromptText =
                "You can say things like, how much butter do I need for Quick Peanut Butter Cookies ?"
                        + " or, what's step 2 of Blackened Tuna?";
        return newAskResponse(speechOutput, repromptText);
    }

    private static SpeechletResponse newAskResponse(String stringOutput, String repromptText) {
        PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
        outputSpeech.setText(stringOutput);


        PlainTextOutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech();
        repromptOutputSpeech.setText(repromptText);
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptOutputSpeech);

        return SpeechletResponse.newAskResponse(outputSpeech, reprompt);

    }
 

}
