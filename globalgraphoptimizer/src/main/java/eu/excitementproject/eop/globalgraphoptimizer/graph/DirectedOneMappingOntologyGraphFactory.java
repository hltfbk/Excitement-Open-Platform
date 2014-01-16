package eu.excitementproject.eop.globalgraphoptimizer.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DirectedOneMappingOntologyGraphFactory {
	
	public static DirectedOneMappingOntologyGraph fromDirectedEdgeFile(File edgeFile) throws IOException {
		Map<String,Integer> mapNodeDescription2ID = new HashMap<String,Integer>();
		DirectedOneMappingOntologyGraph graph = new DirectedOneMappingOntologyGraph();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(edgeFile)));
		String line=null;
		while ((line=reader.readLine())!=null) {
			String[] nodes = line.split("\t");
			Integer id1 = getID(mapNodeDescription2ID, nodes[0]);
			Integer id2 = getID(mapNodeDescription2ID, nodes[1]);
			graph.addEdge(new RelationNode(id1,nodes[0]),new RelationNode(id2,nodes[1]));
		}	
		reader.close();
		return graph;
	}
	
	private static int getID(Map<String,Integer> map,String key) {
		Integer id = map.get(key);
		if (id == null) {
			id = map.size();
			map.put(key,id);
		}
		return id;
	}	

}
