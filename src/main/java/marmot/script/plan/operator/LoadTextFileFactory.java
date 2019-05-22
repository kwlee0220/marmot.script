package marmot.script.plan.operator;

import com.google.common.collect.Lists;

import marmot.plan.LoadOptions;
import marmot.proto.optor.LoadTextFileProto;
import marmot.proto.optor.OperatorProto;
import marmot.script.GroovyDslClass;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class LoadTextFileFactory extends GroovyDslClass implements OperatorFactory {
	private LoadTextFileProto.Builder m_builder;
	private LoadOptions m_options = LoadOptions.create();
	
	public LoadTextFileFactory(String path) {
		m_builder = LoadTextFileProto.newBuilder().addAllPaths(Lists.newArrayList(path));
	}
	
	public LoadTextFileFactory splitCount(int count) {
		m_options.splitCount(count);
		return this;
	}

	@Override
	public OperatorProto create() {
		LoadTextFileProto load = m_builder.setOptions(m_options.toProto()).build();
		
		return OperatorProto.newBuilder().setLoadTextfile(load).build();
	}
}
