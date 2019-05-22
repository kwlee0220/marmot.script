package marmot.script.plan.operator;

import groovy.lang.Closure;
import marmot.optor.JoinOptions;
import marmot.optor.JoinType;
import marmot.proto.optor.LoadHashJoinProto;
import marmot.proto.optor.OperatorProto;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class LoadHashJoinFactory extends GroovyOperatorFactory<LoadHashJoinFactory> {
	private final LoadHashJoinProto.Builder m_builder;
	private final JoinOptions m_options;
	
	public LoadHashJoinFactory(Closure decl) {
		m_builder = LoadHashJoinProto.newBuilder();
		m_options = JoinOptions.INNER_JOIN();
		
		ScriptUtils.callClosure(decl, this);
//		switch ( type.getLiteral() ) {
//			case "inner":
//				m_options = JoinOptions.INNER_JOIN();
//				break;
//			case "leftOuter":
//				m_options = JoinOptions.LEFT_OUTER_JOIN();
//				break;
//			case "rightOuter":
//				m_options = JoinOptions.RIGHT_OUTER_JOIN();
//				break;
//			case "fullOuter":
//				m_options = JoinOptions.FULL_OUTER_JOIN();
//				break;
//			default:
//				throw new IllegalArgumentException("unknown join type: " + type);
//		}
	}
	
	@Override
	public Object getProperty(String name) {
		switch ( name ) {
			case "inner":
			case "leftOuter":
			case "rightOuter":
			case "fullOuter":
			case "semi":
				return JoinType.INNER_JOIN;
		}
		
		return super.getProperty(name);
	}
	
	public LoadHashJoinFactory type(JoinType type) {
		m_options.joinType(type);
		
		return this;
	}
	
	public String dataset(String dsId) {
		return dsId;
	}
	
	public String columns(String cols) {
		return cols;
	}
	
	public LoadHashJoinFactory left(String dsId, String cols) {
		m_builder.setLeftDataset(dsId).setLeftJoinColumns(cols);
		return this;
	}

	public LoadHashJoinFactory right(String dsId, String cols) {
		m_builder.setRightDataset(dsId).setRightJoinColumns(cols);
		return this;
	}
	
	public LoadHashJoinFactory output(String outputCols) {
		m_builder.setOutputColumnsExpr(outputCols);
		
		return this;
	}
	
	public LoadHashJoinFactory workerCount(int count) {
		m_options.workerCount(count);
		return this;
	}

	@Override
	public OperatorProto create() {
		LoadHashJoinProto join = m_builder.setJoinOptions(m_options.toProto())
										.build();
		return OperatorProto.newBuilder().setLoadHashJoin(join).build();
	}
}
