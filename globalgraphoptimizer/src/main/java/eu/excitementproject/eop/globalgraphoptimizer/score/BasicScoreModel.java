package eu.excitementproject.eop.globalgraphoptimizer.score;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;

/**
 * @author Meni Adler
 * @since 24/08/2011
 *
 * Basic implementation of the ScoreModel interface
 */
public class BasicScoreModel implements ScoreModel {

	public BasicScoreModel(Map<Pair<String,String>,Double> mapTemplatePair2Score) {
		this(mapTemplatePair2Score,0);
	}
	
	public BasicScoreModel(Map<Pair<String,String>,Double> mapTemplatePair2Score, double entailmentThreshold) {
		this.mapTemplatePair2Score = mapTemplatePair2Score;
		for (Entry<Pair<String, String>, Double> entry : mapTemplatePair2Score.entrySet()) {
			if (entry.getValue() < entailmentThreshold)
				sNonEntailing.add(entry.getKey());
			else
				sEntailing.add(entry.getKey());
		}
	}
	
	public BasicScoreModel(InputStream in) throws IOException {
		this(in,0,0);
	}
	
	public BasicScoreModel(File root, double entailmentThreshold, double edgeCost) throws IOException {
		init();
		if (root.isDirectory()) {
			for (File f : root.listFiles()) {
				read(new FileInputStream(f),entailmentThreshold, edgeCost);
				System.out.println(f + " was load");
			}
		} else {
			read(new FileInputStream(root),entailmentThreshold, edgeCost);
		}
	}
	
	/**
	 * @param in an input stream where each line is of the form: template1 template2 score
	 * @param entailmentThreshold  positive/negative (entailing / not entailing) score threshold
	 * @throws IOException
	 */
	public BasicScoreModel(InputStream in, double entailmentThreshold, double edgeCost) throws IOException {
		init();
		read(in,entailmentThreshold, edgeCost);
	}

	protected void init() {
		mapTemplatePair2Score = new HashMap<Pair<String,String>,Double>();
		sEntailing = new HashSet<Pair<String,String>>();
		sNonEntailing = new HashSet<Pair<String,String>>();

	}
	
	protected void read(InputStream in, double entailmentThreshold, double edgeCost) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		while ((line=reader.readLine())!=null) {			
			String[] toks = line.split("\t");
			String template1 = toks[0].trim();
			String template2 = toks[1].trim();
			double score = Double.parseDouble(toks[2]);
			Pair<String, String> pair = new Pair<String,String>(template1,template2);
			mapTemplatePair2Score.put(pair, score);
			if (score  - edgeCost < entailmentThreshold)
				sNonEntailing.add(pair);
			else
				sEntailing.add(pair);			
		}
		reader.close();
	}
	

	/* (non-Javadoc)
	 * @see org.BIU.NLP.ontology.learn.graph.global.ScoreModel#getEntailmentScore(org.BIU.NLP.ontology.learn.graph.rep.RelationNode, org.BIU.NLP.ontology.learn.graph.rep.RelationNode)
	 */
	@Override
	public double getEntailmentScore(RelationNode fromNode, RelationNode toNode) throws Exception {		
		return getEntailmentScore(fromNode.description(), toNode.description());
	}

	/* (non-Javadoc)
	 * @see org.BIU.NLP.ontology.learn.graph.global.ScoreModel#getEntailmentScore(java.lang.String, java.lang.String)
	 */
	@Override
	public double getEntailmentScore(String desc1, String desc2) throws Exception {
		Double score = mapTemplatePair2Score.get(new Pair<String,String>(desc1,desc2));
		if (score == null)
			score = -1.0;
			//throw new UnknownScoreException("Unknown score for " + desc1 + " -> " + desc2);
		return score;
	}

	/* (non-Javadoc)
	 * @see org.BIU.NLP.ontology.learn.graph.global.ScoreModel#getEntailing()
	 */
	@Override
	public Set<Pair<String, String>> getEntailing() {
		return sEntailing;
	}

	/* (non-Javadoc)
	 * @see org.BIU.NLP.ontology.learn.graph.global.ScoreModel#getNonEntailing()
	 */
	@Override
	public Set<Pair<String, String>> getNonEntailing() {
		return sNonEntailing;
	}
	

	protected Map<Pair<String,String>,Double> mapTemplatePair2Score;
	protected Set<Pair<String,String>> sEntailing;
	protected Set<Pair<String,String>> sNonEntailing;
}
