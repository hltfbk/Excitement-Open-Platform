package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.AbstractOntologyGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.DirectedOneMappingOntologyGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RuleEdge;
import eu.excitementproject.eop.globalgraphoptimizer.score.BasicScoreModel;

public class ScoredTemplatePairBasedEdgeLearnerResource extends GeneralEdgeLearnerResource {

	public ScoredTemplatePairBasedEdgeLearnerResource(double graphEdgeScoreThreshold) {
		this.graphEdgeScoreThreshold = graphEdgeScoreThreshold;
	}
	
	@Override
	protected void loadResource(InputStream in) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = null;
		HashMap<String, RelationNode> relationNodes = new HashMap<String,RelationNode>();
		AbstractOntologyGraph graph = new DirectedOneMappingOntologyGraph();
		Map<Pair<String,String>,Double> mapTemplatePair2Score = new HashMap<Pair<String,String>,Double>();
		int id=0;
		while ((line=reader.readLine())!=null) {
			
			String[] toks = line.split("\t");
			String template1 = toks[0];
			String template2 = toks[1];
			double score = Double.parseDouble(toks[2]);
			
			mapTemplatePair2Score.put(new Pair<String,String>(template1,template2), score);
			
			if (score > graphEdgeScoreThreshold) {
				RelationNode node1 = relationNodes.get(template1);
				if (node1 == null) {
					node1 = new RelationNode(id,template1);
					relationNodes.put(template1,node1);
					id++;
				}
				RelationNode node2 = relationNodes.get(template2);
				if (node2 == null) {
					node2 = new RelationNode(id,template2);
					relationNodes.put(template2,node2);
					id++;
				}
				graph.addEdge(new RuleEdge(node1,node2,score));
			}
		}
		scoreModel = new BasicScoreModel(mapTemplatePair2Score);
		nodeGraph = new NodeGraph(graph);
	}
	
	protected double graphEdgeScoreThreshold;

}
