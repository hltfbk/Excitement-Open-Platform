/**
 * 
 */
package eu.excitementproject.eop.core.component.lexicalknowledge.wikipedia;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import eu.excitementproject.eop.common.component.lexicalknowledge.LexicalResourceException;


/**
 * This enum is an ordered list of Types of extractions of rules from a Wikipedia dump. 
 * <p>
 * The ranks are based on tables in Eyal's paper:
 * Eyal Shnarch, Libby Barak, Ido Dagan. Extracting Lexical Reference Rules from Wikipedia. In Proceedings of ACL, 2009.
 * @author Amnon Lotan
 *
 * @since Dec 4, 2011
 */
public enum WikiExtractionType {
	
	REDIRECT(0.87, "Redirect", 1), 
//	BE_COMP(0.78, "BeComp", 0.9), 
//	BE_COMP_IDIRECT(0.78, "BeCompIndirect", 0.6), 
	BE_COMP(0.8, "BeComp", 0.9), 
	BE_COMP_IDIRECT(0.8, "BeCompIndirect", 0.6), 
	PARENTHESIS(0.71, "Parenthesis", 0.8), 
	LINK(0.7, "Link", 0.7), 
	ALL_NOUNS(0.49, "AllNouns", 0.5), 
	ALL_NOUNS_TOP(0.6, "AllNounsTop", 10), 
	ALL_NOUNS_MID(0.46, "AllNounsMid", 20), 
	ALL_NOUNS_BOT(0.41, "AllNounsBot", 30), 
	CLIQUE(0.49, "Clique", 0.99), 
	RPT(0.21, "RPT", 0.87), 
	CATEGORY(0.5,"Category",1),
	LEX_ALL_NOUNS(0.49, "LexicalAllNoun", 1),
	SYNT_ALL_NOUNS(0.49, "SyntacticAllNoun", 1),
	OTHER(-1, "", -1),
	;
	
	private static final String COMMA_REGEX = "\\s*,\\s*";
	
	private final String stringRepresentation;
	private final double rank;
	private final double code;		// a numeric code that represents this enum value in the wiki db
	private static final Map<String, WikiExtractionType> mapStringRepresentationToEnum = new LinkedHashMap<String, WikiExtractionType>();
	private static final Map<Double, WikiExtractionType> mapCodeToEnum = new LinkedHashMap<Double, WikiExtractionType>();
	static
	{
		for (WikiExtractionType  value: values())
		{
			mapStringRepresentationToEnum.put(value.stringRepresentation, value);
			mapCodeToEnum.put(value.code, value);
			//System.out.println("Mapped wiki extraction type: " + value.stringRepresentation);
		}
	}
	
	/**
	 * Ctor
	 */
	private WikiExtractionType(double rank, String stringRepresentation, double code) {
		this.rank = rank;
		this.stringRepresentation = stringRepresentation;
		this.code = code;
	}
	
	/**
	 * @return the precision
	 */
	public double getRank() {
		return rank;
	}
	
	@Override
	public String toString() {
		return stringRepresentation;
	}
	
	public final static WikiExtractionType WORST_EXTRACTION_TYPE = OTHER;
	public final static String SEPARATOR = "@";
	
	/**
	 * Parse the given String, supposedly read straight from the "rules_new" wiki DB table, into a set of {@link WikiExtractionType}s
	 * 
	 * @param extractionTypesStr
	 * @param rulePrecision
	 * @return
	 * @throws LexicalResourceException 
	 */
	public static Set<WikiExtractionType> parseExtractionTypeDBEntry(String extractionTypesStr, double rulePrecision) throws LexicalResourceException {
		Set<WikiExtractionType> toReturn = new LinkedHashSet<WikiExtractionType>();
		WikiExtractionType currType;
		String[] extractionTypeStrings = extractionTypesStr.split(SEPARATOR);
		for (int i = 0; i < extractionTypeStrings.length; i++) {
			currType = parseExtractionTypeStr(extractionTypeStrings[i]);
			//System.out.println(" -- extraction type: " + currType.stringRepresentation);
			
			//split All-Nouns into 3 parts
			if (currType == ALL_NOUNS) {
				if (rulePrecision > 0.66) {
					currType = ALL_NOUNS_TOP;
				} else if (rulePrecision > 0.33) {
					currType = ALL_NOUNS_MID;
				} else {
					currType = ALL_NOUNS_BOT;
				}
			}

			toReturn.add(currType);
		}
		
		//System.out.println("  number of extraction types:  " + toReturn.size());
		return toReturn;
	}
	
