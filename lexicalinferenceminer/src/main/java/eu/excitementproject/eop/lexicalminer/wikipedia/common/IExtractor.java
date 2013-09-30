package eu.excitementproject.eop.lexicalminer.wikipedia.common;


import java.io.FileNotFoundException;

import java.sql.SQLException;
import java.util.*;

import javax.naming.ConfigurationException;

import de.tudarmstadt.ukp.wikipedia.api.*;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.utilities.configuration.InitException;
import eu.excitementproject.eop.lexicalminer.definition.Common.RelationType;
/*
 * Interface for each type of Inferences extraction
 * 
 */
public interface IExtractor {
	List<LexicalRule<RuleInfo>> ExtractDocument(Page page) throws FileNotFoundException,SQLException, InitException, ConfigurationException;

	RelationType getRelationType();
}
