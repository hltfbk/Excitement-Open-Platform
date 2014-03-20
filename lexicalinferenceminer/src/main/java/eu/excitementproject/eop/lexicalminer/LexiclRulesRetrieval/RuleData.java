package eu.excitementproject.eop.lexicalminer.LexiclRulesRetrieval;

public final class RuleData 
{
	private String leftTerm;
	private String rightTerm;
	private String POSPattern;	
	private String wordsPattern;	
	private String relationPattern;	
	private String POSrelationsPattern;
	private String fullPattern;
	private double defultRank;
	private Double classifierRank;		//it's Double, so it can also be null
	private int ruleID;
	private String ruleResource;
	private String ruleType;
	private String ruleMetadata;
	private int ruleSourceId;

	public RuleData(String leftTerm, String rightTerm, String POSPattern, String wordsPattern,
			String relationPattern, String POSrelationsPattern, String fullPattern, double defultRank,
			Double classifierRank, int ruleID, String ruleResource,
			String ruleType, String ruleMetadata, int ruleSourceId) {
		super();
		this.leftTerm = leftTerm;
		this.rightTerm = rightTerm;
		this.POSPattern = POSPattern;
		this.wordsPattern = wordsPattern;
		this.relationPattern = relationPattern;
		this.POSrelationsPattern = POSrelationsPattern;		
		this.fullPattern = fullPattern;
		this.defultRank = defultRank;
		this.classifierRank = classifierRank;
		this.ruleID = ruleID;
		this.ruleResource = ruleResource;
		this.ruleType = ruleType;
		this.ruleMetadata = ruleMetadata;
		this.ruleSourceId = ruleSourceId;
	}

	public String getLeftTerm() {
		return leftTerm;
	}

	public String getRightTerm() {
		return rightTerm;
	}

	public String getPOSPattern() {
		return POSPattern;
	}

	public String getWordsPattern() {
		return wordsPattern;
	}

	public String getRelationPattern() {
		return relationPattern;
	}

	public String getPOSrelationsPattern() {
		return POSrelationsPattern;
	}


	public String getFullPattern() {
		return fullPattern;
	}

	public double getDefultRank() {
		return defultRank;
	}

	public Double getClassifierRank() {
		return classifierRank;
	}

	public int getRuleID() {
		return ruleID;
	}

	public String getRuleResource() {
		return ruleResource;
	}

	public String getRuleType() {
		return ruleType;
	}

	public String getRuleMetadata() {
		return ruleMetadata;
	}

	public int getRuleSourceId() {
		return ruleSourceId;
	}

	@Override
	public String toString() {
		return "RuleData [leftTerm=" + leftTerm + ", rightTerm=" + rightTerm
				+ ", POSPattern=" + POSPattern + ", wordsPattern="
				+ wordsPattern + ", relationPattern=" + relationPattern
				+ ", fullPattern=" + fullPattern + ", defultRank=" + defultRank
				+ ", classifierRank=" + classifierRank + ", ruleID=" + ruleID
				+ ", ruleResource=" + ruleResource + ", ruleType=" + ruleType
				+ ", ruleMetadata=" + ruleMetadata + ", ruleSourceId="
				+ ruleSourceId + "]";
	}





}