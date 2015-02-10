package eu.excitementproject.eop.adarte;

/**
 * 
 * Transformations are elementary edit operations (i.e., deleting, replacing and inserting pieces 
 * of text) needed to transform a text into another. In the context of Textual Entailment, the 
 * transformations of a T-H pair are the edit operations needed to transform T into H.
 * 
 * 
 * With tree edit distance three different type of transformations can be defined:
 * 
 * -Inserting: insert a node N from the tree of H into the tree of T. 
 * -Deleting: delete a node N from the tree of T. 
 * -Replacing: change the label of a node N1 in the source tree (the tree of T) into a label of a 
 *  node N2 of the target tree (the tree of H).
 *  
 * There is a fourth transformation, called as Matching, but that does not count as an edit operation; basically 
 * it is applied on two nodes that are equal.
 * 
 * 
 * The transformations are used by the classifier as features and they can be represented at different levels
 * of specificity/genericity:
 * 
 * -General Form: it includes the transformation type while the nodes in the transformation are represented
 * with the lemma and PoS in addition to the whole chain of the dependency relations (dprel) from the current node
 * to the root of the tree.
 * 
 * -Least Specific Form: it includes the transformation type as in the General Form but the nodes in the
 * transformation are here only represented by the dependency relations (dprel) that they have with their direct
 * parent nodes, i.e.,
 * 
 * 
 * @author roberto zanoli
 * @author silvia colombo
 * 
 * @since Janaury 2015
 */
public class Transformation {
	
	/*
	 * Possible type of transformations
	 */
	//replace transformation
	public final static String REPLACE = "rep";
	//match transformation
	public final static String MATCH = "match";
	//insertion transformation
	public final static String INSERTION = "ins";
	//deletion transformation
	public final static String DELETION = "del";
	
	//transformation type, i.e. replace, match, deletion, insertion
	private String type;
	
	//the rule type such as "synonym" or "hypernym" for WordNet, "redirect" for Wikipedia  
	//that is used to produce the transformation (e.g. HYPERNYM of WordNet)
	private String resource;	
	
	//token in the Text involved in the transformation
	private FToken token_T;		 
	
	//token in the Hypothesis involved in the transformation
	private FToken token_H;		
	
	
	/**
	 * constructor
	 * 
	 * @param type the transformation type: deletion, substitution, insertion and matching
	 * @param resource the rule type such as "synonym" or "hypernym" for WordNet
	 * @param token_T the token T involved in the transformation
	 * @param token_H the token H involved in the transformation
	 * 
	 */
	public Transformation(String type, String resource, FToken token_T, FToken token_H){
		
		this.type = type;
		this.resource = resource;
		this.token_T = token_T;
		this.token_H = token_H;
		
	}
	
	
	/**
	 * constructor
	 * 
	 * @param type the transformation type: deletion, substitution, insertion and matching
	 * @param token the token involved in the transformation
	 * 
	 */
	public Transformation(String type, FToken token) {
		
		this.type = type;
		if (type.equals(INSERTION)){
			this.token_H = token;
		}
		else
			this.token_T = token;
		
	}

	
	/**
	 * Get the type of transformation
	 * 
	 * @return the type
	 * 
	 */
	protected String getType() {
		
		return this.type;
		
	}
	
	
	/**
	 * Set the type of transformation
	 * 
	 * @param type the transformation type: deletion, substitution, insertion and matching
	 * 
	 */
	protected void setType(String type) {
		
		this.type = type;
		
	}

	
	/**
	 * Get the the rule type such as "synonym" or "hypernym" for WordNet
	 * 
	 * @return the resource
	 * 
	 */
	protected String getResource() {
		
		return this.resource;
		
	}

	
	/**
	 * Get the token T
	 * 
	 * @return the token T
	 * 
	 */
	protected FToken getToken_T() {
		
		return token_T;
		
	}


