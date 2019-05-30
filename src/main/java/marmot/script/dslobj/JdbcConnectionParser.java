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
	private JdbcConnectOptions m_options = JdbcConnectOptions.create();
	
	public JdbcConnectionParser() {
	}
	
	public static JdbcConnectOptions parse(Map<String,Object> info) {
		return JdbcConnectOptions.create()
							.jdbcUrl(ScriptUtils.getOrThrow(info, "url").toString())
							.user(ScriptUtils.getOrThrow(info, "user").toString())
							.passwd(ScriptUtils.getOrThrow(info, "passwd").toString())
							.driverClassName(ScriptUtils.getOrThrow(info, "driverClass").toString());
	}
	
	public JdbcConnectionParser url(String url) {
		m_options.jdbcUrl(url);
		return this;
	}
	
	public JdbcConnectionParser user(String user) {
		m_options.user(user);
		return this;
	}
	
	public JdbcConnectionParser passwd(String passwd) {
		m_options.passwd(passwd);
		return this;
	}
	
	public JdbcConnectionParser driverClass(String clsName) {
		m_options.driverClassName(clsName);
		return this;
	}

	public JdbcConnectOptions parse() {
		return m_options;
	}
}
