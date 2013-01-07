/**
 * 
 */
package ac.biu.nlp.nlp.engineml.utilities;

import ac.biu.nlp.nlp.instruments.parse.representation.basic.DefaultNodeInfo;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.Info;
import ac.biu.nlp.nlp.instruments.parse.representation.basic.NodeInfo;
import ac.biu.nlp.nlp.representation.WildcardPartOfSpeech;

/**
 * If rhsInfo has a POS (not {@link WildcardPartOfSpeech}), copy it to treeInfo. Return treeInfo
 * @author Amnon Lotan
 *
 * @since 25 Mar 2012
 */
public class InfoServicesUtils {

	/**
	 * If rhsInfo has a POS (not {@link WildcardPartOfSpeech}), copy it to treeInfo. Return treeInfo
	 * @param treeInfo
	 * @param rhsInfo
	 * @return
	 */
	public static NodeInfo combineNodeInfo(Info treeInfo, Info rhsInfo) {
		NodeInfo treeNodeInfo = treeInfo.getNodeInfo();
		if (rhsInfo != null && rhsInfo.getNodeInfo() != null && rhsInfo.getNodeInfo().getSyntacticInfo() != null && 
			rhsInfo.getNodeInfo().getSyntacticInfo().getPartOfSpeech() != null && 
			!WildcardPartOfSpeech.isWildCardPOS(rhsInfo.getNodeInfo().getSyntacticInfo().getPartOfSpeech()))
		{
			// treeInfo with rhsInfo's syntactic info
			return new DefaultNodeInfo(treeNodeInfo.getWord(), treeNodeInfo.getWordLemma(), treeNodeInfo.getSerial(), 
					treeNodeInfo.getNamedEntityAnnotation(), rhsInfo.getNodeInfo().getSyntacticInfo());
		}
		else
			return treeNodeInfo;
	}
}