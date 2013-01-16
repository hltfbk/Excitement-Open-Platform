package eu.excitementproject.eop.lap.biu.en.sentencesplit.nagel;

public class Demo
{
	public static void main(String[] args)
	{
		try
		{
			NagelSentenceSplitter nss = new NagelSentenceSplitter();
			nss.setDocument("I am nice. I am very nice.\n\n This is what you can see here. Bye!!!");
			nss.split();
			for (String sentence : nss.getSentences())
			{
				System.out.println(sentence);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
}
