package marmot.script.dslobj;

import groovy.lang.Closure;
import marmot.plan.JdbcConnectOptions;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class JdbcTable {
	private String m_tblName;
	private JdbcConnectOptions m_options;
	
	public JdbcTable(String tblName, Closure decl) {
		m_tblName = tblName;
		m_options = ScriptUtils.parseJdbcConnectOptions(decl);
	}
	
	public String getName() {
		return m_tblName;
	}
	
	public JdbcConnectOptions getOptions() {
		return m_options;
	}
	
	@Override
	public String toString() {
		return String.format("jdbc_table(%s)", m_tblName);
	}
}
