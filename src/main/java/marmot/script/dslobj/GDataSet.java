package marmot.script.dslobj;

import groovy.lang.GroovyObjectSupport;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class GDataSet extends GroovyObjectSupport {
	private final String m_dsId;
	
	public GDataSet(String dsId) {
		m_dsId = dsId;
	}
	
	public String getId() {
		return m_dsId;
	}
	
	@Override
	public String toString() {
		return String.format("dataset(%s)", m_dsId);
	}
}
