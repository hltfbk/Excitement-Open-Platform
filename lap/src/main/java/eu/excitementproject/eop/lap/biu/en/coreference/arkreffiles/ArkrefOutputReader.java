package eu.excitementproject.eop.lap.biu.en.coreference.arkreffiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;

/**
 * Reads the contents of ArkRef output file (the ".tagged" file), and generates
 * Java objects which represent it.
 * The Java object representing ArkRef output is an ArrayList of {@link ArkrefOutputWord}.
 * 
 * @author Asher Stern
 * @since Dec 10, 2013
 *
 */
public class ArkrefOutputReader<I extends Info, S extends AbstractNode<I, S>>
{
	public static final Pattern MARKER_BEGIN_PATTERN = Pattern.compile("<mention mentionid=\"([0-9]*)\" entityid=\"([0-9_]*)\">");
	public static final Pattern MARKER_END_PATTERN = Pattern.compile("</mention>");

	
	public ArkrefOutputReader(String arkrefOutputString)
	{
		super();
		this.arkrefOutputString = arkrefOutputString;
	}
	
	
	public void read() throws CoreferenceResolutionException
	{
		arkrefOutput = new ArrayList<>();
		String remainingString = arkrefOutputString;
		Stack<ArkrefMention> entitiesStack = new Stack<>();
		List<ArkrefMarker> beginMarkers = new LinkedList<>();
		List<ArkrefMarker> endMarkers = new LinkedList<>();
		boolean stop = false;
		while(!stop)
		{
			Matcher matcherBegin = MARKER_BEGIN_PATTERN.matcher(remainingString);
			Matcher matcherEnd = MARKER_END_PATTERN.matcher(remainingString);
			boolean thereIsBegin = matcherBegin.find();
			boolean thereIsEnd = matcherEnd.find();
			if (thereIsBegin||thereIsEnd)
			{
				boolean beginTaken = true;
				int nearestIndex = 0;
				String matchedString = null;
				if (!thereIsEnd) {beginTaken=true;}
				else if (!thereIsBegin) {beginTaken=false;}
				else
				{
					if (matcherBegin.start() == matcherEnd.start()) {throw new CoreferenceResolutionException("Bug");}
					beginTaken = (matcherBegin.start() < matcherEnd.start());
				}
				if (beginTaken)
				{
					nearestIndex = matcherBegin.start();
					matchedString = matcherBegin.group();
				}
				else
				{
					nearestIndex = matcherEnd.start();
					matchedString = matcherEnd.group();
				}
				String simpleWords = remainingString.substring(0, nearestIndex);
				simpleWords = simpleWords.trim();
				if (simpleWords.length()>0)
				{
					addSimpleWords(simpleWords,beginMarkers,endMarkers);
					beginMarkers = new LinkedList<>();
					endMarkers = new LinkedList<>();
				}
				if (beginTaken)
				{
					String mentionId = matcherBegin.group(1);
					String entityId = matcherBegin.group(2);
					beginMarkers.add(new ArkrefMarker(entityId,mentionId,true));
					entitiesStack.push(new ArkrefMention(mentionId, entityId));
				}
				else
				{
					ArkrefMention lastBegin = null;
					try {lastBegin = entitiesStack.pop();}
					catch(EmptyStackException e){throw new CoreferenceResolutionException("Bug or malformed Arkref output. end-tag has been encountered, but no begin-tag has been encountered earlier.",e);}
					String mentionId = lastBegin.getMentionId();
					String entityId = lastBegin.getEntityId();
					endMarkers.add(new ArkrefMarker(entityId, mentionId, false));
				}
				remainingString = remainingString.substring(nearestIndex+matchedString.length());
			}
			else
			{
				addSimpleWords(remainingString.trim(),beginMarkers,endMarkers);
				stop = true;
			}
			
		}
	}
	
	public ArrayList<ArkrefOutputWord<I, S>> getArkrefOutput() throws CoreferenceResolutionException
	{
		if (null==arkrefOutput) {throw new CoreferenceResolutionException("Method read was not called.");}
		return arkrefOutput;
	}


	private void addSimpleWords(String text,List<ArkrefMarker> beginMarkers, List<ArkrefMarker> endMarkers) throws CoreferenceResolutionException
	{
		if (endMarkers.size()>0)
		{
			if (arkrefOutput.size()==0) {throw new CoreferenceResolutionException("Bug or malformed arkref output");}
			ArkrefOutputWord<I, S> lastWord = arkrefOutput.get(arkrefOutput.size()-1);
			arkrefOutput.remove(arkrefOutput.size()-1);
			List<ArkrefMarker> existingEndMarkers = lastWord.getEndMarkers();
			List<ArkrefMarker> newEndMarkers = new LinkedList<>();
			if (existingEndMarkers!=null) {newEndMarkers.addAll(existingEndMarkers);}
			newEndMarkers.addAll(endMarkers);
			arkrefOutput.add(new ArkrefOutputWord<I, S>(lastWord.getWord(),lastWord.getBeginMarkers(),newEndMarkers));
		}

		text = text.trim();
		String[] textWords = text.split("\\s+");
		ArkrefOutputWord<I, S> firstWord = new ArkrefOutputWord<I, S>(textWords[0],beginMarkers,Collections.<ArkrefMarker>emptyList());
		arkrefOutput.add(firstWord);
		if (textWords.length>1)
		{
			for (int index=1;index<textWords.length;++index)
			{
				arkrefOutput.add(new ArkrefOutputWord<I, S>(textWords[index],Collections.<ArkrefMarker>emptyList(),Collections.<ArkrefMarker>emptyList()));
			}
		}
	}
	
	// input
	private final String arkrefOutputString;
	
	
	// output
	private ArrayList<ArkrefOutputWord<I, S>> arkrefOutput = null;
}
