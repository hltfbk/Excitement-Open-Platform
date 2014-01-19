package eu.excitementproject.eop.globalgraphoptimizer.api;

import java.util.HashSet;


import java.util.Set;
import java.util.List;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Constants;
import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.edgelearners.EdgeLearner;
import eu.excitementproject.eop.globalgraphoptimizer.graph.AbstractOntologyGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.DirectedOneMappingOntologyGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.DirectedOntologyGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.UndirectedOntologyGraph;
import eu.excitementproject.eop.globalgraphoptimizer.graph.UntypedPredicateGraph;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;

/**
 * This class defines the whole learning process, for a given edge learner and graph
 * 
 * @author Jonathan Berant
 *
 */
public class UntypedPredicateGraphLearner {

	private final Logger logger = Logger.getLogger(UntypedPredicateGraphLearner.class);
	
	public UntypedPredicateGraphLearner(EdgeLearner edgeLearner) {
		this(edgeLearner, false,new HashSet<Pair<String,String>>(),new HashSet<Pair<String,String>>(),Constants.EPSILON);
	}

	public UntypedPredicateGraphLearner(EdgeLearner edgeLearner, double unknownScore) {
		this(edgeLearner, false,new HashSet<Pair<String,String>>(),new HashSet<Pair<String,String>>(),unknownScore);
	}

	public UntypedPredicateGraphLearner(EdgeLearner edgeLearner, Set<Pair<String,String>> entailings,Set<Pair<String,String>> nonentailings) {
		this(edgeLearner, false,entailings,nonentailings,Constants.EPSILON);
	}

	public UntypedPredicateGraphLearner(EdgeLearner edgeLearner, Set<Pair<String,String>> entailings,Set<Pair<String,String>> nonentailings, double unknownScore) {
		this(edgeLearner, false,entailings,nonentailings,unknownScore);
	}
	
	public UntypedPredicateGraphLearner(EdgeLearner edgeLearner, boolean convertProb2Score, Set<Pair<String,String>> entailings,Set<Pair<String,String>> nonentailings, double unknownScore) {
		m_edgeLearner = edgeLearner;
		m_convertProb2Score = convertProb2Score;
		m_entailings = entailings;
		m_nonentailings = nonentailings;
		m_unknownScore = unknownScore;
	}

	public Set<AbstractOntologyGraph> learn(DirectedOntologyGraph graph) throws Exception {

		Set<AbstractOntologyGraph> ret = new HashSet<AbstractOntologyGraph>(); 
		
		//find the components
		List<Set<String>> componentList = new UndirectedOntologyGraph(graph).findConnectivityComponents();
			
		System.out.println("Number of components with size larger than 1: " + componentList.size());
		System.gc();

		//generate the local model
		MultiComponentMapLocalScorer multiComponentScorer = new MultiComponentMapLocalScorer(componentList,graph.getEdges(),m_convertProb2Score, m_entailings, m_nonentailings,m_unknownScore);
		
		//learn the graph component by component
		int numOfEdges = 0;
		double objectiveValue = 0.0;
		for(Set<String> component: componentList) {

			MapLocalScorer currentScorer = multiComponentScorer.getScorerForPredicate(component.iterator().next());

			AbstractOntologyGraph graph1 = new DirectedOneMappingOntologyGraph(component);
			UntypedPredicateGraph componentGraph = new UntypedPredicateGraph(graph1, m_edgeLearner);
			m_edgeLearner.init(componentGraph,currentScorer);			
			logger.info("Number of nodes: " + componentGraph.getGraph().getNodeCount());
			
			componentGraph.learn();
			ret.add(componentGraph.getGraph());
			numOfEdges+=componentGraph.getGraph().getEdgeCount();
		}
		logger.info("Number of edges: " + numOfEdges);
		logger.info("Objective value: " + objectiveValue);
		
		return ret;
	}
	
	protected EdgeLearner m_edgeLearner;
	protected boolean m_convertProb2Score;
	protected Set<Pair<String,String>> m_entailings;
	protected Set<Pair<String,String>> m_nonentailings;
	protected double m_unknownScore;

}
