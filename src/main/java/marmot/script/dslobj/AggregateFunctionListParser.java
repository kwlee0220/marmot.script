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
	
	public AggregateFunction count() {
		return AggregateFunction.COUNT();
	}
	
	public AggregateFunction sum(String colName) {
		return AggregateFunction.SUM(colName);
	}
	
	public AggregateFunction max(String colName) {
		return AggregateFunction.MAX(colName);
	}
	
	public AggregateFunction min(String colName) {
		return AggregateFunction.MIN(colName);
	}
	
	public AggregateFunction avg(String colName) {
		return AggregateFunction.AVG(colName);
	}
	
	public AggregateFunction stddev(String colName) {
		return AggregateFunction.STDDEV(colName);
	}
	
	public AggregateFunction geom_union(String colName) {
		return AggregateFunction.GEOM_UNION(colName);
	}
	
	// aggregation function
	public AggregateFunction envelope(String colName) {
		return AggregateFunction.ENVELOPE(colName);
	}
	
	public AggregateFunction convex_hull(String colName) {
		return AggregateFunction.CONVEX_HULL(colName);
	}
	
	public AggregateFunction concat_str(String colName, String delim) {
		return AggregateFunction.CONCAT_STR(colName, delim);
	}
}
