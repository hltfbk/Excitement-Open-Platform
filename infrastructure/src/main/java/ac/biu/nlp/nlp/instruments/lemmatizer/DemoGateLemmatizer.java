package ac.biu.nlp.nlp.instruments.lemmatizer;

import java.io.File;

import ac.biu.nlp.nlp.general.ExceptionUtil;
import ac.biu.nlp.nlp.representation.CanonicalPosTag;
import ac.biu.nlp.nlp.representation.PartOfSpeech;
import ac.biu.nlp.nlp.representation.UnsupportedPosTagStringException;


/**
 * Demonstrates the {@link GateLemmatizer}
 * @author Asher Stern
 * @since Jan 18, 2011
 *
 */
public class DemoGateLemmatizer
{
	public static void main(String[] args)
	{
		try
		{
			File gateRulesFile = new File(args[0]);
			Lemmatizer lemmatizer = new GateLemmatizer(gateRulesFile.toURI().toURL());
			lemmatizer.init();
			try
			{
				lemmatizer.set("going");
				lemmatizer.process();
				String lemma = lemmatizer.getLemma();
				System.out.println(lemma);
				System.out.println("--------------------");
				PartOfSpeech pos = new FakePartOfSpeech("n");
				System.out.println(pos.getCanonicalPosTag().name());
				lemmatizer.set("children",pos);
				lemmatizer.process();
				lemma = lemmatizer.getLemma();
				System.out.println(lemma);
				
				
			}
			finally
			{
				lemmatizer.cleanUp();
			}
			
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}
	}
	
	@SuppressWarnings("serial")
	private static class FakePartOfSpeech extends PartOfSpeech
	{
		public FakePartOfSpeech(String posTagString)
				throws UnsupportedPosTagStringException
		{
			super(posTagString);
		}

		@Override
		protected void setCanonicalPosTag()
		{
			if (this.posTagString.equals("v"))
				canonicalPosTag = CanonicalPosTag.VERB;
			else if (this.posTagString.equals("n"))
				canonicalPosTag = CanonicalPosTag.NOUN;
			else
				canonicalPosTag = CanonicalPosTag.OTHER;
		}

		@Override
		protected void validatePosTagString(String posTagString)
				throws UnsupportedPosTagStringException
		{
			// do nothing
		}

		@Override
		public PartOfSpeech createNewPartOfSpeech(String posTagString) throws UnsupportedPosTagStringException
		{
			return new FakePartOfSpeech(posTagString);
		}
	}

}
