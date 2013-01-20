package eu.excitementproject.eop.lap.biu.en.coreference.arkref.workaround;

import java.util.ArrayList;

import arkref.analysis.ARKref.Opts;
import arkref.analysis.Sem;
import arkref.analysis.SyntacticPaths;
import arkref.analysis.Types;
import arkref.data.Document;
import arkref.data.Mention;
import arkref.data.Sentence;
import arkref.parsestuff.AnalysisUtilities;
import arkref.parsestuff.TregexPatternFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

/**
 * This is a copy of the class arkref.analysis.Resolve.
 * Since it throws {@link NullPointerException}s, I had to copy and modify it.
 * 
 * @author Asher Stern
 * @since Jul 30, 2012
 *
 */
public class Resolve {
	public static void go(Document d) {
		Mention antecedent;
		Sentence curS = null;
		for (Mention m : d.mentions()) {
			if (m.getSentence() != curS) {
				curS = m.getSentence();
			}
			if (m.node()==null) {
				continue;
			}
			if (Types.isPronominal(m)) {
				resolvePronoun(m, d);
			//} else if (isRelativePronoun(m)){
			//	resolveRelativePronoun(m, d);
			} else if (inAppositiveConstruction(m)) {
				resolveAppositive(m, d);
			} else if ((antecedent = findAntecedentInRoleAppositiveConstruction(m,d)) != null) {
				d.refGraph().setRef(m, antecedent);
				reportResolution("role-appos", m, antecedent);
			} else if ((antecedent = findAntecendentInPredicateNominativeConstruction(m, d)) != null) {
				d.refGraph().setRef(m, antecedent);
				reportResolution("pred-nom", m, antecedent);
			} else {
				resolveOther(m, d);
			}
			
			
			if (d.refGraph().getFinalResolutions().get(m) == null) {
				boolean hadAChance = isThereAGoldAntecedent(d, m);
				reportResolution("null", m, null, hadAChance);
				if (hadAChance && m.aceMention!=null && !Types.isPronominal(m)) {
					printGoldAntecedents(d,m);
				}
			}
		}
	}
	
	public static void reportResolution(String reason, Mention mention, Mention ref) {
		reportResolution(reason,mention,ref, true);
	}
	/** really anal-retentive output format to enable grep-based statistical analysis **/
	public static void reportResolution(String reason, Mention mention, Mention ref, boolean hadAChance) {
		@SuppressWarnings("unused")
		String eval = null;
		if (mention.aceMention!=null && ref==null) {
			eval = mention.aceMention.isSingleton() ? "RIGHT " : (hadAChance ? "WRONG " : "NOCHANCE ");
		} else if (mention.aceMention!=null && ref.aceMention!=null) {
			eval = mention.aceMention.entity == ref.aceMention.entity ? "RIGHT " : (hadAChance ? "WRONG " : "NOCHANCE ");
		} else {
			eval = "";
		}
		if (ref==null) {
		} else {
		}
	}	
	public static void printGoldAntecedents(Document d, Mention m) {
		for (Mention ant : d.prevMentions(m)) {
			if (ant.aceMention!=null && 
					ant.aceMention.entity == m.aceMention.entity)
				{}
		}

	}
	public static boolean isThereAGoldAntecedent(Document d, Mention m) {
		if (m.aceMention == null) return true; // weird
		for (Mention ant : d.prevMentions(m)) {
			if (ant.aceMention!=null && 
					ant.aceMention.entity == m.aceMention.entity)
				return true;
		}
		return false;
	}
	
	

