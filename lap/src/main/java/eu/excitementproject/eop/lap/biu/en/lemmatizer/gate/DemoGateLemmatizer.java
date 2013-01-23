package eu.excitementproject.eop.lap.biu.en.lemmatizer.gate;
import static eu.excitementproject.eop.common.representation.partofspeech.SimplerPosTagConvertor.simplerPos;

import java.io.File;

import eu.excitementproject.eop.common.representation.partofspeech.CanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.lap.biu.lemmatizer.Lemmatizer;



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
				System.out.println(simplerPos(pos.getCanonicalPosTag()).name());
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
				canonicalPosTag = CanonicalPosTag.V;
			else if (this.posTagString.equals("n"))
				canonicalPosTag = CanonicalPosTag.N;
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
