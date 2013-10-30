package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.util.List;

import eu.excitementproject.eop.globalgraphoptimizer.graph.NodeGraph;
import eu.excitementproject.eop.globalgraphoptimizer.score.LocalScoreModel;


public interface InitialGraphConstructor {
	NodeGraph constructInitialGraph(List<String> vertices,  LocalScoreModel scoreModel);
}
