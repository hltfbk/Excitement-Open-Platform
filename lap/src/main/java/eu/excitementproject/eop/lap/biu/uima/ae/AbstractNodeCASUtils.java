package eu.excitementproject.eop.lap.biu.uima.ae;

import java.util.ArrayList;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eu.excitementproject.eop.common.representation.parse.representation.basic.Info;
import eu.excitementproject.eop.common.representation.parse.tree.AbstractNode;

public class AbstractNodeCASUtils {

	/***
	 * Return the token relevant to given node, from given token list.
	 * @param tokenAnnotations a list of tokens, by their order in text 
	 * @param node a node from a parse tree
	 * @return
	 * @throws AbstractNodeCasException in case the text covered by the token doesn't match the text in the node
	 */
	public static Token nodeToToken(ArrayList<Token> tokenAnnotations, AbstractNode<Info,?> node) throws AbstractNodeCasException {
		
		int serial = node.getInfo().getNodeInfo().getSerial();
		Token token = tokenAnnotations.get(serial - 1);
		
		String tokenText = token.getCoveredText();
		String nodeText = node.getInfo().getNodeInfo().getWord();
		if (!tokenText.equals(nodeText)) {
			throw new AbstractNodeCasException(
					"For node (serial=" + serial + ") with text \"" + nodeText +
					"\" got token with text \"" + tokenText + "\"");
		}
		
		return token;
	}

}
