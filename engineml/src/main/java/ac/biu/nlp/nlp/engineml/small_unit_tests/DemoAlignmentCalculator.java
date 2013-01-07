package ac.biu.nlp.nlp.engineml.small_unit_tests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import ac.biu.nlp.nlp.engineml.alignment.AlignmentCalculator;
import ac.biu.nlp.nlp.engineml.alignment.AlignmentCriteria;
import ac.biu.nlp.nlp.engineml.alignment.DefaultAlignmentCriteria;
import ac.biu.nlp.nlp.engineml.representation.AdditionalInformationServices;
import ac.biu.nlp.nlp.engineml.representation.ExtendedInfo;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNode;
import ac.biu.nlp.nlp.engineml.representation.ExtendedNodeConstructor;
import ac.biu.nlp.nlp.engineml.rteflow.macro.SingleTreeEvaluations;
import ac.biu.nlp.nlp.engineml.utilities.parsetreeutils.TreeUtilities;
import ac.biu.nlp.nlp.instruments.parse.BasicParser;
import ac.biu.nlp.nlp.instruments.parse.ParserRunException;
import ac.biu.nlp.nlp.instruments.parse.easyfirst.EasyFirstParser;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.tree.AbstractNodeUtils;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import ac.biu.nlp.nlp.instruments.parse.tree.TreeCopier;
import ac.biu.nlp.nlp.instruments.parse.tree.dependency.basic.BasicNode;

/**
 * 
 * @author Asher Stern
 * @since Jun 7, 2012
 *
 */
public class DemoAlignmentCalculator
{
	

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		try
		{
			DemoAlignmentCalculator app = new DemoAlignmentCalculator(args[0],args[1],args[2]);
			app.go();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}

	

	


	public DemoAlignmentCalculator(String server, String port,
			String posTaggerModelFile)
	{
		super();
		this.server = server;
		this.port = port;
		this.posTaggerModelFile = posTaggerModelFile;
	}









	public void go() throws ParserRunException, IOException, TreeAndParentMapException
	{
		AlignmentCriteria<ExtendedInfo, ExtendedNode> alignmentCriteria = new DefaultAlignmentCriteria();
		
		BasicParser parser = new  EasyFirstParser(server, Integer.parseInt(port), posTaggerModelFile);
		parser.init();
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			boolean stop = false;
			while (!stop)
			{
				System.out.println("Enter two sentences: The first is text, the second is hypothesis");
				
				String sent1 = reader.readLine();
				if (sent1.equals("exit")) {stop=true; break;}
				String sent2 = reader.readLine();
				if (sent2.equals("exit")) {stop=true; break;}
				
				parser.setSentence(sent1);
				parser.parse();
				BasicNode tree1 = parser.getParseTree();

				parser.setSentence(sent2);
				parser.parse();
				BasicNode tree2 = parser.getParseTree();
				
				ExtendedNode etree1 = convertToExtendedNode(tree1);
				ExtendedNode etree2 = convertToExtendedNode(tree2);
				
				etree1 = TreeUtilities.addArtificialRoot(etree1);
				etree2 = TreeUtilities.addArtificialRoot(etree2);

				TreeAndParentMap<ExtendedInfo, ExtendedNode> emtree1 = new TreeAndParentMap<ExtendedInfo, ExtendedNode>(etree1);
				TreeAndParentMap<ExtendedInfo, ExtendedNode> emtree2 = new TreeAndParentMap<ExtendedInfo, ExtendedNode>(etree2);
		
				Set<String> hypothesisLemmasLowerCase = TreeUtilities.constructSetLemmasLowerCase(emtree2);
				int hypothesisNumberOfNodes = AbstractNodeUtils.treeToSet(tree2).size();
				
				AlignmentCalculator alignmentCalculator = new AlignmentCalculator(alignmentCriteria, emtree1, emtree2);
				
				SingleTreeEvaluations evaluations = alignmentCalculator.getEvaluations(hypothesisLemmasLowerCase, hypothesisNumberOfNodes);
				
				System.out.println("evaluations.getMissingLemmas(): "+evaluations.getMissingLemmas());
				System.out.println("evaluations.getMissingNodes(): "+evaluations.getMissingNodes());
				System.out.println("evaluations.getMissingRelations(): "+evaluations.getMissingRelations());
				

			}
			
			
			
		}
		finally
		{
			parser.cleanUp();
		}
	}

	private ExtendedNode convertToExtendedNode(BasicNode tree)
	{
		TreeCopier<Info, BasicNode, ExtendedInfo, ExtendedNode> treeCopier =
				new TreeCopier<Info, BasicNode, ExtendedInfo, ExtendedNode>(
						tree,
						new TreeCopier.InfoConverter<BasicNode,ExtendedInfo>()
						{
							public ExtendedInfo convert(BasicNode node)
							{
								return new ExtendedInfo(node.getInfo(), AdditionalInformationServices.emptyInformation());
							}
						},
						new ExtendedNodeConstructor()
						);
			
			treeCopier.copy();
			ExtendedNode generatedTree = treeCopier.getGeneratedTree();

			return generatedTree;
	}

	private String server;
	private String port;
	private String posTaggerModelFile;
}
