
package eu.excitementproject.eop.core.component.lexicalknowledge.derivbase;

import java.util.HashSet;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;

/**
 * Contains additional information about rules generated with help of the
 * resource DErivBase.
 * More specifically, this class holds information about the whole derivational
 * family out of which one (or two) lemma(s) is (are) from.
 * 
 * @author zeller
 *
 */
public class DerivBaseInfo implements RuleInfo
{

	/**
	 * Auto-generated.
	 */
	private static final long serialVersionUID = 7592198298444758211L;
	
	/**
	 * Contains all lemma-POS pairs in the derivational cluster of lemmas of 
	 * the corresponding rule, in the following format: 
	 * [lemma1_pos1, lemma2_pos2, lemma3_pos3...]
	 */
	private Set<String> derivationalFamily = new HashSet<String>();
	

	
	public DerivBaseInfo(Set<String> family) {
		this.derivationalFamily = family;
	}


	public Set<String> getDerivationalFamily() {
		return derivationalFamily;
	}
	
	
}



