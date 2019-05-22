package marmot.script.plan.operator;

import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.SampleProto;
import marmot.script.GroovyDslClass;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class SampleFactory extends GroovyDslClass implements OperatorFactory {
	private final double m_ratio;
	
	public SampleFactory(double ratio) {
		m_ratio = ratio;
	}

	@Override
	public OperatorProto create() {
		SampleProto assign = SampleProto.newBuilder()
										.setSampleRatio(m_ratio)
										.build();
		
		return OperatorProto.newBuilder().setSample(assign).build();
	}
}
