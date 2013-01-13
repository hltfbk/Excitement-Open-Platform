package ac.biu.nlp.nlp.engineml.small_unit_tests.old_small_tests;
import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.utilities.TeEngineMlException;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.FindMainVerbHeuristic;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.TreeUtilities;
import ac.biu.nlp.nlp.engineml.utilities.preprocess.ParserFactory;
import eu.excitementproject.eop.common.representation.parse.BasicParser;
import eu.excitementproject.eop.common.representation.parse.ParserRunException;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.DefaultRTEMainReader;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.RTEMainReader;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.RTEMainReaderException;
import eu.excitementproject.eop.common.utilities.datasets.rtepairs.TextHypothesisPair;

public class DemoFindMainVerbHeuristics
{
	public static void f(String[] args) throws TeEngineMlException, ParserRunException, RTEMainReaderException
	{
		@SuppressWarnings("deprecation")
		BasicParser parser = ParserFactory.getParser("localhost");
		parser.init();
		try
		{
			RTEMainReader reader = new DefaultRTEMainReader();
			reader.setXmlFile(new File("/home/asher/main/temp/prototype1/RTE5_MainTask_DevSet.xml"));
			reader.read();
			Map<Integer,TextHypothesisPair> dataset = reader.getMapIdToPair();
			Set<TextHypothesisPair> moreThanOneMainVerb = new HashSet<TextHypothesisPair>();
			

			for (Map.Entry<Integer,TextHypothesisPair> pair : dataset.entrySet())
			{
				System.out.println(pair.getKey());
				String hypothesis = pair.getValue().getHypothesis();
				System.out.println(hypothesis);
				parser.setSentence(hypothesis);
				parser.parse();
				BasicNode originalTree =  parser.getParseTree();
				ExtendedNode tree = TreeUtilities.copyFromBasicNode(originalTree);
				FindMainVerbHeuristic fmvh = new FindMainVerbHeuristic();
				Set<ExtendedNode> tcv = fmvh.topContentVerbs(tree);
				for (ExtendedNode tcVerb : tcv)
				{
					String lemma = InfoGetFields.getLemma(tcVerb.getInfo());
					System.out.println(lemma);
				}
				System.out.println(StringUtil.generateStringOfCharacter('-', 50));
				
				if (tcv.size()>1)
					moreThanOneMainVerb.add(pair.getValue());
			}
			
			System.out.println(StringUtil.generateStringOfCharacter('-', 50));
			System.out.println("Specials:");
			for (TextHypothesisPair pair : moreThanOneMainVerb)
			{
				System.out.println(pair.getId());
				System.out.println(pair.getHypothesis());
				System.out.println(StringUtil.generateStringOfCharacter('-', 50));
			}
			
			 
			
			
		}
		finally
		{
			parser.cleanUp();
		}
		
		
	}
	
	public static void main(String[] args)
	{
		try
		{
			f(args);
			
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}

	}


}
