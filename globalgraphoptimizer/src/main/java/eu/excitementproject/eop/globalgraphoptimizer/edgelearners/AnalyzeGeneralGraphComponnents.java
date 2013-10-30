package eu.excitementproject.eop.globalgraphoptimizer.edgelearners;

import java.io.File;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import eu.excitementproject.eop.globalgraphoptimizer.alg.StrongConnectivityComponents;
import eu.excitementproject.eop.globalgraphoptimizer.graph.DirectedOneMappingOntologyGraph;
import eu.excitementproject.eop.globalgraphoptimizer.score.BasicScoreModel;
import eu.excitementproject.eop.globalgraphoptimizer.score.ScoreModel;

public class AnalyzeGeneralGraphComponnents {
	public static void main(String[] args) {
		try {			
			ScoreModel localScoreModel = new BasicScoreModel(new File(args[0]),0,2.5);
			DirectedOneMappingOntologyGraph graph = ThresholdScoreBasedTreeLearner.createInitGraph(localScoreModel,2.5);
			StrongConnectivityComponents scc = new StrongConnectivityComponents();
			List<List<Integer>> components = scc.DFSandSCC(graph);
			System.out.println("Total number of nodes: " + graph.getNodeCount());
			System.out.println("Total number of edges: " + graph.getEdgeCount());
			System.out.println("Number of components: " + components.size());
			int sum = 0;
			Map<Integer,Integer> dist = new TreeMap<Integer,Integer>();
			for (List<Integer> l : components) {
				if (!l.contains(-1)) {
					sum += l.size();
					Integer count = dist.get(l.size());
					if (count == null)
						dist.put(l.size(),1);
					else
						dist.put(l.size(),count+1);
				}
			}
			if (sum != graph.getNodeCount()) 
				System.out.println("Different count of nodes: " + graph.getNodeCount() + " --- " + sum);
			
			System.out.println("Average number of nodes per scc: " + ((double)sum / (double)components.size()));
			System.out.println("Distribution: <number of components>  <count>");
			for (Entry<Integer,Integer> entry : dist.entrySet())
				System.out.println(entry.getKey() + ": "  + entry.getValue() + " (" + ((double)entry.getValue() / (double)sum) + ")");
									
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
