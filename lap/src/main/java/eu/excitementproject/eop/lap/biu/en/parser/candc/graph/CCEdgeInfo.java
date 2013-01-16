package eu.excitementproject.eop.lap.biu.en.parser.candc.graph;

public class CCEdgeInfo
{
	
	public CCEdgeInfo(String grType, String optionalSubtype,
			String optionalInitialGr) {
		super();
		this.grType = grType;
		this.optionalSubtype = optionalSubtype;
		this.optionalInitialGr = optionalInitialGr;
	}
	
	
	public String getGrType() {
		return grType;
	}
	public String getOptionalSubtype() {
		return optionalSubtype;
	}
	public String getOptionalInitialGr() {
		return optionalInitialGr;
	}
	
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((grType == null) ? 0 : grType.hashCode());
		result = prime
				* result
				+ ((optionalInitialGr == null) ? 0 : optionalInitialGr
						.hashCode());
		result = prime * result
				+ ((optionalSubtype == null) ? 0 : optionalSubtype.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CCEdgeInfo other = (CCEdgeInfo) obj;
		if (grType == null) {
			if (other.grType != null)
				return false;
		} else if (!grType.equals(other.grType))
			return false;
		if (optionalInitialGr == null) {
			if (other.optionalInitialGr != null)
				return false;
		} else if (!optionalInitialGr.equals(other.optionalInitialGr))
			return false;
		if (optionalSubtype == null) {
			if (other.optionalSubtype != null)
				return false;
		} else if (!optionalSubtype.equals(other.optionalSubtype))
			return false;
		return true;
	}




	private String grType;
	private String optionalSubtype;
	private String optionalInitialGr;

}
