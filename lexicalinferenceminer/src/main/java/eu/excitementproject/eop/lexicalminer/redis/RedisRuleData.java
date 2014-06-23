package eu.excitementproject.eop.lexicalminer.redis;

import gnu.trove.list.array.TDoubleArrayList;

public final class RedisRuleData 
{
	
	public final static String DELIMITER = "|";
	
	private String leftTerm;
	private String rightTerm;
	private String POSPattern;	
	private String wordsPattern;	
	private String relationPattern;	
	private String POSrelationsPattern;
	private String fullPattern;
	private double defultRank;
	private TDoubleArrayList classifierRanks;
	private String ruleType;
	private boolean isBonus;

	public RedisRuleData(String term, String valueSerialization, boolean l2r) {
		this.leftTerm = term;
		fromValue(valueSerialization);
		if (!l2r) {
			this.leftTerm = this.rightTerm;
			this.rightTerm = term;
		}		
	}
	public RedisRuleData(String leftTerm, String rightTerm, String POSPattern, String wordsPattern,
			String relationPattern, String POSrelationsPattern, String fullPattern, double defultRank,
			TDoubleArrayList ruleClassifierRanks,String ruleType, boolean isBonus) {
		super();
		this.leftTerm = leftTerm;
		this.rightTerm = rightTerm;
		this.POSPattern = POSPattern;
		this.wordsPattern = wordsPattern;
		this.relationPattern = relationPattern;
		this.POSrelationsPattern = POSrelationsPattern;		
		this.fullPattern = fullPattern;
		this.defultRank = defultRank;
		this.classifierRanks = ruleClassifierRanks;
		this.ruleType = ruleType;
		this.isBonus = isBonus;
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

	public Double getClassifierRank(int classifierId) {
		
		if (classifierRanks == null)
			return null; 
		
		double d = classifierRanks.get(classifierId-1);
		if (d == -1)
			return null; 
		else
			return d;
	}

	public String getRuleType() {
		return ruleType;
	}

	public boolean getIsBonus() {
		return isBonus;
	}

	public String toValue() {
		StringBuilder value = new StringBuilder();
		value.append(rightTerm);
		value.append(DELIMITER);
		value.append(ruleType);
		value.append(DELIMITER);
		value.append(defultRank);
		value.append(DELIMITER);
		value.append(toClassifierRanks());
		value.append(DELIMITER);
		value.append(POSPattern == null ? "" : POSPattern);
		value.append(DELIMITER);
		value.append(wordsPattern == null ? "" : wordsPattern);
		value.append(DELIMITER);
		value.append(relationPattern == null ? "" : relationPattern);
		value.append(DELIMITER);
		value.append(POSrelationsPattern == null ? "" : POSrelationsPattern);
		value.append(DELIMITER);
		value.append(fullPattern == null ? "" : fullPattern);
		value.append(DELIMITER);		
		value.append(isBonus ? 1 : 0);
		return value.toString();
	}
	
	private String toClassifierRanks() {
		if (classifierRanks == null)
			return "";
		else {
			StringBuilder sb = new StringBuilder();
			for (int i=0; i< classifierRanks.size(); i++) {
				if (i>0)
					sb.append(",");
				sb.append(classifierRanks.get(i));
			}
			return sb.toString();
		}
	}
	public void fromValue(String value) {
		String[] toks = value.split("\\" + DELIMITER);
		this.rightTerm = toks[0];
		this.ruleType = toks[1];
		this.defultRank = Double.parseDouble(toks[2]);
		this.classifierRanks = fromClassifierRanks(toks[3]);
		this.POSPattern = (toks[4].equals("") ? null : toks[4]);
		this.wordsPattern = (toks[5].equals("") ? null : toks[5]);
		this.relationPattern = (toks[6].equals("") ? null : toks[6]);
		this.POSrelationsPattern = (toks[7].equals("") ? null : toks[7]);
		this.fullPattern = (toks[8].equals("") ? null : toks[8]);
		this.isBonus = (toks[9].equals("1") ? true : false);

	}
	
	public TDoubleArrayList fromClassifierRanks(String str) {
		if (str.isEmpty())
			return null;
		
		TDoubleArrayList ret = new TDoubleArrayList();
		for (String rank : str.split(",",-1)) {
			if (rank.isEmpty())
				ret.add(-1);
			else
				ret.add(Double.parseDouble(rank));
		}
				
		return ret;
	}
	@Override
	public String toString() {
		return "RuleData [leftTerm=" + leftTerm + ", rightTerm=" + rightTerm
				+ ", POSPattern=" + POSPattern + ", wordsPattern="
				+ wordsPattern + ", relationPattern=" + relationPattern
				+ ", POSrelationsPattern=" + POSrelationsPattern
				+ ", fullPattern=" + fullPattern + ", defultRank=" + defultRank
				+ ", classifierRank=" + classifierRanks
				+ ", isBonus=" + isBonus + "]";
	}
}