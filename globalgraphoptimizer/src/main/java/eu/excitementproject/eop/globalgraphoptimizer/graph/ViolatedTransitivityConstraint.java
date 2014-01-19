package eu.excitementproject.eop.globalgraphoptimizer.graph;

import eu.excitementproject.eop.globalgraphoptimizer.defs.EdgeType;

public class ViolatedTransitivityConstraint {

	public ViolatedTransitivityConstraint(int u, int v, int w, EdgeType uv, EdgeType vw) {
		this.m_u = u;
		this.m_v = v;
		this.m_w = w;
		this.m_uv = uv;
		this.m_vw = vw;
	}
	
	public int getU() {
		return m_u;
	}
	public int getV() {
		return m_v;
	}
	public int getW() {
		return m_w;
	}
	
	public EdgeType getUV() {
		return m_uv;
	}
	public EdgeType getVW() {
		return m_vw;
	}

	private int m_u;
	private int m_v;
	private int m_w;
	private EdgeType m_uv;
	private EdgeType m_vw;
}