	/*
	private static void resolveRelativePronoun(Mention mention, Document d) {
		Tree root = mention.getSentence().rootNode();
		Tree node = mention.node();
		Tree parent = node.parent(root);
		
		parent = SyntacticPaths.getMaximalProjection(parent, root);
		
		for (Mention cand : d.prevMentions(mention)) {
			if(cand.node() == parent){
				d.refGraph().setRef(mention, cand);
				break;
			}
		}
		
		Mention ref = d.refGraph().getFinalResolutions().get(mention);
		if(ref != null){
			U.pf("resolved relative pronouns M%-2d -> M%-2d    %20s    ->   %-20s\n", 
					mention.ID(), ref.ID(), AnalysisUtilities.abbrevTree(mention.node()),
					 AnalysisUtilities.abbrevTree(ref.node()));
		}
	}


	private static boolean isRelativePronoun(Mention m) {
		if (m.node()==null) return false;
		TregexMatcher matcher = TregexPatternFactory.getPattern("__ <<# WDT|IN|WRB|WP !> __").matcher(m.node());
		return matcher.find();
	}
	 */

	/**
	 * 
	 * Note: This is slightly different than what is described in H&K EMNLP 09.
	 * I think the head rules they used were slightly different (or possibly their description is a little off).
	 * 
	 * @param m
	 * @param d
	 * @return
	 */
	private static Mention findAntecedentInRoleAppositiveConstruction(Mention m, Document d) {
		Tree root = m.getSentence().rootNode();
		Tree node = m.node();
		Tree parent = node.parent(root);
		
		//System.err.println("mention:"+node.yield().toString()+"\thead:"+node.headTerminal(AnalysisUtilities.getInstance().getHeadFinder()).yield().toString());
		if(!parent.label().value().equals("NP")){
			return null;
		}
		
		int index = parent.indexOf(node);
		if(index+1 >= parent.numChildren()){
			return null;
		}
		
		
		TregexPattern pat = TregexPatternFactory.getPattern("NP=parent !> __ <<# (NNP=head ,, NP=mention)");
		TregexMatcher matcher = pat.matcher(parent);
		while (matcher.find()) {
			if (matcher.getNode("mention") == node){
				Tree head = matcher.getNode("head");

				//find maximal projection of the head of the parent
				Tree maxProj = SyntacticPaths.getMaximalProjection(head, root);
				
				//find the mention for the parent
				for(Mention cand:d.mentions()){
					if(cand.node() == maxProj){
						// Asher, 20-January-2013 - yet another workaround:
						// Wrapping the "if" by try...catch
						try
						{
							if(Types.personhood(cand) == Types.Personhood.Person){
								return cand;
							}
						}
						catch(NullPointerException npe)
						{
							// Do nothing. Just assume that Types.personhood(cand) != Types.Personhood.Person
						}
						break;
					}
				}
			}
		}
		
		return null;
	}

	/**
	 * returns the antecedent NP or null
	 * The way this method is called could be made more efficient.  
	 * It doesn't really need to get called for every mention
	 * 
	 */
	private static Mention findAntecendentInPredicateNominativeConstruction(Mention m, Document d) {
		Tree root = m.getSentence().rootNode();
		Tree node = m.node();
		
		TregexPattern pat = TregexPatternFactory.getPattern("S < NP=np1 <+(VP) (VP < (/^VB.*/ < be|is|was|were|are|being|been) < NP=np2)");
		TregexMatcher matcher = pat.matcher(root);
		while (matcher.find()) {
			if(matcher.getNode("np2") == node){
				Tree ante  = matcher.getNode("np1");
				for(Mention m2: d.mentions()){
					if(ante == m2.node()){
						return m2;
					}
				}
			}
		}
		
		return null;
	}

	
	/**
	 * return true when m is the third child in of a parent who expands as
	 * NP -> NP , NP .*
	 * 
	 * @param m
	 * @return
	 */
	private static boolean inAppositiveConstruction(Mention m) {
		if (m.node()==null) return false;
		Tree root = m.getSentence().rootNode();
		Tree node = m.node();
		Tree parent = node.parent(root);
		
		if(parent.numChildren()<3){
			return false;
		}else if(!parent.getChild(0).label().value().equals("NP")){
			return false;
		}else if(!parent.getChild(1).label().value().equals(",")){
			return false;
		}else if(parent.indexOf(node) != 2){
			return false;
		}
	
		//check to make sure this isn't a conjunction
		for(Tree sibling: parent.getChildrenAsList()){
			if(sibling.label().value().equals("CC")){
				return false;
			}
		}
		
		return true;
	}

	
	public static void resolveAppositive(Mention mention, Document d) {
		Tree root = mention.getSentence().rootNode();
		Tree node = mention.node();
		Tree parent = node.parent(root);
		
		for (Mention cand : d.prevMentions(mention)) {
			if(cand.node() == parent) {
				d.refGraph().setRef(mention, cand);
				reportResolution("appos", mention, cand);
				break;
			}
		}
		
	}
	
