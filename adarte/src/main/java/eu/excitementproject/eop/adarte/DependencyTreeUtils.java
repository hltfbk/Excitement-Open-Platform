package eu.excitementproject.eop.adarte;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

import org.apache.uima.jcas.JCas;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.apache.uima.fit.util.JCasUtil.selectCovered;

/**
 * 
 * This class contains same utility methods for managing the dependency trees and the
 * text containing them.
 * 
 * @author roberto zanoli
 * @author silvia colombo
 * 
 * @since January 2015
 * 
*/
public class DependencyTreeUtils {

	
	/*
	 * check if the text contains multiple sentences.
	 * 
	 * @param text the text
     * 
     * @return true in case of multiple sentences
	 */
	protected static boolean checkMultiSentences(String text){
		
		String[] trees = text.split("\n\n");
		
		return trees.length>1;
		
	}
	
	
	/*
	 * check if the text contains multiple trees.
	 * 
	 * @param text the text
     * 
     * @return true in case of multiple trees
	 */
	protected static boolean checkMultiTree(String text){
		
		Pattern p = Pattern.compile("\t_\t_\t_\t_\t_\t_\n");
		Matcher m = p.matcher(text);
		int count = 0;
		while (m.find()){
			count +=1;
		}
		
		if (count > 1)
			return true;
		
		return false;
		
	}
	
	
	/*
	 * check if the text contains PhrasalVerbs
	 * 
	 * @param text the text
     * 
     * @return true in case of PhrasalVerbs
	 */
	protected static boolean checkPhrasalVerbs(String text) {
		
		String[] splitLines = text.split("\n");
		for (int i = 0; i < splitLines.length; i++) {
			String[] fields = splitLines[i].split("\t");
			if (fields[7].equalsIgnoreCase("prt"))
					return true;
		}
		
		return false;
		
	}
	
	
	/*
	 * check if the text contains some punctuation
	 * 
	 * @param text the text
     * 
     * @return true in case of punctuation
	 */
	protected static boolean checkPunctuation(String text) {
		
		String[] splitLines = text.split("\n");
		for (int i = 0; i < splitLines.length; i++) {
			String[] fields = splitLines[i].split("\t");
			if (fields[7].equalsIgnoreCase("punct") || fields[7].equalsIgnoreCase("PUNC"))
				return true;
		}
		
		return false;
		
	}
	
	
	/*
	 * It creates a new tree where the provided trees have been attached in.
	 * 
	 * @param trees
     * 
     * @return a new tree 
	 */
	protected static String createFakeTree(String multiTree) {
		
		StringBuffer newTree = new StringBuffer();
		
		String newNode = "1\t_\t_\t_\t_\t_\t_\t_\t_\t_\n";
		newTree.append(newNode);
		
		String[] splitLines = multiTree.split("\n");
		for (int i = 0; i < splitLines.length; i++) {
			
			String[] tokens = splitLines[i].split("\t");
			
			for (int j = 0; j < tokens.length; j++) {
				
				//token id, or link id
				if (j == 0 || j == 6) {
					
					if (j == 6 && tokens[j].equals("_"))
						tokens[j] = "1";
					else {
						int num = Integer.parseInt(tokens[j]);
						num++;
						tokens[j] = Integer.toString(num); 
					}
				}
				newTree.append(tokens[j]);
				if (j < tokens.length - 1)
					newTree.append("\t");
				
			}
			newTree.append("\n");
			
		}
		
		newTree.append("\n");
		return newTree.toString();
		
	}
	

	/*
	 * It merges the provided trees
	 * 
	 * @param trees
     * 
     * @return a new tree 
	 */
	protected static String mergeTrees(String multiTree) {
		
		String[] trees = multiTree.split("\n\n");
		String newTree = "";
		//add new node
		newTree+="1\t_\t_\t_\t_\t_\t_\t_\t_\t_\n";
		int prevtreelenght = 1;
		
		for (int i = 0; i < trees.length; i++){
			String tree = trees[i];
			String[] lines = tree.split("\n");
			for(int j = 0; j<lines.length; j++){
				String[] fields = lines[j].split("\\s");
				int tokenId = Integer.parseInt(fields[0]);
				fields[0] = (tokenId + prevtreelenght) + "";
				if(fields[6].equals("_")){
					fields[6] = "1";
				}
				else {
					fields[6] = (Integer.parseInt(fields[6]) + prevtreelenght) + "";
				}
				String line = "";
				for (String field:fields){
					line+= field + "\t"; 
				}
				lines[j]=line;
				newTree+=line+"\n";
			}
			prevtreelenght+=lines.length;
		}
		
		return newTree;
	   
	}
	
	
	    
