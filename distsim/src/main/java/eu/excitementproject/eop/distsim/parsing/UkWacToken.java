package eu.excitementproject.eop.distsim.parsing;

public class UkWacToken {
	private String word;
	private String lemma;
	private String POS;
	private Integer num;
	private Integer num_father;
	private String dep_relation;
	
	public UkWacToken(){
		this.lemma = "ROOT";
		this.POS = "ROOT";
		this.num = new Integer(0);
		this.num_father = new Integer(0);
		this.dep_relation = "ROOT";
	}
	
	public UkWacToken(String data){
		String[] d = data.split("\t");
		if (d.length < 5){
			System.out.println("problem:\t"+d.length+data);	
			this.word = "ROOT";
			this.lemma = "ROOT";
			this.POS = "ROOT";
			this.num = new Integer(0);
			this.num_father = new Integer(0);
			this.dep_relation = "ROOT";
		}
		else{
		this.word = d[0];
		this.lemma = d[1];
		this.POS = d[2];
		
		try{
			this.num =  Integer.parseInt(d[3]);
			this.num_father = Integer.parseInt(d[4]);
			this.dep_relation = d[5];		
		}
		catch (Exception e){
			System.out.println("error: "+data);
			e.printStackTrace();			
		}
		}
	}
	
	public UkWacToken(String data,String sep){
		String[] d = data.split(sep);
		if (d.length < 5){
			System.out.println("problem:\t"+data);	
			this.word = "ROOT";
			this.lemma = "ROOT";
			this.POS = "ROOT";
			this.num = new Integer(0);
			this.num_father = new Integer(0);
			this.dep_relation = "ROOT";
		}
		else{
			this.word = d[0];
			this.lemma = d[1];
			this.POS = d[2];
			
		try{
			this.num = Integer.parseInt(d[3]);
			this.num_father = Integer.parseInt(d[4]);
			this.dep_relation = d[5];		
		}
		catch (Exception e){
			System.out.println("error: "+data);
			e.printStackTrace();			
		}
		}
	}
	public String getWord(){return this.word;}
	public String getLemma(){return this.lemma;}
	public String getPOS(){return this.POS;}
	public Integer getNum(){return this.num;}
	public Integer getNumOfFather(){return this.num_father;}
	public void setLemma(String lemma){
		this.lemma = lemma;
	}
	public String getDependencyRelation(){return this.dep_relation;	}
	
	public boolean isMainVerb(){//if verb's father is root, this is the main verb
		return ((this.num_father.intValue() ==0) && (this.POS.startsWith("V")));
	}

	public boolean isProgressive() {
		if (this.POS.equalsIgnoreCase("VVG") ||this.POS.equalsIgnoreCase("VHG")||this.POS.equalsIgnoreCase("VBG") ){
			return true;
		}
		return false;
	}
	
	public int getDistance(UkWacToken other){
		int dist = this.num-other.getNum();
		dist = (dist>0? --dist : ++dist);
		return dist;
	}

	public boolean isNegation() {
		if ((this.lemma.equalsIgnoreCase("not")|| this.lemma.equalsIgnoreCase("n't")||this.lemma.equalsIgnoreCase("cannot") || this.lemma.equalsIgnoreCase("never") ||
				this.lemma.equalsIgnoreCase("nothing")||this.lemma.equalsIgnoreCase("neither") ||this.lemma.equalsIgnoreCase("none")) && this.POS.equalsIgnoreCase("RB") )			
			return true;
		else
			return false;
	}

	public boolean isVerb() {
		if (this.POS.startsWith("V")) return true;
		return false;
	}
	
	public boolean isAuxVerb(){
		boolean is_aux = false;
		if (this.POS.startsWith("V") && (this.lemma.equalsIgnoreCase("do") || this.lemma.equalsIgnoreCase("be") || this.lemma.equalsIgnoreCase("have"))){
			is_aux= true;
		}
		return is_aux;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((POS == null) ? 0 : POS.hashCode());
		result = prime * result
				+ ((dep_relation == null) ? 0 : dep_relation.hashCode());
		result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
		result = prime * result + ((num == null) ? 0 : num.hashCode());
		result = prime * result
				+ ((num_father == null) ? 0 : num_father.hashCode());
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
		UkWacToken other = (UkWacToken) obj;
		if (POS == null) {
			if (other.POS != null)
				return false;
		} else if (!POS.equals(other.POS))
			return false;
		if (dep_relation == null) {
			if (other.dep_relation != null)
				return false;
		} else if (!dep_relation.equals(other.dep_relation))
			return false;
		if (lemma == null) {
			if (other.lemma != null)
				return false;
		} else if (!lemma.equals(other.lemma))
			return false;
		if (num == null) {
			if (other.num != null)
				return false;
		} else if (!num.equals(other.num))
			return false;
		if (num_father == null) {
			if (other.num_father != null)
				return false;
		} else if (!num_father.equals(other.num_father))
			return false;
		return true;
	}

	public boolean isFather(UkWacToken other) {
		return this.num == other.getNumOfFather();

	}
	public boolean isStart(){
		return this.num ==1;
	}
	

	public boolean isBefore(UkWacToken curr_token) {
		return this.num < curr_token.getNum();
	}
	

}
