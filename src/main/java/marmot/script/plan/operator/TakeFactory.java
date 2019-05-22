package marmot.script.plan.operator;

import marmot.proto.optor.GroupByKeyProto;
import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.ReducerProto;
import marmot.proto.optor.TakeProto;
import marmot.proto.optor.TakeReducerProto;
import marmot.proto.optor.TransformByGroupProto;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class TakeFactory extends GroovyOperatorFactory<TakeFactory> {
	private final long m_count;
	private GroupByKeyProto m_group;
	
	public TakeFactory(long count) {
		m_count = count;
	}
	
	public TakeFactory group(Object grouper) {
		m_group = ScriptUtils.parseGroup(grouper);
		return this;
	}

	@Override
	public OperatorProto create() {
		if ( m_group == null ) {
			TakeProto take = TakeProto.newBuilder()
												.setCount(m_count)
												.build();
			return OperatorProto.newBuilder().setTake(take).build();
		}
		else {
			TakeReducerProto take = TakeReducerProto.newBuilder().setCount(m_count).build();
			ReducerProto reducer = ReducerProto.newBuilder()
												.setTake(take)
												.build();
			TransformByGroupProto transform = TransformByGroupProto.newBuilder()
																.setGrouper(m_group)
																.setTransform(reducer)
																.build();
			return OperatorProto.newBuilder()
								.setTransformByGroup(transform)
								.build();
		}
	}
}