	 public static WikiExtractionType parseExtractionTypeStr(String extractionTypeString) throws LexicalResourceException	
	 {
		 double extractionTypeCode;
		 
		 //System.out.println("Extraction type string: >" + extractionTypeString + "<");
		 
		 if (extractionTypeString.matches(".*[a-z]+.*")) {
			 //System.out.println("\textraction type is string: " + extractionTypeString);			 
			 return mapStringRepresentationToEnum.get(extractionTypeString);
		 }
		 try{
			 extractionTypeCode = Double.parseDouble(extractionTypeString);
		 }catch (NumberFormatException e) {
			 throw new LexicalResourceException("This string is not a double: " + extractionTypeString, e);
		 }

		 if (mapCodeToEnum.containsKey(extractionTypeCode))
			 return mapCodeToEnum.get(extractionTypeCode);
		 else
			 return WORST_EXTRACTION_TYPE;

//		 WikiExtractionType type;
//		 // TODO ask eyal why we use the string here!
//		 if(extractionTypeCode == 1){
//			 type = REDIRECT;
//		 }else if(extractionTypeCode == 0.99 || extractionTypeString.equals("Clique")){
//			 type = CLIQUE;
//		 }else if(extractionTypeCode == 0.9 || extractionTypeString.equals("BeComp")){
//			 type = BE_COMP;
//		 }else if(extractionTypeCode == 0.6 || extractionTypeString.equals("BeCompIndirect")){
//			 type = BE_COMP_IDIRECT;
//		 }else if(extractionTypeCode == 0.87 || extractionTypeString.equals("RPT")){
//			 type = RPT;
//		 }else if(extractionTypeCode == 0.8 || extractionTypeString.equals("Parenthesis")){
//			 type = PARENTHESIS;
//		 }else if(extractionTypeCode == 0.7 || extractionTypeString.equals("Link")){
//			 type = LINK;
//		 }else if(extractionTypeCode == 0.5 || extractionTypeString.equals("AllNouns")){
//			 type = ALL_NOUNS;
//		 }else if(extractionTypeCode == 10 || extractionTypeString.equals("AllNounsTop")){
//			 type = ALL_NOUNS_TOP;
//		 }else if(extractionTypeCode == 20 || extractionTypeString.equals("AllNounsMid")){
//			 type = ALL_NOUNS_MID;
//		 }else if(extractionTypeCode == 30 || extractionTypeString.equals("AllNounsBot")){
//			 type = ALL_NOUNS_BOT;
//		 }else
//			 type = WORST_EXTRACTION_TYPE;
//		 return type;
	 }

	/**
	 * Read a comma separated list of extraction type names and return a set of enums
	 * @param extractionTypeListOfStrings
	 * @return
	 * @throws LexicalResourceException 
	 */
	public static Set<WikiExtractionType> parseExtractionTypeListOfStrings(	String extractionTypeListOfStrings) throws LexicalResourceException 
	{
		Set<WikiExtractionType> wikiExtractionTypes = new LinkedHashSet<WikiExtractionType>();
		String[] tokens = extractionTypeListOfStrings.split(COMMA_REGEX);
		for (String token : tokens)
			if (mapStringRepresentationToEnum.containsKey(token))
				wikiExtractionTypes.add(mapStringRepresentationToEnum.get(token));
			else
				throw new LexicalResourceException("This token \"" + token +"\" is one of the follwing valid string representations: " + mapStringRepresentationToEnum.keySet());
		
		return wikiExtractionTypes;
	}
}

