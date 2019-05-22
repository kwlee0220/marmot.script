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
	
	public <T> T getArgs(Object args, int index) {
		return (T)Array.get(args, index);
	}
	
	@Override
	public Object getProperty(String name) {
		System.out.printf("!!!!!!! %s: missing property: name=%s%n",
							getClass().getSimpleName(), name);
		System.out.flush();
		throw new IllegalArgumentException();
	}

	@Override
    public void setProperty(String name, Object newValue) {
		System.out.printf("!!!!!!! %s: unknown property: name=%s, value=%s%n",
						getClass().getSimpleName(), name, newValue);
		System.out.flush();
		throw new IllegalArgumentException();
    }
	
	@Override
	public Object invokeMethod(String name, Object args) {
		if ( args.getClass().isArray() ) {
			args = Arrays.toString((Object[])args);
		}
		
		System.out.printf("!!!!!!! %s: missing method: name=%s, args=%s%n",
							getClass().getSimpleName(), name, args);
		System.out.flush();
		throw new IllegalArgumentException();
	}
}
