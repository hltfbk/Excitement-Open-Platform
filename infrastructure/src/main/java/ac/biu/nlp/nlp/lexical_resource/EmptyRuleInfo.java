/**
 * 
 */
package ac.biu.nlp.nlp.lexical_resource;



/**
 * Holds no info.
 * <p>
 * <b>Singleton</b> - cos any two {@link EmptyRuleInfo}s are equal.
 * @author Amnon Lotan
 * @since 17/05/2011
 * 
 */
public class EmptyRuleInfo implements RuleInfo {

	private static final long serialVersionUID = -4662379271257523258L;
	private static EmptyRuleInfo instance = new EmptyRuleInfo();
	
	/**
	 * Ctor
	 */
	private EmptyRuleInfo() {}
	
	public static EmptyRuleInfo getInstance()
	{
		return instance ;
	}
}
