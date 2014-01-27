package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.util.Set;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Pair;
import eu.excitementproject.eop.globalgraphoptimizer.graph.RelationNode;


public class ImplicitSetProb {

	public ImplicitSetProb(Set<Pair<RelationNode, RelationNode>> implicitSet,
			double prob,Set<RelationNode> reaching, Set<RelationNode> reachable) {
		m_implicitSet = implicitSet;
		m_prob = prob;
		m_reaching=reaching;
		m_reachable=reachable;
	}


	public Set<Pair<RelationNode, RelationNode>> getImplicitSet() {
		return m_implicitSet;
	}

	public double getProb() {
		return m_prob;
	}

	public Set<RelationNode> getReachableNodes() {
		return m_reachable;
	}

	public Set<RelationNode> getReachingNodes() {
		return m_reaching;
	}

	private Set<Pair<RelationNode,RelationNode>> m_implicitSet;
	private double m_prob;
	private Set<RelationNode> m_reaching;
	private Set<RelationNode> m_reachable;
}
