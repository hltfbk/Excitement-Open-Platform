package eu.excitementproject.eop.lexicalminer.definition.idm;

import java.io.FileNotFoundException;

import java.sql.SQLException;
import java.util.List;

import javax.naming.ConfigurationException;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.utilities.configuration.InitException;
import eu.excitementproject.eop.lexicalminer.definition.Common.RelationType;
/**
 * The Interface of the Inference from Definition Module (IDM) which extracts inference 
 * rules connecting the term to be defined, the <i>definiendum</i>, with terms
 * of the definition.  
 * This IDM is the interface of the mechanism which every idm should implement (such as SyntacticIDM or LexicalIDM)
 * 
 * 
 * @author Eyal Shnarch
 * @since 12/04/12
 *
 */
public interface IIDM {

	public List<LexicalRule<RuleInfo>> retrieveSentenceLexicalRules(String sentence, String title, int sourceId) throws FileNotFoundException, SQLException, InitException, ConfigurationException;
	
	public RelationType getRelationType();
}