	public static void resolvePronoun(Mention mention, Document d) {
		
		ArrayList<Mention> candidates = new ArrayList<Mention>();
	
		for (Mention cand : d.prevMentions(mention)) {
			boolean match = false;
			try
			{
				match = Types.checkPronominalMatch(mention, cand);
			}
			catch(NullPointerException npe)
			{
				// do nothing. Workaround. match is false.
			}
			catch(ArrayIndexOutOfBoundsException aiofbe)
			{
				// do nothing. Workaround. match is false.
			}
			
			if (cand.node() == null) {
				match = false;
			}else if (SyntacticPaths.aIsDominatedByB(mention, cand)){
				 // I-within-I constraint
				//U.pl("fails A dominates B test");
				match = false;
			} else if (!Types.isReflexive(mention) && SyntacticPaths.inSubjectObjectRelationship(cand, mention)){
				//U.pl("fails reflexive test");
				match = false;
			} else if (SyntacticPaths.isSubjectAndMentionInAdjunctPhrase(mention, cand)){
				//U.pl("fails adjunct test");
				match = false;
			}
		
			
			if (match) {
				@SuppressWarnings("unused")
				String s="";
				if (mention.aceMention!=null & cand.aceMention!=null) {
					boolean gold_match = mention.aceMention.entity==cand.aceMention.entity;
					s = gold_match ? "[gold RIGHT]" : "[gold WRONG]";
				}
//				U.pf("PRONOUN CANDIDATE %s: %20s -> %s\n", s, mention, cand);
				if(cand.node() != null){
					candidates.add(cand);
				}
			} else {
//				U.pl("reject mismatch:  " + cand);
			}
		}
		// HACK HACK
//		if (Types.perspective(mention)==Types.Perspective.Second)
//			candidates.clear();
		if (candidates.size() == 0) {
			d.refGraph().setNullRef(mention);
		} else if (candidates.size() == 1) {
			d.refGraph().setRef(mention, candidates.get(0));
		} else if (candidates.size() > 1) {
			d.refGraph().setRef(mention, SyntacticPaths.findBestCandidateByShortestPath(mention, candidates, d)); 
		}
		Mention ref = d.refGraph().getFinalResolutions().get(mention);
		if(ref != null){
			reportResolution("pronoun", mention,ref);
		}
	}



