package eu.excitementproject.eop.lap.biu.en.parser.candc.graph;

public final class CCNode
{
	
	
	
	public CCNode(int serial, CCNodeInfo info)
	{
		super();
		this.serial = serial;
		this.info = info;
	}
	
	
	
	public int getSerial() {
		return serial;
	}
	public CCNodeInfo getInfo() {
		return info;
	}
	
	
	



	public int hashCode()
	{
		return super.hashCode();
		
	}
	
	public boolean equals(Object obj)
	{
		return super.equals(obj);
	}
	







	private int serial;
	private CCNodeInfo info;
	


}
