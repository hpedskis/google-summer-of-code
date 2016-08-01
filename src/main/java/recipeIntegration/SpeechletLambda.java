package recipeIntegration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.json.SpeechletResponseEnvelope;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class SpeechletLambda {

	private static final int MAX_CARD_SIZE = 8000;
	private static final int MAX_SPEECH_SIZE = 8000;
	private static final int MAX_RESPONSE_SIZE = 24576;
	private Speechlet mSpeechlet;
	private static final Map<Thread, LambdaLogger> mLoggers = new HashMap<Thread, LambdaLogger>();
	private static final StringBuffer mProxyLog = new StringBuffer();

	private static Set<String> supportedApplicationIds;

	public SpeechletLambda() {
		log("SpeechletLambda init");
		supportedApplicationIds = new HashSet<String>();
		supportedApplicationIds
				.add("amzn1.echo-sdk-ams.app.5c367d99-3850-4c8c-8774-0c9ecfd1c80e");

	}

	public void handleRequest(InputStream inputStream,
			OutputStream outputStream, Context context) throws IOException {
		mLoggers.put(Thread.currentThread(), context.getLogger());
		if (mProxyLog.length() > 0) {
			context.getLogger().log(mProxyLog.toString());
			mProxyLog.setLength(0);
		}
		log("Handling request");
		byte serializedSpeechletRequest[] = IOUtils.toByteArray(inputStream);
		SpeechletRequestEnvelope requestEnvelope = SpeechletRequestEnvelope
				.fromJson(serializedSpeechletRequest);
		SpeechletRequest speechletRequest = requestEnvelope.getRequest();
		Session session = requestEnvelope.getSession();
		String requestId = speechletRequest == null ? null : speechletRequest
				.getRequestId();
		@SuppressWarnings("unused")
		String applicationId = null;
		if (session != null && session.getApplication() != null)
			applicationId = session.getApplication().getApplicationId();

		boolean saveSessionAttributes = true;
		SpeechletResponseEnvelope responseEnvelope = new SpeechletResponseEnvelope();
		responseEnvelope.setVersion("1.0");
		try {
			if (session != null && session.isNew()) {
				SessionStartedRequest sessionStartedRequest = SessionStartedRequest
						.builder().withRequestId(requestId).build();
				mSpeechlet.onSessionStarted(sessionStartedRequest, session);
			}
			if (speechletRequest instanceof IntentRequest) {
				SpeechletResponse speechletResponse = mSpeechlet.onIntent(
						(IntentRequest) speechletRequest, session);
				responseEnvelope.setResponse(speechletResponse);
				if (speechletResponse != null)
					saveSessionAttributes = !speechletResponse
							.getShouldEndSession();
			} else if (speechletRequest instanceof LaunchRequest) {
				SpeechletResponse speechletResponse = mSpeechlet.onLaunch(
						(LaunchRequest) speechletRequest, session);
				responseEnvelope.setResponse(speechletResponse);
				if (speechletResponse != null)
					saveSessionAttributes = !speechletResponse
							.getShouldEndSession();
			} else if (speechletRequest instanceof SessionEndedRequest) {
				saveSessionAttributes = false;
				mSpeechlet.onSessionEnded(
						(SessionEndedRequest) speechletRequest, session);
			} else {
				String requestType = speechletRequest == null ? null
						: speechletRequest.getClass().getName();
				log("warn:Unsupported request type "
						+ requestType
						+ ". Consider updating your SDK version. Request envelope version "
						+ requestEnvelope.getVersion() + ", SDK version 1.0");
			}
		} catch (Exception e) {
			log("error:Exception occurred in speechlet ");
			log(e);
		}
		if (session != null && saveSessionAttributes)
			responseEnvelope.setSessionAttributes(session.getAttributes());
		// response.setContentType("application/json");
		// response.setStatus(status);
		checkResponseConstraints(responseEnvelope.getResponse());
		byte jsonBytes[] = responseEnvelope.toJsonBytes();
		int responseSize = jsonBytes.length;
		if (responseSize > MAX_RESPONSE_SIZE)
			log("WARN:Speechlet response with size of "
					+ Integer.valueOf(responseSize)
					+ " bytes exceeds the maximum allowed size of "
					+ MAX_RESPONSE_SIZE
					+ " bytes and will be rejected by the Alexa Cloud Service");
		// response.setContentLength(responseSize);
		outputStream.write(jsonBytes);
		if (outputStream != null)
			outputStream.close();
	}

	public Speechlet getSpeechlet() {
		return mSpeechlet;
	}

	public void setSpeechlet(Speechlet speechlet) {
		if (speechlet != null)
			log("Setting speechlet to " + speechlet.getClass().getName());
		else
			log("Setting speechlet to <null>");
		this.mSpeechlet = speechlet;
	}

	private void checkResponseConstraints(SpeechletResponse response) {
		if (response == null)
			return;
		if (response.getOutputSpeech() instanceof PlainTextOutputSpeech) {
			String text = ((PlainTextOutputSpeech) response.getOutputSpeech())
					.getText();
			checkConstraint("OutputSpeech", StringUtils.length(text),
					MAX_SPEECH_SIZE);
		}
		if (response.getCard() instanceof SimpleCard) {
			SimpleCard card = (SimpleCard) response.getCard();
			int cardSize = StringUtils.length(card.getContent())
					+ StringUtils.length(card.getTitle());
			checkConstraint("Card", cardSize, MAX_CARD_SIZE);
		}
	}

	private static void checkConstraint(String constraint, int actualSize,
			int maxSize) {
		if (actualSize > maxSize)
			log("warn: " + constraint + " with size "
					+ Integer.valueOf(actualSize)
					+ " exceeds the maximum allowed size of "
					+ Integer.valueOf(maxSize)
					+ " and will be rejected by the Alexa Cloud Service");
	}

	public static void log(String msg) {
		LambdaLogger logger = mLoggers.get(Thread.currentThread());
		if (logger != null)
			logger.log(msg + "\n");
		else
			mProxyLog.append(msg + "\n");
	}

	public static void log(Throwable t) {
		for (Throwable e = t; e != null; e = e.getCause()) {
			log(e.toString());
			for (StackTraceElement ele : e.getStackTrace())
				log("  " + ele.toString());
		}
	}

}
