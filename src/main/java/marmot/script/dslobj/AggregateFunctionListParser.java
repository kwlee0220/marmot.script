package marmot.script.dslobj;

import java.util.ArrayList;
import java.util.List;

import groovy.lang.Closure;
import marmot.optor.AggregateFunction;
import marmot.script.GroovyDslClass;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class AggregateFunctionListParser extends GroovyDslClass {
	private List<AggregateFunction> m_aggrList = new ArrayList<>();
	
	public AggregateFunctionListParser() {
	}
	
	public List<AggregateFunction> parse(Closure aggrsDecl) {
		ScriptUtils.callClosure(aggrsDecl, this);
		return m_aggrList;
	}
	
	@Override
	public void setProperty(String name, Object value) {
		if ( value instanceof AggregateFunction ) {
			AggregateFunction aggr = (AggregateFunction)value;
			m_aggrList.add(aggr.as(name));
			
			return;
		}
		
		super.setProperty(name, value);
	}
	
	public AggregateFunction COUNT() {
		return AggregateFunction.COUNT();
	}
	
	public AggregateFunction SUM(String colName) {
		return AggregateFunction.SUM(colName);
	}
	
	public AggregateFunction MAX(String colName) {
		return AggregateFunction.MAX(colName);
	}
	
	public AggregateFunction MIN(String colName) {
		return AggregateFunction.MIN(colName);
	}
	
	public AggregateFunction AVG(String colName) {
		return AggregateFunction.AVG(colName);
	}
	
	public AggregateFunction STDDEV(String colName) {
		return AggregateFunction.STDDEV(colName);
	}
	
	public AggregateFunction GEOM_UNION(String colName) {
		return AggregateFunction.GEOM_UNION(colName);
	}
	
	// aggregation function
	public AggregateFunction ENVELOPE(String colName) {
		return AggregateFunction.ENVELOPE(colName);
	}
	
	public AggregateFunction CONVEX_HULL(String colName) {
		return AggregateFunction.CONVEX_HULL(colName);
	}
	
	public AggregateFunction CONCAT_STR(String colName, String delim) {
		return AggregateFunction.CONCAT_STR(colName, delim);
	}
}
