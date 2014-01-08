package eu.excitementproject.eop.transformations.operations.rules.distsimnew;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.BIU.NLP.DIRT.binary.BinaryDIRTUtils;
import org.BIU.NLP.DIRT.binary.GraphPathExtractor;
import org.BIU.NLP.DIRT.binary.TemplateInstancesExtractor;
import org.BIU.NLP.DIRT.lexical.LexicalExtractor.LexicalElementInfo;
import org.BURST.NLP.TE.impl.Minipar.MiniparSentence;
import org.BURST.NLP.TE.rep.Edge;
import org.BURST.NLP.TE.rep.Node;
import org.BURST.NLP.TE.rep.Term.Type;
import org.BURST.NLP.TE.rep.Tree;
import org.BURST.NLP.TE.rep.TreeNode;
import org.BURST.NLP.rep.POS;
import org.BURST.NLP.utils.TreeUtils;
import org.BURST.v2bridge.BurstConvertException;
import org.BURST.v2bridge.GenericTreeToBurstSentence;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.transformations.utilities.TeEngineMlException;


/**
 * 
 * Extensively uses legacy code to extract DIRT-like templates from a given tree. 
 * 
 * @author Jonathan Berant and Asher Stern 
 * @since Aug 2, 2011
 *
 */
public class TemplatesFromTree<T extends Info, S extends AbstractNode<T,S>> extends TemplateInstancesExtractor
{
	public TemplatesFromTree(S tree) throws TeEngineMlException
	{
		super();
		try
		{
			ConfigurationParams params = createLegacyConfigurationParams();
			this.tree = tree;
			super.init(params);
			m_utils = new BinaryDIRTUtils();
			m_utils.init(params);
		}
		catch(Exception e)
		{
			throw new TeEngineMlException("Unknown problem. See nested",e);
		}
	}

	public void createTemplate() throws TeEngineMlException
	{
		try
		{
			GenericTreeToBurstSentence<T, S> converter = new GenericTreeToBurstSentence<T, S>(tree);
			converter.create();
			MiniparSentence burstTree = converter.getBurstSentence();
			List<TupleExtraction> listTuples = extractTuples(burstTree);
			templates = new LinkedHashSet<String>(listTuples.size());
			for (TupleExtraction tuple : listTuples)
			{
				String templateString = tuple.getPredicate();
				templates.add(templateString);
			}
		}
		catch(BurstConvertException e)
		{
			throw new TeEngineMlException("convert to burst problem",e);
		}
	}
	

	public Set<String> getTemplates() throws TeEngineMlException
	{
		if (null==templates) throw new TeEngineMlException("Not created");
		return templates;
	}

	/**
	 * Ugly legacy solution.
	 * @return
	 */
	protected static ConfigurationParams createLegacyConfigurationParams()
	{
		ConfigurationParams ret = new ConfigurationParams();
		ret.put("verbose-level","1");
		ret.put("lexical-extractor-class","org.BIU.NLP.DIRT.lexical.NounPhraseElementExtractor");
		ret.put("element-type","NOUN_TO_NOUN");
		ret.put("algorithm-type","TEASE");
		
		return ret;
	}

	

	
	// TODO: Handle the problem of pronoun
	
	// CODE by Jonathan Berant
	// (Asher: comments below were written during my futile effort to understand
	// the code... I'm not sure they're correct...)
	

