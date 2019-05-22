package marmot.script.dslobj;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class DataSet {
	private String m_dsId;
	
	public DataSet(String dsId) {
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
