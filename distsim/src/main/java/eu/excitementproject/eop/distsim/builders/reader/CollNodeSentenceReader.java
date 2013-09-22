package eu.excitementproject.eop.distsim.builders.reader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import eu.excitementproject.eop.common.representation.parse.AbstractNodeStringUtils;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultEdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultNodeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DefaultSyntacticInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.DependencyRelation;
import eu.excitementproject.eop.common.representation.parse.representation.basic.EdgeInfo;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NodeInfo;
import eu.excitementproject.eop.common.representation.parse.tree.dependency.basic.BasicNode;
import eu.excitementproject.eop.common.representation.partofspeech.GermanPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.distsim.util.Configuration;
import eu.excitementproject.eop.distsim.util.CreationException;
import eu.excitementproject.eop.distsim.util.Factory;
import eu.excitementproject.eop.distsim.util.Pair;

/**
 * An implementation of the {@link SentenceReader} interface, for a given stream of Conll representations of parsed sentences.
 * where the extracted sentences are converted to  {@link BasicNode}, 
 * 
 * @author Meni Adler
 * @since 08/01/2013
 *
 */

public class CollNodeSentenceReader extends ReaderBasedSentenceReader<BasicNode>{

	public CollNodeSentenceReader(PartOfSpeech pos) {
		super();
		this.pos = pos;
		this.position = 0;
	}

	public CollNodeSentenceReader(ConfigurationParams params) throws ConfigurationException, CreationException {
		super(params);
		this.pos = (PartOfSpeech)Factory.create(params.get(Configuration.PART_OF_SPEECH_CLASS),"other");
		this.position = 0;
	}

	/* (non-Javadoc)
	 * @see eu.excitementproject.eop.distsim.builders.cooccurrence.SentenceReader#nextSentence()
	 */
	@Override
	public Pair<BasicNode,Long> nextSentence() throws SentenceReaderException {
		try {
			Map<Integer,BasicNode> id2node = new HashMap<Integer,BasicNode>();
			Map<Integer,Integer> child2parent = new HashMap<Integer,Integer>();
			
			String line;
			int rootId = -1;
			
			String sProblem = null;
			
			while (true) {
				synchronized(this) {
					line=reader.readLine();
					if (line == null || line.isEmpty()) 
						break;
					else 
						position += line.getBytes(charset).length;
				}
				String[] toks = line.split("\t");
				int nodeId = Integer.parseInt(toks[0]);
				int parentId = Integer.parseInt(toks[6]);
				BasicNode node = getBasicNodeFromConll(toks);
				id2node.put(nodeId,node);
				child2parent.put(nodeId,parentId);
				if (parentId == 0) {
					if (rootId != -1)
						sProblem = "More than one root was identified!";
					else 
						rootId = nodeId;
				}
			}

			if (sProblem != null) 
				throw new SentenceReaderException(sProblem);

			if (id2node.isEmpty())
				return null;
			
			if (rootId == -1)
				throw new SentenceReaderException("Root node was not identified!");
			
			//debug
			/*for (Entry<Integer,BasicNode> entry : id2node.entrySet()) 
				System.out.println(entry.getKey() + ": " + entry.getValue());
			System.out.println("\n");
			for (Entry<Integer,Integer> entry : child2parent.entrySet()) 
				System.out.println(entry.getKey() + "->" + entry.getValue());
			*/
			
			for (Entry<Integer,Integer> entry : child2parent.entrySet()) {
				if (entry.getValue() != 0) { // not a root
					BasicNode child = id2node.get(entry.getKey());
					BasicNode parent = id2node.get(entry.getValue());
					parent.addChild(child);
				}
			}

			return new Pair<BasicNode,Long>(id2node.get(rootId),1L);
			
		} catch (Exception e) {
			throw new SentenceReaderException(e);
		}
	}
	
	protected BasicNode getBasicNodeFromConll(String[] toks) throws Exception {
		/*
		 * items[0]: ID
		 * items[1]: word form
		 * items[2]: lemma
		 * items[4]: POS
		 * items[6]: head ID
		 * items[7]: dependency label
		 */
		if (toks.length < 8)
			throw new Exception("Number of columns for each word should be at least 8: " + toks);
		
		String lemma = toks[2];
		if (lemma.equals(UNKNOWN))
			lemma = toks[1];
		
		NodeInfo nodeInfo = new DefaultNodeInfo(toks[1],lemma,Integer.parseInt(toks[0]),null, 
				new DefaultSyntacticInfo(pos.createNewPartOfSpeech(toks[4])));
		EdgeInfo edgeInfo = new DefaultEdgeInfo(new DependencyRelation(toks[7],null)); 
		Info info = new DefaultInfo(null,nodeInfo,edgeInfo);
		return new BasicNode(info);
	}
	
	
	public static void main(String[] args) throws Exception {
		CollNodeSentenceReader reader = new CollNodeSentenceReader(new GermanPartOfSpeech(""));
		reader.setSource(new File(args[0]));
		Pair<BasicNode,Long> pair = null;
		while ((pair=reader.nextSentence())!=null) {
			System.out.println(AbstractNodeStringUtils.toIndentedString(pair.getFirst()));
			System.out.println("\n\n\n");
		}
	}
	
	protected final PartOfSpeech pos;
	protected static final String UNKNOWN = "<unknown>";
}

