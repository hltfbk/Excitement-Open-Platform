package eu.excitementproject.eop.lap.biu.en.pasta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.view.TreeStringGenerator.TreeStringGeneratorException;
import eu.excitementproject.eop.common.representation.pasta.Argument;
import eu.excitementproject.eop.common.representation.pasta.ClausalArgument;
import eu.excitementproject.eop.common.representation.pasta.PredicateArgumentStructure;
import eu.excitementproject.eop.common.representation.pasta.TypedArgument;
import eu.excitementproject.eop.common.utilities.StringUtil;
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
			e.printStackTrace(System.out);
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
		System.out.println("Ready.");
		
		BasicParser parser = new EasyFirstParser(host,port,posTaggerFileName);
		parser.init();
		try
		{
			PredicateArgumentStructureBuilderFactory<Info, BasicNode> builderFactory =
					new PredicateArgumentStructureBuilderFactory<Info, BasicNode>(nomlexBuilder.getNomlexMap()); 
			
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
						showPas(pas);
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
	
	private void showPas(PredicateArgumentStructure<Info, BasicNode> pas)
	{
		String predicateHead = InfoGetFields.getLemma(pas.getPredicate().getHead().getInfo());
		System.out.print(predicateHead);
		if (pas.getPredicate().getVerbsForNominal()!=null)
		{
			System.out.print(" ("+StringUtil.joinIterableToString(pas.getPredicate().getVerbsForNominal())+")");
		}
		System.out.println();
		
		for (TypedArgument<Info, BasicNode> typedArgument : pas.getArguments())
		{
			Argument<Info, BasicNode> argument = typedArgument.getArgument();
			BasicNode semanticHead = argument.getSemanticHead();
			String lemma = InfoGetFields.getLemma(semanticHead.getInfo());
			
			System.out.println("\t"+typedArgument.getArgumentType().name()+" "+lemma+" ("+strSetNodes(argument.getNodes())+")");
		}
		
		for (ClausalArgument<Info, BasicNode> clausalArgument : pas.getClausalArguments())
		{
			String lemma = InfoGetFields.getLemma(clausalArgument.getClause().getInfo());
			System.out.println("\t(c) "+clausalArgument.getArgumentType().name()+" "+lemma);
		}
		
	}
	
	private String strSetNodes(Collection<BasicNode> nodes)
	{
		List<BasicNode> listNodes = getSortedBySerial(nodes);
		StringBuilder sb = new StringBuilder();
		boolean firstIteration = true;
		for (BasicNode node : listNodes)
		{
			if (firstIteration){firstIteration=false;}else{sb.append(" ");}
			
			sb.append(
					InfoGetFields.getWord(node.getInfo())
					);
		}
		return sb.toString();
	}

	
	
	// copied from ac.biu.nlp.nlp.predarg.utilities.PredArgsUtilities

	/**
	 * Returns the given parse-tree-nodes, sorted by the their "serial" field.
	 * @param nodes a collection of parse-tree-nodes.
	 * @return the given parse-tree-nodes, sorted by the their "serial" field.
	 */
	private static <I extends Info, S extends AbstractNode<I, S>> List<S> getSortedBySerial(Collection<S> nodes)
	{
		ArrayList<S> list = new ArrayList<S>(nodes.size());
		list.addAll(nodes);
		Collections.sort(list,new BySerialComparator<I, S>());
		return list;
	}

	
	
	// copied from ac.biu.nlp.nlp.predarg.utilities
	
	/**
	 * 
	 * @author Asher Stern
	 * @since Oct 9, 2012
	 *
	 */
	private static class BySerialComparator<I extends Info, S extends AbstractNode<I, S>> implements Comparator<S>
	{
		@Override
		public int compare(S o1, S o2)
		{
			int serial1 = -1;
			int serial2 = -1;
			if (o1!=null){if(o1.getInfo()!=null){if (o1.getInfo().getNodeInfo()!=null)
			{
				serial1 = o1.getInfo().getNodeInfo().getSerial();
			}}}
			if (o2!=null){if(o2.getInfo()!=null){if (o2.getInfo().getNodeInfo()!=null)
			{
				serial2 = o2.getInfo().getNodeInfo().getSerial();
			}}}
			
			if (serial1<serial2) return -1;
			else if (serial1==serial2) return 0;
			else return 1;
		}
		

	}

	

	// copied from ac.biu.nlp.nlp.predarg.utilities
	
	/**
	 * 
	 * @author Asher Stern
	 * @since Oct 15, 2012
	 *
	 * @param <I>
	 * @param <S>
	 */
	private static class PredicateArgumentStructureBySerialComparator<I extends Info, S extends AbstractNode<I, S>> implements Comparator<PredicateArgumentStructure<I, S>>
	{

		@Override
		public int compare(PredicateArgumentStructure<I, S> o1,
				PredicateArgumentStructure<I, S> o2)
		{
			int serial1 = getSerial(o1);
			int serial2 = getSerial(o2);
			if (serial1<serial2)
				return -1;
			else if (serial1 == serial2)
				return 0;
			else
				return 1;
			
		}
		
		private static <I extends Info, S extends AbstractNode<I, S>> int getSerial(PredicateArgumentStructure<I, S> pas)
		{
			return InfoGetFields.getSerial(pas.getPredicate().getHead().getInfo());
		}

	}

	
	
	
	private String posTaggerFileName;
	private String host;
	private int port;
	private String nomlexFileName;
	private String nomlexClassRoleTableFileName;

	
}
