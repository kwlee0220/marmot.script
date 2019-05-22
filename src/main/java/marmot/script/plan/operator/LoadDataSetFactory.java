package marmot.script.plan.operator;

import marmot.plan.LoadOptions;
import marmot.proto.optor.LoadDataSetProto;
import marmot.proto.optor.OperatorProto;
import marmot.script.GroovyDslClass;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class LoadDataSetFactory extends GroovyDslClass implements OperatorFactory {
	private LoadDataSetProto.Builder m_builder;
	private LoadOptions m_options = LoadOptions.create();
	
	public LoadDataSetFactory(String dsId) {
		m_builder = LoadDataSetProto.newBuilder().setDsId(dsId);
	}
	
	public LoadDataSetFactory splitCount(int count) {
		m_options.splitCount(count);
		return this;
	}

	@Override
	public OperatorProto create() {
		m_builder.setOptions(m_options.toProto());
		LoadDataSetProto load = m_builder.build();
		
		return OperatorProto.newBuilder().setLoadDataset(load).build();
	}
}
