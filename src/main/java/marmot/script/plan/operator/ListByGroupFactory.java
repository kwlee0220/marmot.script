package marmot.script.plan.operator;

import marmot.proto.optor.GroupByKeyProto;
import marmot.proto.optor.ListReducerProto;
import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.ReducerProto;
import marmot.proto.optor.TransformByGroupProto;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ListByGroupFactory extends GroovyOperatorFactory<ListByGroupFactory> {
	private GroupByKeyProto m_group;
	
	public ListByGroupFactory() {
	}
	
	public ListByGroupFactory group(Object grouper) {
		m_group = ScriptUtils.parseGroup(grouper);
		return this;
	}

	@Override
	public OperatorProto create() {
		ListReducerProto list = ListReducerProto.newBuilder().build();
		ReducerProto reducer = ReducerProto.newBuilder()
											.setList(list)
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
