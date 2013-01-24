package eu.excitementproject.eop.lap.biu.en.coreference.arkref;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.excitementproject.eop.lap.biu.coreference.CoreferenceResolutionException;
import eu.excitementproject.eop.lap.biu.coreference.merge.WordWithCoreferenceTag;
import eu.excitementproject.eop.lap.biu.en.coreference.arkref.ArkrefClient.ArkrefClientException;


/**
 * Parses the output of ArkRef utility, and generates a list of
 * {@link WordWithCoreferenceTag}.
 * 
 * @deprecated No longer in use. Currently {@link ArkrefClient} calls directly
 * to ArkRef methods.
 * 
 * @author Asher Stern
 * @since Apr 23, 2012
 *
 */
@Deprecated
public class ArkrefOutputParser
{
	//////////////////////////////// PUBLIC ///////////////////////////////////
	
	
	// REMOVE THIS
	public static void main(String[] args)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String text = reader.readLine();
			ArkrefOutputParser app = new ArkrefOutputParser(text);
			app.parse();
			List<WordWithCoreferenceTag> output = app.getListOfWordsWithTags();
			for (WordWithCoreferenceTag wwct : output)
			{
				System.out.print(wwct.getWord());
				if (wwct.getCoreferenceTag()!=null)
				{
					System.out.print("/"+wwct.getCoreferenceTag());
				}
				System.out.println();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
	
	//////// CONSTRUCTOR ////////
	
	public ArkrefOutputParser(String outputText)
	{
		super();
		this.outputText = outputText;
	}

	
	//////// PUBLIC METHODS ////////

	public void parse() throws IOException, CoreferenceResolutionException
	{
		try
		{
			// matches either the end of a mention-element, or the beginning of a mention-element.
			Pattern pattern = Pattern.compile("(</mention>)|(<mention mentionid=\"([^<>]*)\" entityid=\"([^<>]*)\">)");
			Vector<SequenceOfWords> output = new Vector<SequenceOfWords>();

			// stack of entity-IDs, since mention elements might be bested.
			Stack<MentionIdAndEntityId> stack = new Stack<MentionIdAndEntityId>();
			Matcher matcher = pattern.matcher(this.outputText);
			int lastEnd = 0;
			
			// for each beginning or end of mention-element
			for (boolean found = matcher.find();found;found = matcher.find())
			{
				int start = matcher.start();
				int end = matcher.end();

				// collect the text between the begnnings and ends of elements
				if (start>lastEnd)
				{
					String sequence = this.outputText.substring(lastEnd, start);
					sequence = sequence.trim();
					if (sequence.length()>0)
					{
						String mentionId = null;
						String entityId = null;
						if (!stack.isEmpty())
						{
							MentionIdAndEntityId top = stack.peek();
							mentionId = top.getMentionId();
							entityId = top.getEntityId();
						}

						output.add(new SequenceOfWords(sequence, mentionId, entityId));
					}
				}
				lastEnd=end;

				// update the stack about what the current entity-id is.
				String captured = matcher.group().trim();
				if (captured.equals("</mention>"))
				{
					stack.pop();
				}
				else
				{
					stack.push(new MentionIdAndEntityId(matcher.group(3).trim(), matcher.group(4).trim()));
				}



			}

			// Collect the rest of the text: the text that is after the last "</mention>" tag.
			if (lastEnd<this.outputText.length())
			{
				String rest = this.outputText.substring(lastEnd, this.outputText.length());
				rest = rest.trim();
				if (rest.length()>0)
				{
					output.add(new SequenceOfWords(rest, null, null));
				}
			}

			// Create a list of WordWithCoreferenceTag
			listOfWordsWithTags = fromVectorSequences(output);
			
			// Remove the tags that appear only once in the sequence.
			listOfWordsWithTags = removeOnlyOnce(listOfWordsWithTags);
		}
		catch(RuntimeException e)
		{
			throw new CoreferenceResolutionException("Failed to parse output: \n"+this.outputText+"\nPlease see nested exception.",e);
		}
	}
	
	/**
	 * Returns the output of the parse of the given output-text.
	 * The output is given as a list of {@link WordWithCoreferenceTag}.
	 * <P>
	 * You should call {@link #parse()} before calling this method.
	 * @return
	 * @throws ArkrefClientException 
	 */
	public List<WordWithCoreferenceTag> getListOfWordsWithTags() throws ArkrefClientException
	{
		if (null==listOfWordsWithTags) throw new ArkrefClientException("You should call parse() before calling this method.");
		return listOfWordsWithTags;
	}

	//////////////////////////////// PRIVATE ///////////////////////////////////


	private static List<WordWithCoreferenceTag> fromVectorSequences(Vector<SequenceOfWords> output)
	{
		List<WordWithCoreferenceTag> ret = new ArrayList<WordWithCoreferenceTag>();
		for (SequenceOfWords seq : output)
		{
			String[] words = seq.getSequence().split("\\s+");
			String tag = seq.getEntityId();
			for (String word : words)
			{
				ret.add(new WordWithCoreferenceTag(word, tag));
			}
		}
		return ret;
	}
	
	
	private static Set<String> tagsOnlyOneSequence(List<WordWithCoreferenceTag> list)
	{
		Set<String> allTags = new LinkedHashSet<String>();
		Set<String> tagsTwice = new LinkedHashSet<String>();
		String lastTag = null;
		for (WordWithCoreferenceTag wwct : list)
		{
			String currentTag = wwct.getCoreferenceTag();
			boolean equalsToLast = true;
			if (null==currentTag)
			{
				if (lastTag!=null){equalsToLast=false;}
				else{equalsToLast=true;}
			}
			else {equalsToLast = currentTag.equals(lastTag);}
			
			if (!equalsToLast)
			{
				if (allTags.contains(currentTag))
				{
					tagsTwice.add(currentTag);
				}
				else
				{
					allTags.add(currentTag);
				}
			}
			lastTag = currentTag;
		}
		Set<String> ret = new LinkedHashSet<String>();
		for (String tag : allTags)
		{
			if (!tagsTwice.contains(tag))
			{
				ret.add(tag);
			}
		}
		
		return ret;
	}
	
	
	private static List<WordWithCoreferenceTag> removeOnlyOnce(List<WordWithCoreferenceTag> list)
	{
		Set<String> tagsOnlyOnce = tagsOnlyOneSequence(list);
		List<WordWithCoreferenceTag> ret = new ArrayList<WordWithCoreferenceTag>(list.size());
		for (WordWithCoreferenceTag wwct : list)
		{
			if (null==wwct.getCoreferenceTag())
			{
				ret.add(wwct);
			}
			else if (tagsOnlyOnce.contains(wwct.getCoreferenceTag()))
			{
				ret.add(new WordWithCoreferenceTag(wwct.getWord(), null));
			}
			else
			{
				ret.add(wwct);
			}
		}
		return ret;
	}
	
	
	
	
	private static final class SequenceOfWords
	{
		
		public SequenceOfWords(String sequence, String mentionId,
				String entityId)
		{
			super();
			this.sequence = sequence;
			this.mentionId = mentionId;
			this.entityId = entityId;
		}
		
		
		public String getSequence()
		{
			return sequence;
		}
		@SuppressWarnings("unused")
		public String getMentionId()
		{
			return mentionId;
		}
		public String getEntityId()
		{
			return entityId;
		}


		private final String sequence;
		private final String mentionId;
		private final String entityId;
	}
	
	
	private static class MentionIdAndEntityId
	{
		public MentionIdAndEntityId(String mentionId, String entityId)
		{
			super();
			this.mentionId = mentionId;
			this.entityId = entityId;
		}
		
		
		
		public String getMentionId()
		{
			return mentionId;
		}
		public String getEntityId()
		{
			return entityId;
		}



		private final String mentionId;
		private final String entityId;
	}
	

	private final String outputText;
	private List<WordWithCoreferenceTag> listOfWordsWithTags = null;
}

