package marmot.script.dslobj;

import java.util.Map;

import com.google.common.collect.Maps;

import marmot.script.GroovyDslClass;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class OptionsParser extends GroovyDslClass {
	private final Map<String,Object> m_args;
	
	public OptionsParser(Map<String,Object> args) {
		m_args = Maps.newHashMap(args);
	}
	
	public Map<String,Object> getArguments() {
		return m_args;
	}
	
	@Override
	public Object getProperty(String name) {
		m_args.put(name, true);
		return true;
	}

	@Override
    public void setProperty(String name, Object value) {
		m_args.put(name, value);
    }
	
	@Override
	public Object invokeMethod(String name, Object args) {
		m_args.put(name, getArgs(args, 0));
		return args;
	}
}
