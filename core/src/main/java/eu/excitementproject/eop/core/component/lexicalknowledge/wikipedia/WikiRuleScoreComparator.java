/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia;
import java.util.Comparator;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;


/**
 * Compare two wikipedia lexical rules by rank, and secondarily by coocurrence score.
 * @author Amnon Lotan
 *
 * @since 8 Jan 2012
 */
public class WikiRuleScoreComparator  implements Comparator<LexicalRule<? extends WikiRuleInfo>> {

	@Override
	public int compare(LexicalRule<? extends WikiRuleInfo> rule1, LexicalRule<? extends WikiRuleInfo> rule2) 
	{
		if (rule1 == null && rule2 == null)
			return 0;
		else if (rule1 == null)
			return -1;
		else if (rule2 == null)
			return 1;
		else {
			double diff = rule1.getInfo().getRank() - rule2.getInfo().getRank();
			if (diff < 0)
				return 1;
			else if (diff > 0)
				return -1;
			else {
				// diff == 0 i.e. have the same rank (extraction type), so
				// compare according to co-occurrence
				double coocDiff = rule1.getInfo().getCoocurenceScore() - rule2.getInfo().getCoocurenceScore();
				if (coocDiff < 0)
					return 1;
				else if (coocDiff > 0)
					return -1;
				else
					return 0;
			}
		}
	}
}