	/**
	 * Given a cas (it contains the T view or the H view) in input it produces a
	 * string containing the tree in the CoNLL-X format, e.g. 
	 * 
	 * 1	A	a	DT	_	_	3	det	_	_
	 * 2	soccer	soccer	NN	_	_	3	nn	_	_
	 * 3	ball	ball	NN	_	_	6	nsubj	_	_
	 * 4	is	be	VBZ	_	_	6	aux	_	_
	 * 5	not	not	RB	_	_	6	neg	_	_
	 * 6	rolling	roll	VBG	_	_	_	_	_	_
	 * 7	into	into	IN	_	_	6	prep	_	_
	 * 8	a	a	DT	_	_	10	det	_	_
	 * 9	goal	goal	NN	_	_	10	nn	_	_
	 * 10	net	net	NN	_	_	7	pobj	_	_
	 * 11	.	.	.	_	_	6	punct	_	_
     * 
     *  @param aJCas the cas
	 *  
	 *  @return the tree in the CoNLL-X format
	 */
	protected static String cas2CoNLLX(JCas aJCas) throws Exception {
	    	
	    StringBuffer result = new StringBuffer();
	    	
	    try { 
	    	
	    	// StringBuilder conllSb = new StringBuilder();
	    	for (Sentence sentence : select(aJCas, Sentence.class)) {
	    		// Map of token and the dependent (token address used as a Key)
	    		Map<Integer, Integer> dependentMap = new HashMap<Integer, Integer>();
	    		// Map of governor token address and its token position
	    		Map<Integer, Integer> dependencyMap = new HashMap<Integer, Integer>();
	    		// Map of governor token address and its dependency function value
	    		Map<Integer, String> dependencyTypeMap = new HashMap<Integer, String>();
	
	            for (Dependency dependecny : selectCovered(Dependency.class, sentence)) {
	            	dependentMap.put(dependecny.getDependent()
	            			.getAddress(), dependecny.getGovernor().getAddress());
	            }
		
	            int i = 1;
	            for (Token token : selectCovered(Token.class, sentence)) {
	            	dependencyMap.put(token.getAddress(), i);
	            	i++;
	            }
		
	            for (Dependency dependecny : selectCovered(Dependency.class, sentence)) {
	            	dependencyTypeMap.put(dependecny.getDependent().getAddress(),
	            			dependecny.getDependencyType());
	            }
		
	            int j = 1;
	            
	            for (Token token : selectCovered(Token.class, sentence)) {
	            	String lemma = token.getLemma() == null ? "_" : token.getLemma().getValue();
	            	String pos = token.getPos() == null ? "_" : token.getPos().getPosValue();
	            	String dependent = "_";
		
	            	if (dependentMap.get(token.getAddress()) != null) {
	            		if (dependencyMap.get(dependentMap.get(token.getAddress())) != null) {
		                        dependent = "" + dependencyMap.get(dependentMap.get(token.getAddress()));
	            		}
	            	}
	            	String type = dependencyTypeMap.get(token.getAddress()) == null ? "_"
		                        : dependencyTypeMap.get(token.getAddress());
		
	            	if (dependentMap.get(token.getAddress()) != null
	            			&& dependencyMap.get(dependentMap.get(token.getAddress())) != null
	            			&& j == dependencyMap.get(dependentMap.get(token.getAddress()))) {
	            		// IOUtils.write(j + "\t" + token.getCoveredText() + "\t" + lemma + "\t" + pos
	            		// + "\t_\t_\t" + 0 + "\t" + type + "\t_\t_\n", aOs, aEncoding);
	            		result.append(j + "\t" + token.getCoveredText() + "\t" + lemma + "\t" + pos
	            				+ "\t_\t_\t" + 0 + "\t" + type + "\t_\t_\n");
	            	}
	            	else {
	            		//IOUtils.write(j + "\t" + token.getCoveredText() + "\t" + lemma + "\t" + pos
	            		//  + "\t_\t_\t" + dependent + "\t" + type + "\t_\t_\n", aOs, aEncoding);
	            		result.append(j + "\t" + token.getCoveredText() + "\t" + lemma + "\t" + pos
	            				+ "\t_\t_\t" + dependent + "\t" + type + "\t_\t_\n");
	            	}
	            	j++;
	            }
		            
	            //IOUtils.write("\n", aOs, aEncoding);
	            //System.out.print("\n");
	            result.append("\n");
		            
	    	}
	        
	    } catch (Exception e) {
			
	    	throw new Exception(e.getMessage());
			
	    }
	        
	    return result.toString();
	        
	}

	    
	/**
	 * Given a dependency tree it removes the punctuation (the punct marker in the
	 * CoNLL-X file is used to recognize the punctuation). 
	 * 
	 * @param dependencyTree the tree
	 * 
	 * @return the tree in input without the punctuation
	 * 
	 */
	protected static String removePunctuation(String dependencyTree){

		String cleaned_tree = "";
	    	
	    Boolean hasChild = false;
	    String[] lines = dependencyTree.split("\n");
	     
	    for (int i = 0; i < lines.length; i++) {
	    	if(!lines[i].isEmpty()){
	    		String[] fields = lines[i].split("\\s");
	    		int tokenId = Integer.parseInt(fields[0]);
	    			//if(fields[7].equals("punct")){
	    		    if(fields[7].equalsIgnoreCase("punct") || fields[7].equalsIgnoreCase("PUNC")){ //added by roberto for German language
	    				//checking for children
	    				for (int j = 0; j < lines.length; j++){
	    					if(!lines[j].isEmpty()){
	    						String[] fieldsj = lines[j].split("\\s");
	    						if(fieldsj[6].equals(tokenId+"")){
	    							hasChild = true;
	    						}
	    					}
	    				}
	    				//update stage
	    				if (!hasChild) {
	    					lines[i]="";
	    					for (int j = 0; j < lines.length; j++){
	    						if(!lines[j].isEmpty()) {
	    							String[] fieldsj = lines[j].split("\\s");
	    							//updating the IDs for the deletion
	    							if(Integer.parseInt(fieldsj[0]) >= tokenId) {
	    								fieldsj[0] = (Integer.parseInt(fieldsj[0])-1)+"";
	    							}
	    							//updating the heads. I assume that the root cannot be a punctuation mark
	    							if(!fieldsj[6].equals("_") && Integer.parseInt(fieldsj[6]) > tokenId) {
	    								fieldsj[6] = (Integer.parseInt(fieldsj[6])-1)+"";
	    							}
	    							String line = "";
	    							for (String field:fieldsj) {
	    								line+= field + "\t";
	    							}
	    							lines[j]=line;
	    						}
	    					}
	    				}
	    		    }
	    		}
	    	}
	    	for (int i = 0; i < lines.length; i++){
	    		if(!lines[i].isEmpty())
	    			cleaned_tree+=lines[i]+"\n";
	    	}
	    	
	    	return cleaned_tree+"\n";
	    	
	}
	
