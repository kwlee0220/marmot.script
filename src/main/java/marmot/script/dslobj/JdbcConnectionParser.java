package marmot.script.dslobj;

import java.util.Map;

import marmot.plan.JdbcConnectOptions;
import marmot.script.GroovyDslClass;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class JdbcConnectionParser extends GroovyDslClass {
	private JdbcConnectOptions m_options = new JdbcConnectOptions(null, null, null, null);
	
	public JdbcConnectionParser() {
	}
	
	public static JdbcConnectOptions parse(Map<String,Object> info) {
		return new JdbcConnectOptions(ScriptUtils.getOrThrow(info, "url").toString(),
									ScriptUtils.getOrThrow(info, "user").toString(),
									ScriptUtils.getOrThrow(info, "passwd").toString(),
									ScriptUtils.getOrThrow(info, "driverClass").toString());
	}
	
	public JdbcConnectionParser url(String url) {
		m_options = m_options.jdbcUrl(url);
		return this;
	}
	
	public JdbcConnectionParser user(String user) {
		m_options = m_options.user(user);
		return this;
	}
	
	public JdbcConnectionParser passwd(String passwd) {
		m_options = m_options.passwd(passwd);
		return this;
	}
	
	public JdbcConnectionParser driverClass(String clsName) {
		m_options = m_options.driverClassName(clsName);
		return this;
	}

	public JdbcConnectOptions parse() {
		return m_options;
	}
}