	/**
	 * Get the token H
	 * 
	 * @return the token H
	 * 
	 */
	protected FToken getToken_H() {
		
		return token_H;
		
	}
	
	
	/**
	 * Print the transformation: its type, the rule type such as "synonym" or "hypernym" for WordNet used to produce 
	 * this transformation, with the tokens involved in the transformation.
	 * 
	 * @return the transformation
	 * 
	 */
	public String toString() {
		
		switch (this.type) {
		
		case REPLACE:
			
			return ("Type: " + this.type + " Resource: " + this.resource + " token_T: " + this.token_T + " token_H: " + this.token_H);
			
		case MATCH:
			return ("Type: " + this.type + " Resource: " + this.resource + " token_T: " + this.token_T + " token_H: " + this.token_H);
		
		case INSERTION:
			return ("Type: " + this.type + " token_H:: " + this.token_H );
		
		default:
			return ("Type: " + this.type + " token_T: " + this.token_T );
		
		}
		
	}
	
	
	/**
	 * Print the transformation considering different representations at different levels
     * of specificity/genericity:
	 * 
	 * LeastSpecificForm, IntermediateForm, GeneralForm
	 * 
	 * @param replace true for consider the replace transformations; false otherwise
	 * @param match true for consider the match transformations; false otherwise
	 * @param deletion true for consider the deletion transformations; false otherwise
	 * @param insertion true for consider the insertion transformations; false otherwise
	 * @param how to represent the transformations: LeastSpecificForm | IntermediateForm | GeneralForm
	 * 
	 * @return the transformation
	 */
	public String print(boolean replace, boolean match, boolean deletion, boolean insertion, String form) {
		
		if (type.equals(REPLACE) && replace == true) {
			
			if (form.equals("LeastSpecificForm"))
				return ("Type:" + this.type + "#" + "Resource:" + this.resource + "#" + "T_DPrel:" + this.token_T.getDprel() + "#" + "H_DPrel:" + this.token_H.getDprel());
			else if (form.equals("IntermediateForm"))
				return ("Type:" + this.type + "#" + "Resource:" + this.resource + "#" + "T_DPrel:" + this.token_T.getDprelRelations() + "#" + "H_DPrel:" + this.token_H.getDprelRelations());
			else // GeneralForm
				return ("Type:" + this.type + "#" + "Resource:" + this.resource + "#" + "T_DPrel:" + this.token_T.getDprelRelations() + "#" + "T_POS:" + this.token_T.getPOS() + "#" + "T_Token:" + this.token_T.getLemma() + "#" + "H_DPrel:" + this.token_H.getDprelRelations() + "#" + "H_POS:" + this.token_H.getPOS() + "#" + "H_Token:" + this.token_H.getLemma());
			
			//you can represent the representations in other ways
			//return ("Type:" + this.type + "#" + "Info:" + this.info + "#" + "T_DPrel:" + this.token_T.getDprelRelations() + "#" + "H_DPrel:" + this.token_H.getDprelRelations());
		    //return ("Type:" + this.type + "#" + "Info:" + this.info + "#" + "T_DPrel:" + this.token_T.getDprel() + "#" + "T_POS:" + this.token_T.getPOS() + "#" + "H_DPrel:" + this.token_H.getDprel() + "#" + "H_POS:" + this.token_H.getPOS());
		    //return ("Type:" + this.type + "#" + "Info:" + this.info + "#" + "T_DPrel:" + this.token_T.getDprel() + "#" + "T_POS:" + this.token_T.getPOS() + "#" + "T_Token:" + this.token_T.getLemma() + "#" + "H_DPrel:" + this.token_H.getDprel() + "#" + "H_POS:" + this.token_H.getPOS() + "#" + "H_Token:" + this.token_H.getLemma());
		}
		else if (type.equals(MATCH) && match == true) {
			
			if (form.equals("LeastSpecificForm"))
				return ("Type:" + this.type + "#" + "Resource:" + this.resource + "#" + "T_DPrel:" + this.token_T.getDprel() + "#" + "H_DPrel:" + this.token_H.getDprel());
			else if (form.equals("IntermediateForm"))
				return ("Type:" + this.type + "#" + "Resource:" + this.resource + "#" + "T_DPrel:" + this.token_T.getDprelRelations() + "#" + "H_DPrel:" + this.token_H.getDprelRelations());
			else // GeneralForm
				return ("Type:" + this.type + "#" + "Resource:" + this.resource + "#" + "T_DPrel:" + this.token_T.getDprelRelations() + "#" + "T_POS:" + this.token_T.getPOS() + "#" + "T_Token:" + this.token_T.getLemma() + "#" + "H_DPrel:" + this.token_H.getDprelRelations() + "#" + "H_POS:" + this.token_H.getPOS() + "#" + "H_Token:" + this.token_H.getLemma());
			
			//you can represent the representations in other ways
			//return ("Type:" + this.type + "#" + "Info:" + this.info + "#" + "T_DPrel:" + this.token_T.getDprelRelations() + "#" + "H_DPrel:" + this.token_H.getDeprelRelations());
		    //return ("Type:" + this.type + "#" + "Info:" + this.info + "#" + "T_DPrel:" + this.token_T.getDprel() + "#" + "T_POS:" + this.token_T.getPOS() + "#" + "H_DPrel:" + this.token_H.getDeprel() + "#" + "H_POS:" + this.token_H.getPOS());
		    //return ("Type:" + this.type + "#" + "Info:" + this.info + "#" + "T_DPrel:" + this.token_T.getDprel() + "#" + "T_POS:" + this.token_T.getPOS() + "#" + "T_Token:" + this.token_T.getLemma() + "#" + "H_DPrel:" + this.token_H.getDeprel() + "#" + "H_POS:" + this.token_H.getPOS() + "#" + "H_Token:" + this.token_H.getLemma());
		}
		else if (type.equals(INSERTION) && insertion == true) {
			
			if (form.equals("LeastSpecificForm"))
				return ("Type:" + this.type + "#" + "H_DPrel:" + this.token_H.getDprel() );
			else if (form.equals("IntermediateForm"))
				return ("Type:" + this.type + "#" + "H_DPrel:" + this.token_H.getDprelRelations());
			else // GeneralForm
				return ("Type:" + this.type + "#" + "H_DPrel:" + this.token_H.getDprelRelations() + "#" + "H_POS:" + this.token_H.getPOS() + "#" + "H_Token:" + this.token_H.getLemma());
			
			//you can represent the representations in other ways
			//return ("Type:" + this.type + "#" + "H_DPrel:" + this.token_H.getDeprelRelations() );
		    //return ("Type:" + this.type + "#" + "H_DPrel:" + this.token_H.getDprel() + "#" + "H_POS:" + this.token_H.getPOS());
		    //return ("Type:" + this.type + "#" + "H_DPrel:" + this.token_H.getDprel() + "#" + "H_POS:" + this.token_H.getPOS() + "#" + "H_Token:" + this.token_H.getLemma());
		}
		else if (type.equals(DELETION) && deletion == true) {
			
			if (form.equals("LeastSpecificForm"))
				return ("Type:" + this.type + "#" + "T_DPrel:" + this.token_T.getDprel() );
			else if (form.equals("IntermediateForm"))
				return ("Type:" + this.type + "#" + "T_DPrel:" + this.token_T.getDprelRelations());
			else // GeneralForm
				return ("Type:" + this.type + "#" + "T_DPrel:" + this.token_T.getDprelRelations() + "#" + "T_POS:" + this.token_T.getPOS() + "#" + "T_Token:" + this.token_T.getLemma());
			
			//you can represent the representations in other ways
		    //return ("Type:" + this.type + "#" + "T_DPrel:" + this.token_T.getDprelRelations() );
		    //return ("Type:" + this.type + "#" + "T_DPrel:" + this.token_T.getDprel() + "#" + "T_POS:" + this.token_T.getPOS());
		    //return ("Type:" + this.type + "#" + "T_DPrel:" + this.token_T.getDprel() + "#" + "T_POS:" + this.token_T.getPOS() + "#" + "T_Token:" + this.token_T.getLemma());
		}
		
		return null;
		
	}
	
	
}
