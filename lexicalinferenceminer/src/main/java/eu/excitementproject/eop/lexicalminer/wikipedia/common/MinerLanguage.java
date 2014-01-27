package eu.excitementproject.eop.lexicalminer.wikipedia.common;

public enum MinerLanguage {
	ENGLISH,
	HEBREW,
	ITALIAN,
	GERMAN;
	
	
	public static  MinerLanguage parseLanguage(String language){
		if (language.equalsIgnoreCase(HEBREW.toString())){
			return HEBREW;
		}else if (language.equalsIgnoreCase(ITALIAN.toString())){
			return ITALIAN;
		}else if (language.equalsIgnoreCase(GERMAN.toString())){
			return GERMAN;
		}
		return ENGLISH;
	}
}
