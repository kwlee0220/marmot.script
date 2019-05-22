package marmot.script.plan.operator;

import marmot.plan.GeomOpOptions;
import marmot.proto.optor.CentroidTransformProto;
import marmot.proto.optor.OperatorProto;
import marmot.script.GroovyDslClass;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class CentroidFactory extends GroovyDslClass implements OperatorFactory {
	private final CentroidTransformProto.Builder m_builder;
	private GeomOpOptions m_options = GeomOpOptions.create();
	
	public CentroidFactory(String geomCol) {
		m_builder = CentroidTransformProto.newBuilder()
										.setGeometryColumn(geomCol);
	}
	
	public CentroidFactory output(String outCol) {
		m_options.outputColumn(outCol);
		return this;
	}

	@Override
	public OperatorProto create() {
		m_builder.setOptions(m_options.toProto());
		CentroidTransformProto proto = m_builder.build();
		
		return OperatorProto.newBuilder().setCentroid(proto).build();
	}
}
