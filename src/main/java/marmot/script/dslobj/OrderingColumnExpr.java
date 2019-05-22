package marmot.script.dslobj;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class OrderingColumnExpr {
	private String m_colsExpr;
	
	public OrderingColumnExpr(String colsExpr) {
		m_colsExpr = colsExpr;
	}
	
	public String getColumnsExpr() {
		return m_colsExpr;
	}
	
	@Override
	public String toString() {
		return String.format("orderBy('%s')", m_colsExpr);
	}
}
