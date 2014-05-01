package eu.excitementproject.eop.core.component.lexicalknowledge.custom;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;
import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalRule;
import eu.excitementproject.eop.common.component.lexicalknowledge.RuleInfo;
import eu.excitementproject.eop.common.representation.partofspeech.BySimplerCanonicalPartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.PartOfSpeech;
import eu.excitementproject.eop.common.representation.partofspeech.SimplerCanonicalPosTag;
import eu.excitementproject.eop.common.representation.partofspeech.UnsupportedPosTagStringException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFile;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationFileDuplicateKeyException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;

/**
 * A lexical resource based on a two-column text-file. 
 * <br>Each line in the file corresponds to a single entailment rule and should follow this structure:<br>
 * left_lemma<-SEP1->left_pos<--Sep2-->right_lemma<-SEP1->right_pos
 * <br>were <-SEP1-> and <--SEP2--> are defined in the configuration.
 * <br>The resource name and relation name are also given in the configuration.
 * @author Eyal Shnarch
 *
 */
public class FileBasedLexicalResourceWithPos extends FileBasedLexicalResource {

	/**
	 * @param params configuration params for initialization. Should include:
	 * <li>table_file - path to the file that contains the rules, in table format. Can also be a URL.
	 * <li>POS_separator - pattern of lemma-part of speech separator, e.g. ":".
	 * <li>table_separator - pattern of column-separator, e.g. "->".
	 * <li>part_of_speech - canonical name of a default part-of-speech for this rule-base. 
	 * For possible values, see {@link SimplerCanonicalPosTag}. The default part-of-speech will be used in cases 
	 * where a part-of-speech was not provided for a certain lemma.    
	 * <li>relation_name - name of relation to put in rules (the same for all rules).
	 * <li>(NOTE: The params.getModuleName() is used as the resource_name).  
	 * @throws ConfigurationException 
	 * @throws IOException 
	 * @throws UnsupportedPosTagStringException 
	 */
	public FileBasedLexicalResourceWithPos(ConfigurationParams params) throws UnsupportedPosTagStringException, IOException, ConfigurationException{
		super(params);
		this.POS_SEP = params.get("POS_separator");
		this.defaultPos = getPartOfSpeech();
	}
	
	public List<LexicalRule<? extends RuleInfo>> getRulesForLeft(String leftLemma, PartOfSpeech leftPos) 
								throws LexicalResourceException {
		return getRulesForSide(leftLemma, leftPos, false);
	}
	
	public List<LexicalRule<? extends RuleInfo>> getRulesForRight(String rightLemma, PartOfSpeech rightPos) 
								throws LexicalResourceException {
		return getRulesForSide(rightLemma, rightPos, true);
	}
	
	
	public List<LexicalRule<? extends RuleInfo>> getRules(String leftLemma, PartOfSpeech leftPos, 
														  String rightLemma, PartOfSpeech rightPos) 
								throws LexicalResourceException {
		List<LexicalRule<? extends RuleInfo>> rules = 
				super.getRules(leftLemma+POS_SEP+leftPos, leftPos, rightLemma+POS_SEP+rightPos, rightPos);
		return arrangePosTagInRules(rules);
	}
	
	protected List<LexicalRule<? extends RuleInfo>> getRulesForSide(String lemma, PartOfSpeech pos, boolean isForRight) 
			throws LexicalResourceException{
		List<LexicalRule<? extends RuleInfo>> rules;
		if(pos == null){
			pos = defaultPos;
		}
		if(isForRight){
			rules = super.getRulesForRight(lemma+POS_SEP+pos, null);
			if(pos.equals(defaultPos)){
				if(rules.isEmpty()){	//rules may be a Collections.EmptyList which is immutable
					rules = new ArrayList<LexicalRule<? extends RuleInfo>>();
				}
				rules.addAll(super.getRulesForRight(lemma, null));
			}
			
		}else{
			rules = super.getRulesForLeft (lemma+POS_SEP+pos, null);
			if(pos.equals(defaultPos)){
				if(rules.isEmpty()){	//rules may be a Collections.EmptyList which is immutable
					rules = new ArrayList<LexicalRule<? extends RuleInfo>>();
					rules.addAll(super.getRulesForLeft (lemma, null));
				}
			}
		}
		return arrangePosTagInRules(rules);
	}
	
	private List<LexicalRule<? extends RuleInfo>> arrangePosTagInRules(List<LexicalRule<? extends RuleInfo>> orgRules) 
								throws LexicalResourceException{
		List<LexicalRule<? extends RuleInfo>> modifiedRules = new ArrayList<LexicalRule<? extends RuleInfo>>();
		for(LexicalRule<? extends RuleInfo> r : orgRules){
			String[] lhs = r.getLLemma().split(POS_SEP);
			String[] rhs = r.getRLemma().split(POS_SEP);
			PartOfSpeech lPos, rPos;
			try {
				lPos = lhs.length == 2 ? new BySimplerCanonicalPartOfSpeech(lhs[1]) : defaultPos;
				rPos = rhs.length == 2 ? new BySimplerCanonicalPartOfSpeech(rhs[1]) : defaultPos;
			} catch (UnsupportedPosTagStringException e) {
				throw new LexicalResourceException("nested exception while reading the POS tags of the rule"+r,e);
			}
			modifiedRules.add(
					new LexicalRule<RuleInfo>(lhs[0], lPos, rhs[0], rPos, 
					    					  r.getRelation(), r.getResourceName(), 
					    					  r.getInfo())
							  				  );
		}
		return modifiedRules;
	}
	
	protected final String POS_SEP;
	protected PartOfSpeech defaultPos;

	
	
	//-------------- main for testing ----------------------//
	
	public static void main(String[] args){
		try {
			ConfigurationFile conf = new ConfigurationFile(new File(args[0]));
			ConfigurationParams dictParams = conf.getModuleConfiguration("Bilingual-aligns");
			FileBasedLexicalResourceWithPos biLingDict = new FileBasedLexicalResourceWithPos(dictParams);
			
			String enTerm = "country";
			PartOfSpeech pos = new BySimplerCanonicalPartOfSpeech(SimplerCanonicalPosTag.NOUN);
			System.out.println("Rules for right for "+enTerm+":"+pos);
			List<LexicalRule<? extends RuleInfo>> rules = 
					biLingDict.getRulesForRight(enTerm, pos);
			for(LexicalRule<? extends RuleInfo> rule : rules){
				System.out.println(rule.getLLemma()+":"+rule.getLPos()+
						"--"+rule.getRelation()+"-->"+
						rule.getRLemma()+":"+rule.getRPos()+"\t"+rule.getInfo());
			}
			System.out.println();
			String fTerm = "comisión";
			PartOfSpeech fPos = null;//new UnspecifiedPartOfSpeech(SimplerCanonicalPosTag.NOUN);
			System.out.println("Rules for left for "+fTerm+":"+fPos);
			rules = biLingDict.getRulesForLeft(fTerm, fPos);
			for(LexicalRule<? extends RuleInfo> rule : rules){
				System.out.println(rule.getLLemma()+":"+rule.getLPos()+
						"--"+rule.getRelation()+"-->"+
						rule.getRLemma()+":"+rule.getRPos()+"\t"+rule.getInfo());
			}
			System.out.println("\nDone FileBasedLexicalResourceWithPos test");
			
			
		} catch (ConfigurationFileDuplicateKeyException e) {
			e.printStackTrace();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (UnsupportedPosTagStringException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LexicalResourceException e) {
			e.printStackTrace();
		}
		
	}
	
}