	public static void resolveOther(Mention mention, Document d) {
		//TODO SEMANTICS!
		
		ArrayList<Mention> candidates = new ArrayList<Mention>();
		
		boolean haveSemInfo = false;//Sem.haveNP(mention);
		
		for (Mention cand : d.prevMentions(mention)) {
			Boolean match = null;
			// do while(false): it's GOTO in java.  fun, eh?
			
			DecideCandidate: do {
				if (cand.node() == null) {
					match = false; break DecideCandidate;
				}
				if (Types.isPronominal(cand)) {
					// we only do pronoun-nominal matching in the other direction
					match = false; break DecideCandidate;
				}
				if (SyntacticPaths.aIsDominatedByB(mention, cand)){// I-within-I constraint
					//U.pl("rejected due to I within I");
					match = false; break DecideCandidate;
				} 
				if (SyntacticPaths.inSubjectObjectRelationship(cand, mention)){
					//U.pl("rejected due to subj-obj constraint");
					match = false; break DecideCandidate;
				} 
				if (SyntacticPaths.isSubjectAndMentionInAdjunctPhrase(mention, cand)){
					//U.pl("rejected due to adjunct constraint");
					match = false; break DecideCandidate;
				}
				
				// Asher 4/Dec/2012: Workaround
//				try
//				{
//					if (mention.hasSameHeadWord(cand) || substringMatch(mention, cand)) { 
//						match = true; break DecideCandidate;
//					}
//				}
//				catch(NullPointerException e)
//				{
//					// if NullPointerException then it seems that the condition did
//					// not hold. do Nothing.
//				}
				try
				{
					if (mention.hasSameHeadWord(cand)) { 
						match = true; break DecideCandidate;
					}
				}
				catch(NullPointerException e)
				{
					// if NullPointerException then it seems that the condition did
					// not hold. do Nothing.
				}
				try
				{
					if (substringMatch(mention, cand)) { 
						match = true; break DecideCandidate;
					}
				}
				catch(NullPointerException e)
				{
					// if NullPointerException then it seems that the condition did
					// not hold. do Nothing.
				}
				// end of workaround
				
				if (Opts.oracleSemantics) {
					match = mention.aceMention.entity==cand.aceMention.entity;
					break DecideCandidate;
				}
				if (haveSemInfo && Sem.haveNP(cand)) {
//					if (mention.aceMention.entity==cand.aceMention.entity) {
//						U.pf("SEMANTICS PLEASE");
//					} else {
//						U.pf("SEMANTICS STOP");
//					}
//					U.pf("\t%s\t%s\n", 
//							Strings.normalizeWhitespace(mention.aceMention.head.charseq.text),
//							Strings.normalizeWhitespace(cand.aceMention.head.charseq.text));
					match = Sem.areCompatible(mention, cand);
					break DecideCandidate;
				} 
				
//				U.pl("Defaulting to reject");
				match = false;
				
			} while(false);
			assert match != null : "if/else logic screwed up!";
			
			if (match) {
//				U.pl("yay   match:\t" + cand);
				candidates.add(cand);
			} else {
//				U.pl("reject mismatch:\t" + cand);
			}
		}
		
		if (candidates.size() == 0) {
			d.refGraph().setNullRef(mention);
		} else if (candidates.size() == 1) {
			d.refGraph().setRef(mention, candidates.get(0));
		} else if (candidates.size() > 1) {
			d.refGraph().setRef(mention, SyntacticPaths.findBestCandidateByShortestPath(mention, candidates, d)); 
		}
		
		Mention ref = d.refGraph().getFinalResolutions().get(mention);
		if (ref != null){
			reportResolution("other", mention, ref);
		}
		
		//semantics!
	}

	
	private static boolean substringMatch(Mention mention, Mention cand) {
		String mHead = mention.getHeadWord();
		String cHead = cand.getHeadWord();
		//String mYield = mention.node().yield().toString();
		//String cYield = cand.node().yield().toString();
		
		/*if(mHead.length() >= 5 && cHead.length() >= 5){
			if(mHead.subSequence(0, 5).equals(cHead.subSequence(0, 5))){
				return true;
			}
		}*/
		
		//both must be proper nouns
		if(mention.node().headPreTerminal(AnalysisUtilities.getInstance().getHeadFinder()).label().toString().indexOf("NNP") != 0
			 && cand.node().headPreTerminal(AnalysisUtilities.getInstance().getHeadFinder()).label().toString().indexOf("NNP") != 0){
				 return false;
		}

		/*if(mHead.indexOf(cHead)!=-1 || cHead.indexOf(mHead)!=-1){
			return true;
		}*/
		
		int matchLen = 4;
		if(mHead.length() >= matchLen && cHead.length() >= matchLen){
			if(mHead.subSequence(0, matchLen).equals(cHead.subSequence(0, matchLen))){// && LevenshteinDistance.getLevenshteinDistance(mHead, cHead) <= 5){
				return true;
			}
		}
		
		/*if(LevenshteinDistance.getLevenshteinDistance(mHead, cHead) < 5){
			return true;
		}*/
		
		return false;
	}



	
	
}
