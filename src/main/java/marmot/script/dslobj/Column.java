package marmot.script.dslobj;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class Column {
	private String m_colName;
	
	public Column(String colName) {
		m_colName = colName;
	}
	
	public String getName() {
		return m_colName;
	}
	
	@Override
	public String toString() {
		return String.format("column('%s')", m_colName);
	}
}
