package marmot.script;

import java.lang.reflect.Array;
import java.util.Arrays;

import groovy.lang.GroovyObjectSupport;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class GroovyDslClass extends GroovyObjectSupport {
	public String getArgsAsString(Object args, int index) {
		return (String)Array.get(args, index);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getArgs(Object args, int index) {
		return (T)Array.get(args, index);
	}
	
	@Override
	public Object getProperty(String name) {
		String msg = String.format("%s: missing property: name=%s",
									getClass().getSimpleName(), name);
		throw new IllegalArgumentException(msg);
	}

	@Override
    public void setProperty(String name, Object value) {
		String msg = String.format("%s: missing property: name=%s, value=%s",
									getClass().getSimpleName(), name, value);
		throw new IllegalArgumentException(msg);
    }
	
	@Override
	public Object invokeMethod(String name, Object args) {
		if ( args.getClass().isArray() ) {
			args = Arrays.toString((Object[])args);
		}
		String msg = String.format("%s: missing method: name=%s, args=%s",
									getClass().getSimpleName(), name, args);
		throw new IllegalArgumentException(msg);
	}
}
