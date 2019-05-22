package marmot.script.plan.operator;

import java.util.ArrayList;
import java.util.List;

import groovy.lang.Closure;
import marmot.optor.AggregateFunction;
import marmot.proto.optor.GroupByKeyProto;
import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.ReducerProto;
import marmot.proto.optor.TransformByGroupProto;
import marmot.proto.optor.ValueAggregateReducersProto;
import marmot.script.GroovyDslClass;
import marmot.script.ScriptUtils;
import utils.stream.FStream;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class AggregateFactory extends GroovyDslClass implements OperatorFactory {
	private List<AggregateFunction> m_aggrList = new ArrayList<>();
	private GroupByKeyProto m_group;
	
	public AggregateFactory(Closure aggrsDecl) {
		ScriptUtils.callClosure(aggrsDecl, this);
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
	
	public AggregateFactory group(Object grpObj) {
		m_group = ScriptUtils.parseGroup(grpObj);
		return this;
	}

	@Override
	public OperatorProto create() {
		ValueAggregateReducersProto varp = FStream.from(m_aggrList)
												.map(AggregateFunction::toProto)
												.foldLeft(ValueAggregateReducersProto.newBuilder(),
															(b,f) -> b.addAggregate(f))
												.build();
		ReducerProto reducer = ReducerProto.newBuilder()
											.setValAggregates(varp)
											.build();
		if ( m_group == null ) {
			return OperatorProto.newBuilder().setReduce(reducer).build();
		}
		else {
			TransformByGroupProto transform = TransformByGroupProto.newBuilder()
																.setGrouper(m_group)
																.setTransform(reducer)
																.build();
			return OperatorProto.newBuilder()
								.setTransformByGroup(transform)
								.build();
		}
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
