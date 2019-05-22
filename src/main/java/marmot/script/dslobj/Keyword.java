package marmot.script.dslobj;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class Keyword {
	private String m_literal;
	
	public Keyword(String literal) {
		m_literal = literal;
	}
	
	public String getLiteral() {
		return m_literal;
	}
	
	@Override
	public String toString() {
		return "keyword:" + m_literal;
	}
}
