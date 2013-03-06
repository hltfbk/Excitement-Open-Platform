package eu.excitementproject.eop.distsim.parsing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.distsim.util.Serialization;
import eu.excitementproject.eop.distsim.util.SerializationException;

public class DeserializeParsedCorpus {
	public static void main(String[] args) throws IOException, SerializationException {
		BufferedReader reader = new BufferedReader(new FileReader(args[0]));
		String line = null;
		while ((line = reader.readLine()) != null) {
			BasicNode tree = (BasicNode)Serialization.deserialize(line);
			System.out.println(tree);
		}
		reader.close();
	}
}
