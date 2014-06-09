package eu.excitementproject.eop.lap.biu.uima;

public class TestDependencyInfo {

	public String relation;
	public int governorId;
	
	public TestDependencyInfo(String relation, int governorId) {
		this.relation = relation;
		this.governorId = governorId;
	}
	
	@Override
	public String toString() {
		return relation + "->" + governorId;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof TestDependencyInfo) {
			TestDependencyInfo other = (TestDependencyInfo) o;
			return relation.equals(other.relation) && governorId==other.governorId;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return 1000*governorId + relation.hashCode();
	}
}
