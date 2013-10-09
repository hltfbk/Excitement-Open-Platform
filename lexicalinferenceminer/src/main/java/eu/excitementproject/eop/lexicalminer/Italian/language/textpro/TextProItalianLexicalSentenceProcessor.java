package eu.excitementproject.eop.lexicalminer.Italian.language.textpro;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import eu.excitementproject.eop.common.datastructures.immutable.ImmutableList;
import eu.excitementproject.eop.common.datastructures.immutable.ImmutableListWrapper;
import eu.excitementproject.eop.common.representation.parse.representation.basic.NamedEntity;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationException;
import eu.excitementproject.eop.common.utilities.configuration.ConfigurationParams;
import eu.excitementproject.eop.lexicalminer.instrumentscombination.InstrumentCombinationException;
import eu.excitementproject.eop.lexicalminer.instrumentscombination.LexicalSentenceProcessor;
import eu.excitementproject.eop.lexicalminer.instrumentscombination.TokenInfo;

public class TextProItalianLexicalSentenceProcessor extends LexicalSentenceProcessor{

	private String textProPath;
	private String parser;
	private String parserPath;
	private String encoding;

	
	public TextProItalianLexicalSentenceProcessor(ConfigurationParams params) throws InstrumentCombinationException{
		super(null,null, null, null);
		try {
			this.textProPath = params.getString("textPro-path");
			this.parser = params. get("parser");
			this.parserPath = params.getString("parser-path");
			this.encoding = params.getString("encoding");
		} catch (ConfigurationException e) {
			throw new InstrumentCombinationException("Nested exception in reading the configuration file",e);
		}
	}
	
	@Override
	public List<TokenInfo> process(String sentence) {
//	    this.tpPath = tpPath;
		List<TokenInfo> processedSent = new Vector<TokenInfo>();
		
        String[] settings = {"token", "sentence", "pos", "lemma", "entity"};
		String language = "ita";
		
		//"/home/aprosio/textpro-sw/", "", "", "ISO-8859-1"
        TextPro tp = new TextPro(textProPath, parser, parserPath, encoding);
        tp.debug = false;
        
        ArrayList<HashMap<String, String>> tokens = tp.run(sentence, language, settings);
        
        for (HashMap<String, String> token: tokens) {
			TokenInfo tInfo = new TokenInfo(token.get("token"));
			
			if (token.get("entity").contains("PER")) {
			    tInfo.setNamedEntity(NamedEntity.PERSON);
		    }
			if (token.get("entity").contains("ORG")) {
			    tInfo.setNamedEntity(NamedEntity.ORGANIZATION);
		    }
			if (token.get("entity").contains("LOC")) {
			    tInfo.setNamedEntity(NamedEntity.LOCATION);
		    }
		    
			List<String> reallist = new Vector<String>();
			reallist.add(token.get("lemma"));
		    ImmutableList<String> lemmas = new ImmutableListWrapper<String>(reallist);
		    tInfo.setLemmas(lemmas);
		    
		    try {
		        TextProPartOfSpeech myPOS = new TextProPartOfSpeech(token.get("pos"));
//		        myPOS.setCanonicalPosTag(token.get("cpos"));
		        myPOS.setCanonicalPosTag();
		        tInfo.setPosTag(myPOS);
	        }
	        catch (Exception e) {
	            e.printStackTrace();
            }
			
			processedSent.add(tInfo);
			
//            System.out.println(tInfo);
//            System.out.format("%3s %20s %6s %5s %20s %3s %10s", token.get("tokenid"), token.get("token"), token.get("pos"), token.get("cpos"), token.get("lemma"), token.get("parseid"), token.get("entity"));
//            System.out.println("");
        }
		
//		System.out.println(sentence);
		
		return processedSent;
	}
	

}
