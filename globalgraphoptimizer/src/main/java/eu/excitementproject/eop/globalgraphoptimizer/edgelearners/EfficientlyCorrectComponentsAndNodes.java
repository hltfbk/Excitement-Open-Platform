package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import org.apache.log4j.Logger;

import eu.excitementproject.eop.globalgraphoptimizer.defs.Constants;
import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.score.MapLocalScorer;

public class EfficientlyCorrectComponentsAndNodes implements EdgeLearner {

	private EfficientlyCorrectHtlTreeLearner m_nodeFix;
	private EfficientlyComponentCorrectHtlTreeLearner m_componentFix;
	private NodeGraph m_nodeGraph;
	private double m_edgeCost;

	private Logger logger = Logger.getLogger(EfficientlyCorrectComponentsAndNodes.class);
	public EfficientlyCorrectComponentsAndNodes(NodeGraph ioGraph,MapLocalScorer iLocalModel, double edgeCost) throws Exception {
		m_edgeCost = edgeCost;
		init(ioGraph,iLocalModel);
	}
	
	public EfficientlyCorrectComponentsAndNodes(double edgeCost) {
		m_nodeGraph = null;
		m_nodeFix = null;
		m_componentFix = null;
		m_edgeCost = edgeCost;
	}
	
	public void init(NodeGraph ioGraph,MapLocalScorer iLocalModel) throws Exception {
		m_nodeGraph = ioGraph;
		m_nodeFix = new EfficientlyCorrectHtlTreeLearner(ioGraph, iLocalModel, m_edgeCost);
		m_componentFix = new EfficientlyComponentCorrectHtlTreeLearner(ioGraph, iLocalModel, m_edgeCost);
	}

	@Override
	public void learn() throws Exception {

		m_componentFix.learn();

		boolean converge = false;
		double currentObjValue = getObjectiveFunctionValue();
		logger.warn("OBJECTIVE-FUNCTION-VALUE: " + currentObjValue);	
		while(!converge) {

			{
				m_nodeFix.learnAfterInit();

				double objectiveValue = getObjectiveFunctionValue();
				if(objectiveValue+0.00001<currentObjValue)
					throw new OntologyException("objective function value can not decrease. Current value: " + currentObjValue + " new value: " + objectiveValue);
				else if(objectiveValue-currentObjValue < Constants.CONVERGENCE)
					converge = true;
				currentObjValue = objectiveValue;
				logger.warn("OBJECTIVE-FUNCTION-VALUE-AFTER-NODE: " + currentObjValue);	
			}
			if(!converge) {

				m_componentFix.learnAfterInit();

				double objectiveValue = getObjectiveFunctionValue();
				if(objectiveValue+0.00001<currentObjValue)
					throw new OntologyException("objective function value can not decrease. Current value: " + currentObjValue + " new value: " + objectiveValue);
				else if(objectiveValue-currentObjValue < Constants.CONVERGENCE)
					converge = true;
				currentObjValue = objectiveValue;
				logger.warn("OBJECTIVE-FUNCTION-VALUE-AFTER-COMP: " + currentObjValue);
			}
		}
	}

	@Override
	public double getObjectiveFunctionValue() {
		// TODO Auto-generated method stub
		return m_nodeGraph.getGraph().sumOfEdgeWeights()-m_edgeCost*m_nodeGraph.getGraph().getEdgeCount();
	}

}
