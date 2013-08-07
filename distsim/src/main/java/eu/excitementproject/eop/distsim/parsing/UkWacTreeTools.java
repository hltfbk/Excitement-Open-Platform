package eu.excitementproject.eop.distsim.parsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultEdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelation;
import eu.excitementproject.eop.common.representation.parse.tree.TreeAndParentMap.TreeAndParentMapException;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.partofspeech.StuttgartTreeTaggerPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.lap.biu.en.parser.ParserRunException;


public class UkWacTreeTools {	
		
	public  static BasicNode buildTreeFromSentence(String sentence, UkwacIndexSeperators seperator) throws ParserRunException, UnsupportedPosTagStringException, TreeAndParentMapException{
		Map<Integer,BasicNode> mapIdToNode = new HashMap<Integer, BasicNode>();
		Map<Integer,Integer> mapNodeIdToParentId = new HashMap<Integer, Integer>();	
		List<String> parserOutput = splitToNodeInfos(sentence, seperator);//split the sentence to Node info 
		
		if (parserOutput==null)      
			throw new ParserRunException("null output");
		if (parserOutput.size()==0)  
			throw new ParserRunException("empty output");
		
		BasicNode root =	new BasicNode(new DefaultInfo("0",new DefaultNodeInfo("ROOT", "ROOT", 0, null, new DefaultSyntacticInfo(new StuttgartTreeTaggerPartOfSpeech("ROOT"))),new DefaultEdgeInfo(new DependencyRelation("ROOT", null))));
		mapIdToNode.put(0, root);

		for (String line : parserOutput){
			if (line!=null){				
				String[] lineComponents = line.split(seperator.getPOSSeperator());
				if (lineComponents.length < seperator.getLength()) continue;
				try	{
					int index = lineComponents.length-1;
					String relation = lineComponents[index];index--;
					String parent = lineComponents[index];	index--;
					String serial = lineComponents[index];index--;
					String pos = lineComponents[index];	index--;
					String lemma = lineComponents[index];	index--;
					if (lemma.equals("")){
						throw new ParserRunException("an empty lemma was identified");
					} 
					
					// debug
					//System.out.println(lemma + "\t" + pos);
					
					//String word = lineComponents[index];						
					int id = Integer.parseInt(serial);
					BasicNode node = 
						new BasicNode(new DefaultInfo(serial,new DefaultNodeInfo(lemma, lemma, id, null, new DefaultSyntacticInfo(new StuttgartTreeTaggerPartOfSpeech(pos))),new DefaultEdgeInfo(new DependencyRelation(relation, null))));
					mapIdToNode.put(id, node);
					int parentId = Integer.parseInt(parent);					
					mapNodeIdToParentId.put(id, parentId);	
					
				}
				catch(ArrayIndexOutOfBoundsException e){
					throw new ParserRunException("Wrong line returned by the parser: "+line);
				}
				catch (UnsupportedPosTagStringException e){
					throw new ParserRunException("Unsupported part-of-speech tag, occurred in line: \""+line+"\". See nested exception.",e);
				}
			}						
		}		
		for (Map.Entry<Integer, Integer> entry : mapNodeIdToParentId.entrySet()){
			
			int nodeId = entry.getKey();
			int parentId = entry.getValue();
			BasicNode node = mapIdToNode.get(nodeId);
			BasicNode parent = mapIdToNode.get(parentId);
			
			if (parent == null) {
				System.out.println("Null parent: " + parentId);
			}
				
			parent.addChild(node);
		}
		
		return root;		
	}
	
	public static List<String> splitToNodeInfos(String sentence, UkwacIndexSeperators seperator) {
		List<String> info = new ArrayList<String>();
		String[] temp = sentence.split(seperator.getTokenSeperator());//was %%%
		info = Arrays.asList(temp);
		return info;
	}
	
	/*
	public static void printTree(BasicNode node) throws TreeStringGeneratorException, IOException{		
		TreeStringGenerator<Info> tree_string = new TreeStringGenerator<Info>(new IdLemmaPosRelNodeString(), node);
		String tree = tree_string.generateString();
		FileWriter fstream = new FileWriter("C:/tree.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(tree);
		out.close();	
	}
	
	public  static String cleanLemma(String lemma) {
		lemma = lemma.trim();
		lemma = lemma.trim().toLowerCase().replaceAll("[^A-Za-z]", "");
		return lemma;
	}*/
}

