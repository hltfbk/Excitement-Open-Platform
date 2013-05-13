package eu.excitementproject.eop.biutee.small_unit_tests;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeCopier;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.utilities.StringUtil;
import eu.excitementproject.eop.lap.biu.en.parser.BasicParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.parser.easyfirst.EasyFirstParser;
import eu.excitementproject.eop.transformations.alignment.AlignmentCriteria;
import eu.excitementproject.eop.transformations.alignment.DefaultAlignmentCriteria;
import eu.excitementproject.eop.transformations.operations.OperationException;
import eu.excitementproject.eop.transformations.operations.finders.MoveNodeOperationFinder;
import eu.excitementproject.eop.transformations.operations.specifications.MoveNodeSpecification;
import eu.excitementproject.eop.transformations.representation.AdditionalInformationServices;
import eu.excitementproject.eop.transformations.representation.ExtendedInfo;
import eu.excitementproject.eop.transformations.representation.ExtendedNode;
import eu.excitementproject.eop.transformations.representation.ExtendedNodeConstructor;
import eu.excitementproject.eop.transformations.utilities.parsetreeutils.TreeUtilities;


/**
 * 
 * @author Asher Stern
 * @since Jun 7, 2012
 *
 */
public class DemoMoveFinder
{
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		try
		{
			DemoMoveFinder app = new DemoMoveFinder(args[0],args[1],args[2]);
			app.go();
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}

	

	


	public DemoMoveFinder(String server, String port,
			String posTaggerModelFile)
	{
		super();
		this.server = server;
		this.port = port;
		this.posTaggerModelFile = posTaggerModelFile;
	}
	
	
	
	
	
	
	
	
	
	public void go() throws ParserRunException, IOException, TreeAndParentMapException, OperationException
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
				
				MoveNodeOperationFinder finder = new MoveNodeOperationFinder(emtree1, emtree2, alignmentCriteria);
				finder.find();
				Set<MoveNodeSpecification> specs = finder.getSpecs();
				
				for (MoveNodeSpecification spec : specs)
				{
					System.out.println(spec.toString());
				}

				System.out.println();
				System.out.println(StringUtil.generateStringOfCharacter('-', 50));
				System.out.println();
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