	/**
	 * Extracting instances from a template. Differences from implementation of super <br/>
	 * <ol>
	 * <li> Extract only from sentences with less than 100 words and with a predicate </li>
	 * <li> Allow only NOUN-TO-NOUN patterns and takes into account that features are CUIs</li>
	 * <li> Deleting commented code </li>	
	 * </ol>
	 */
	private List<TupleExtraction> extractTuples(Tree iTree)
	{

		List<TupleExtraction> result = new LinkedList<TupleExtraction>();	
		Hashtable<Node, HashSet<Node>> nodeInfos = new Hashtable<Node, HashSet<Node>>();
		HashSet<Node> set, done;
		Node node;
		StringBuffer xpath, ypath;
		int xc, yc;
		Edge edge;
		String root;
		String leftFeature;
		String element;
		Hashtable<Node, List<String>> lexInfos = new Hashtable<Node, List<String>>();
		List<String> xinfos, yinfos;
		char posChar;

		if(!TreeUtils.isSentence(iTree))
			return result;

		for(TreeNode n : iTree.nodes()) {
			if(!isPosNoun(n.term().posTag()) || n.term().type()== Type.CLAUSE)
				continue;

			posChar = firstPosLetter(n.term().posTag());

			// It seems that xinfos is a list and each element represents a child
			// of the node, under some constraints.
			xinfos = new LinkedList<String>();
			for(LexicalElementInfo info : m_lexicalExtractor.extractNounPhrases(n, true)){
				leftFeature = m_utils.normalizeFeature(info.lemma() + ":" + posChar);
				if(m_utils.isFeatureValid(leftFeature))
					xinfos.add(leftFeature);
			}

			// lexInfos is a map from a node to a list of children
			lexInfos.put(n, xinfos);

			// Here, the nodeInfos is built. It will be a map from each node
			// to a set of nodes that are "connected" to that node, in the "up"
			// direction: i.e. the set contains the node and all ancestors,
			// until an empty node was encountered.
			node = n;
			while(node != null){
				set = nodeInfos.get(node);
				if(set == null){
					set = new HashSet<Node>();
					nodeInfos.put(node, set);
				}
				set.add(n);

				edge = GraphPathExtractor.getInEdge(node);
				if(edge != null){
					node = edge.from();
					if(node.term().lemma() == null || node.term().lemma().length() == 0)
						node = null;
				}
				else
					node = null;
			}
		}


		for(TreeNode nx : iTree.nodes()){
			if(!isPosNoun(nx.term().posTag()) || nx.term().type()== Type.CLAUSE || !lexInfos.containsKey(nx))
				continue;

			xinfos = lexInfos.get(nx);
			if(xinfos.size()==0)
				continue;

			done = new HashSet<Node>();
			done.add(nx);

			xc = 0;
			xpath = new StringBuffer();
			xpath.append(nx.term().posTag().toString().charAt(0));

			// It seems that this loop starts with the node "nx",
			// and goes upwards - from "nx" to its parent, and to its parent...
			node = nx;
			while(node != null){
				if(node != nx){
					
					root = firstPosLetter(node.term().posTag()) + ":" + node.term().lemma() + (node.negation() ? "-:" : ":") + 
					firstPosLetter(node.term().posTag());
					if(isPosNoun(node.term().posTag()) || node.term().posTag() == POS.VERB || node.term().posTag() == POS.ADJ)
						xc++;
				}
				else
					root = null;

				set = nodeInfos.get(node);
				for(Node ny : set){
					if(done.contains(ny))
						continue;

					done.add(ny);

					if(!isPosNoun(ny.term().posTag()) || lexInfos.get(ny).size()==0) 
						continue;

					ypath = new StringBuffer();
					yc = GraphPathExtractor.getYPath(node, ny, ypath);
					if(node == ny)
						yc = -1;

					if((isPosNoun(ny.term().posTag()) && xc + yc <= 0) || xc + yc > MAX_LEXICALS) // dont require lexical elements between N and V
						continue;


					element = ((node == ny || root == null) ? xpath.toString() + ypath.toString() : xpath.toString() + root + ypath.toString());
					element = m_utils.normalizeAndCanonizeElement(element);
					if(m_utils.isElementValid(element)){

						yinfos = lexInfos.get(ny);
						for(String xfeature : xinfos){
							for(String yfeature : yinfos){
								if(xfeature.equals(yfeature) || xfeature.endsWith(yfeature) || yfeature.endsWith(xfeature))
									continue;

								result.add(new TupleExtraction(xfeature,yfeature,element));
							}
						}

					}
				}

				edge = GraphPathExtractor.getInEdge(node);
				if(edge != null){
					if(root != null){
						xpath.append(root);
						xpath.append('<').append(edge.rel()).append('<');
					}
					else
						xpath.append('<').append(edge.rel()).append('<');

					node = edge.from();
					if(node.term().lemma() == null || node.term().lemma().length() == 0)
						node = null;
				}
				else
					node = null;
			}
		}
		return result;
	}
	
	private static boolean isPosNoun(POS pos)
	{
		return ( (pos==POS.NOUN) || (pos==POS.PRONOUN) );
	}
	
	private static char firstPosLetter(POS pos)
	{
		if (pos==POS.PRONOUN) return POS.NOUN.toString().charAt(0);
		else return pos.toString().charAt(0);
	}

	private static final class TupleExtraction {

		public TupleExtraction(String argx, String argy, String predicate) {
			m_argX = argx;
			m_argY = argy;
			m_predicate = predicate;
		}

		public String getPredicate() {
			return m_predicate;
		}
		@SuppressWarnings("unused")
		public String getArgX() {
			return m_argX;
		}
		@SuppressWarnings("unused")
		public String getArgY() {
			return m_argY;
		}

		private final String m_predicate;
		private final String m_argX;
		private final String m_argY;

	}
	
	private S tree;
	private Set<String> templates = null;
}
