package eu.excitementproject.eop.distsim.builders.similarity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import eu.excitementproject.eop.distsim.util.Serialization;

/**
 * 
 * @author Meni Adler
 * @since 26/12/2012
 *
 */
public class File2IdLinkedIntDoubleMapBasedFile {
	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("Usage: org.excitement.distsim.builders.similarity.File2IdLinkedIntDoubleMapBasedFile <in file> <out file>");
		}
		
		try {			
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0]),"UTF-8"));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(args[1]),"UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] toks = line.split("\t");
				int id = Integer.parseInt(toks[0]);
				out.print(id);
				LinkedHashMap<Integer,Double> rightScores = Serialization.deserialize(toks[1]);
				for (Entry<Integer,Double> entry : rightScores.entrySet()) {					
					out.print("\t");
					out.print(entry.getKey());
					out.print("\t");
					out.print(entry.getValue());
				}
				out.print("\n");
			}
			reader.close();
			out.close();		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
