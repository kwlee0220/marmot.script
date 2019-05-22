package marmot.script.plan.operator;

import groovy.lang.Closure;
import marmot.script.GroovyDslClass;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
abstract class GroovyOperatorFactory<T extends GroovyOperatorFactory<T>>
										extends GroovyDslClass implements OperatorFactory {
	public T options(Closure script) {
		ScriptUtils.callClosure(script, this);
		return (T)this;
	}
}
