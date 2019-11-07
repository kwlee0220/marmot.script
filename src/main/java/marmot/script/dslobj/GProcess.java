package marmot.script.dslobj;

import java.util.Map;

import com.google.common.collect.Maps;

import marmot.script.GroovyDslClass;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class GProcess extends GroovyDslClass {
	private final String m_procName;
	private final Map<String,String> m_params;
	
	public GProcess(String procName) {
		m_procName = procName;
		m_params = Maps.newHashMap();
	}
	
	public String getName() {
		return m_procName;
	}
	
	public Map<String,String> getParameters() {
		return m_params;
	}
	
	@Override
	public Object invokeMethod(String name, Object value) {
		if ( value.getClass().isArray() ) {
			value = ((Object[])value)[0];
		}
		m_params.put(name, value.toString());
		return null;
	}
}
