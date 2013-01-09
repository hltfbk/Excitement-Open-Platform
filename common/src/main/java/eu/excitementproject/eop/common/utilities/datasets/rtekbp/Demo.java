package eu.excitementproject.eop.common.utilities.datasets.rtekbp;

//import java.io.File;
//import java.util.List;
//
//import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitter;
//import ac.biu.nlp.nlp.instruments.sentencesplit.SentenceSplitterException;
//import ac.biu.nlp.nlp.instruments.sentencesplit.nagel.NagelSentenceSplitter;

public class Demo
{
//	public Demo(String[] args) throws Exception
//	{
//		if (args.length<1) throw new Exception("args");
//		this.file = new File(args[0]);
//	}
//	
//	public void f() throws RteKbpIOException, SentenceSplitterException
//	{
//		DocumentReader reader = new DefaultDocumentReader();
//		reader.setFileName(this.file.getPath());
//		reader.read();
//		DocumentContents contents = reader.getDocumentContents();
//		System.out.println(contents.getSentences());
//		
//		System.out.println("---------------------------");
//		SentenceSplitter splitter = new NagelSentenceSplitter();
//		splitter.setDocument(contents.getSentences());
//		splitter.split();
//		int index=1;
//		for (String sentence : splitter.getSentences())
//		{
//			System.out.println(String.valueOf(index)+": "+sentence);
//			++index;
//		}
//		
//	}
//	
//	public void g() throws RteKbpIOException, SentenceSplitterException
//	{
//		
//		DocumentReader reader = new DefaultDocumentReader();
//		reader.setFileName("/media/Data/asher/data/dev/data/Datasets/RTE/RTE6/KBP/TAC2010_RTE-6_KBP_Validation_Pilot_Test_Data/data/source_data/LTW_ENG_20070309.0062.LDC2009T13.sgm");
//		reader.read();
//		DocumentContents contents = reader.getDocumentContents();
//		String sentences = contents.getSentences();
//		SentenceSplitter splitter = new NagelSentenceSplitter();
//		splitter.setDocument(sentences);
////		splitter.setDocument(" unique U.S. visitors, with MySpace getting 61.5 million, according to comScore Media Metrix..  One just need look at the smash success of Dove's \"Evolution\" commercial, a 75-second spot that proved a viral marketing darling last year. In less than one month, the free vehicle pulled more than 1.7 million views on YouTube, according to Advertising Age. It brought Dove its biggest spike in visitors to its CampaignForRealBeauty.com, and was more than three times more effective than the company's Super Bowl com");
//		splitter.split();
//		List<String> list = splitter.getSentences();
//		int index=1;
//		for (String str : list)
//		{
//			System.out.println(""+index+": "+str);
//			++index;
//		}
//		
//		
//		
//	}
//
//	public static void main(String[] args)
//	{
//		try
//		{
//			Demo demo = new Demo(args);
//			demo.f();
//			//demo.g();
//			
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace(System.out);
//		}
//	}
//
//	private File file;
}
