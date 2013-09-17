package eu.excitementproject.eop.lap.biu.en.pasta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructureBySerialComparator;
import eu.excitementproject.eop.common.utilities.ExceptionUtil;
import eu.excitementproject.eop.lap.biu.en.parser.BasicParser;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;
import eu.excitementproject.eop.lap.biu.en.parser.easyfirst.EasyFirstParser;
import eu.excitementproject.eop.lap.biu.en.pasta.nomlex.NomlexException;
import eu.excitementproject.eop.lap.biu.en.pasta.nomlex.NomlexMapBuilder;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentIdentificationException;
import eu.excitementproject.eop.lap.biu.pasta.identification.PredicateArgumentStructureBuilder;

/**
 * 
 * @author Asher Stern
 *
 */
public class DemoParseAndDisplay
{
	/**
	 * @param args (1) pos-tagger file-name, (2) easy-first host, (3) easy-first port
	 * (4) nomlex-plus file, (5) class-role-table file
	 */
	public static void main(String[] args)
	{
		try
		{
			if (args.length<5) throw new RuntimeException("args");
			DemoParseAndDisplay app = new DemoParseAndDisplay(args[0], args[1],Integer.parseInt(args[2]),args[3],args[4]);
			app.go();
		}
		catch(Exception e)
		{
			ExceptionUtil.outputException(e, System.out);
		}


	}

	
	public DemoParseAndDisplay(String posTaggerFileName, String host, int port,
			String nomlexFileName, String nomlexClassRoleTableFileName)
	{
		super();
		this.posTaggerFileName = posTaggerFileName;
		this.host = host;
		this.port = port;
		this.nomlexFileName = nomlexFileName;
		this.nomlexClassRoleTableFileName = nomlexClassRoleTableFileName;
	}

	
	public void go() throws ParserRunException, IOException, TreeAndParentMapException, PredicateArgumentIdentificationException, NomlexException, TreeStringGeneratorException
	{
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		NomlexMapBuilder nomlexBuilder = new NomlexMapBuilder(nomlexFileName, nomlexClassRoleTableFileName);
		nomlexBuilder.build();
		
		System.out.println();
		System.out.println("Type \"exit\" to exit.");
		System.out.println("Ready.");
		
		BasicParser parser = new EasyFirstParser(host,port,posTaggerFileName);
		parser.init();
		try
		{
			PredicateArgumentStructureBuilderFactory<Info, BasicNode> builderFactory =
					new PredicateArgumentStructureBuilderFactory<Info, BasicNode>(nomlexBuilder.getNomlexMap()
							//,PastaMode.EXPANDED
							); 
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Please enter a sentence:");
			String line = reader.readLine();
			while (!"exit".equals(line))
			{
				if (line.length()>0)
				{
					parser.setSentence(line);
					parser.parse();
					BasicNode tree = parser.getParseTree();
					System.out.println("== parsed ==");
					TreeAndParentMap<Info, BasicNode> tapm = new TreeAndParentMap<Info, BasicNode>(tree);
					
//					TreeStringGenerator<Info> tsg = new TreeStringGenerator<Info>(new SimpleNodeString(),tapm.getTree());
//					System.out.println(tsg.generateString());

//					EasyFirstPredicateArgumentStructureBuilder<Info, BasicNode> builder =
//							new EasyFirstPredicateArgumentStructureBuilder<Info, BasicNode>(tapm,nomlexBuilder.getNomlexMap());

					PredicateArgumentStructureBuilder<Info, BasicNode> builder = builderFactory.createBuilder(tapm);
					
					builder.build();

					Set<PredicateArgumentStructure<Info, BasicNode>> pass = builder.getPredicateArgumentStructures();
					List<PredicateArgumentStructure<Info, BasicNode>> pasList = new ArrayList<PredicateArgumentStructure<Info,BasicNode>>(pass.size());
					pasList.addAll(pass);
					Collections.sort(pasList, new PredicateArgumentStructureBySerialComparator<Info, BasicNode>());
					for (PredicateArgumentStructure<Info, BasicNode> pas : pasList)
					{
						System.out.print(pas.toString());
					}
				}
				else
				{
					System.out.println("Empty line!");
				}

				System.out.println("Please enter a sentence:");
				line = reader.readLine();
			}
			
		}
		finally
		{
			parser.cleanUp();
		}
		
	}

	
	private String posTaggerFileName;
	private String host;
	private int port;
	private String nomlexFileName;
	private String nomlexClassRoleTableFileName;

	
}
