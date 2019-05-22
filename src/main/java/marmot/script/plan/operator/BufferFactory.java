package marmot.script.plan.operator;

import marmot.plan.GeomOpOptions;
import marmot.proto.optor.BufferTransformProto;
import marmot.proto.optor.OperatorProto;
import marmot.script.GroovyDslClass;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class BufferFactory extends GroovyDslClass implements OperatorFactory {
	private final BufferTransformProto.Builder m_builder;
	private final GeomOpOptions m_options = GeomOpOptions.create();
	
	public BufferFactory(String geomCol) {
		m_builder = BufferTransformProto.newBuilder()
										.setGeometryColumn(geomCol);
	}
	
	public BufferFactory distance(Object distExpr) {
		double dist = ScriptUtils.parseDistance(distExpr);
		m_builder.setDistance(dist);
		return this;
	}
	
	public BufferFactory output(String outCol) {
		m_options.outputColumn(outCol);
		return this;
	}
	
	public BufferFactory segmentCount(int count) {
		m_builder.setSegmentCount(count);
		return this;
	}

	@Override
	public OperatorProto create() {
		m_builder.setOptions(m_options.toProto());
		BufferTransformProto buffer = m_builder.build();
		
		return OperatorProto.newBuilder().setBuffer(buffer).build();
	}
}
