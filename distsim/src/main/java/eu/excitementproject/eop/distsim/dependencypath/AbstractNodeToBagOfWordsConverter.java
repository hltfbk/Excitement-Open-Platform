package eu.excitementproject.eop.distsim.dependencypath;

import java.util.Map;

import org.apache.commons.collections15.*;
import org.apache.commons.collections15.bag.*;
import org.apache.commons.collections15.map.*;

import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.representation.basic.InfoGetFields;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

/**
 * Converts AbstractNode's to the Bag of words in their subtree. Caches intermediate results..
 *
 * @author Erel Segal Halevi
 * @since 2013-02
 */
public class AbstractNodeToBagOfWordsConverter {
	
	public static int CACHE_SIZE=100;
	
	/**
	 * @return the bag of words (= unordered collection with counts) in the given parse tree.
	 */
	public static <T extends Info,S extends AbstractNode<T,S>> Bag<String> get(S parseTree) {
		return mapNodeToBagOfWords.get(parseTree);
	}

	/**
	 * Recursively add the words of the given parseTree to the given words bag. Subroutine of {@link #toBagOfWords(AbstractNode)}.
	 * @param parseTree [INPUT]
	 * @param words [OUTPUT]
	 *
	protected static <T extends Info,S extends AbstractNode<T,S>> void addWordsToBag(AbstractNode<T,S> parseTree, Bag<String> words) {
		if (parseTree.getChildren()==null) {
			words.add(InfoGetFields.getWord(parseTree.getInfo()));
		} else {
			for (S child: parseTree.getChildren()) {
				addWordsToBag(child, words);
			}
		}
	}*/
	
	@SuppressWarnings("rawtypes")
	protected static Map<AbstractNode<? extends Info,? extends AbstractNode>, Bag<String>> mapNodeToBagOfWords = LazyMap.decorate(
			new LRUMap<AbstractNode<? extends Info,? extends AbstractNode>, Bag<String>>(CACHE_SIZE),
			new Transformer<AbstractNode<? extends Info,? extends AbstractNode>, Bag<String>>() {
				@Override public Bag<String> transform(AbstractNode<? extends Info,? extends AbstractNode> parseTree) {
					Bag<String> words = new HashBag<String> ();
					if (parseTree.getChildren()==null) {
						words.add(InfoGetFields.getWord(parseTree.getInfo()));
					} else {
						for (AbstractNode child: parseTree.getChildren()) {
							words.addAll(mapNodeToBagOfWords.get(child));
						}
					}
					return words;
				}
			}
		);
}