	/**
	 * Given a dependency tree it merges the nodes that are part of a same phrasal verb
	 * 
	 * @param dependencyTree the tree
	 * 
	 * @return the tree in input rith the nodes merged
	 * 
	 */
	protected static String mergePhrasalVerbs(String dependencyTree){

		String cleaned_tree = "";
	    	
	    Boolean hasChild = false;
	    String[] lines = dependencyTree.split("\n");
	     
	    for (int i = 0; i < lines.length; i++) {
	    	if(!lines[i].isEmpty()){
	    		String[] fields = lines[i].split("\\s");
	    		int tokenId = Integer.parseInt(fields[0]);
	    			//if(fields[7].equals("punct")){
	    		    if(fields[7].equalsIgnoreCase("prt")) { 
	    		    	String prepositionToken = fields[1];
	    		    	String prepositionLemma = fields[2];
	    		    	int prepositionHead = Integer.parseInt(fields[6]);
	    				//checking for children
	    				for (int j = 0; j < lines.length; j++){
	    					if(!lines[j].isEmpty()){
	    						String[] fieldsj = lines[j].split("\\s");
	    						if(fieldsj[6].equals(tokenId+"")) {
	    							hasChild = true;
	    						}
	    					}
	    				}
	    				//update stage
	    				if (!hasChild) {
	    					lines[i]="";
	    					for (int j = 0; j < lines.length; j++){
	    						if(!lines[j].isEmpty()) {
	    							String[] fieldsj = lines[j].split("\\s");
	    							
	    							if ( (j + 1) == prepositionHead ) {
	    								
	    								fieldsj[1] = fieldsj[1] + "_|_" +  prepositionToken;
	    								fieldsj[2] = fieldsj[2] + "_|_" +  prepositionLemma;
	    								
	    							}
	    							
	    							//updating the IDs for the deletion
	    							if(Integer.parseInt(fieldsj[0]) >= tokenId) {
	    								fieldsj[0] = (Integer.parseInt(fieldsj[0])-1)+"";
	    							}
	    							//updating the heads. I assume that the root cannot be a punctuation mark
	    							if(!fieldsj[6].equals("_") && Integer.parseInt(fieldsj[6]) > tokenId) {
	    								fieldsj[6] = (Integer.parseInt(fieldsj[6])-1)+"";
	    							}
	    							String line = "";
	    							for (String field:fieldsj) {
	    								line+= field + "\t";
	    							}
	    							lines[j]=line;
	    						}
	    					}
	    				}
	    		    }
	    		}
	    	}
	    	for (int i = 0; i < lines.length; i++){
	    		if(!lines[i].isEmpty())
	    			cleaned_tree+=lines[i]+"\n";
	    	}
	    	
	    	return cleaned_tree+"\n";
	    	
	}
	
}
