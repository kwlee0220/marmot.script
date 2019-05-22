package marmot.script.plan.operator;

import groovy.lang.Closure;
import marmot.optor.JoinOptions;
import marmot.optor.JoinType;
import marmot.proto.optor.HashJoinProto;
import marmot.proto.optor.OperatorProto;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class HashJoinFactory extends GroovyOperatorFactory<HashJoinFactory> {
	private final HashJoinProto.Builder m_builder;
	private final JoinOptions m_options;
	
	public HashJoinFactory(String joinCols, String paramDsId, String paramJoinCols) {
		m_builder = HashJoinProto.newBuilder()
								.setJoinColumns(joinCols)
								.setParamDataset(paramDsId)
								.setParamColumns(paramJoinCols);
		m_options = JoinOptions.INNER_JOIN();
	}
	
	@Override
	public Object getProperty(String name) {
		switch ( name ) {
			case "inner":
				return JoinType.INNER_JOIN;
			case "leftOuter":
				return JoinType.LEFT_OUTER_JOIN;
			case "rightOuter":
				return JoinType.RIGHT_OUTER_JOIN;
			case "fullOuter":
				return JoinType.FULL_OUTER_JOIN;
			case "semi":
				return JoinType.INNER_JOIN;
		}
		
		return super.getProperty(name);
	}
	
	public HashJoinFactory type(JoinType type) {
		m_options.joinType(type);
		
		return this;
	}
	
	public HashJoinFactory output(String outCols) {
		m_builder.setOutputColumnsExpr(outCols);
		
		return this;
	}
	
	public HashJoinFactory workerCount(int count) {
		m_options.workerCount(count);
		return this;
	}
	
	public HashJoinFactory options(Closure optDecls) {
		ScriptUtils.callClosure(optDecls, this);
		return this;
	}

	@Override
	public OperatorProto create() {
		HashJoinProto join = m_builder.setJoinOptions(m_options.toProto())
										.build();
		return OperatorProto.newBuilder().setHashJoin(join).build();
	}
}
