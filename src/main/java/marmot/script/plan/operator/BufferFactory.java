package marmot.script.plan.operator;

import marmot.plan.GeomOpOptions;
import marmot.proto.optor.BufferTransformProto;
import marmot.proto.optor.OperatorProto;
import marmot.script.GroovyDslClass;
import marmot.support.DataUtils;
import utils.UnitUtils;
import utils.func.FOption;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class BufferFactory extends GroovyDslClass implements OperatorFactory {
	private final String m_geomCol;
	private Double m_dist = null;
	private FOption<Integer> m_segCount = FOption.empty();
	private GeomOpOptions m_options = GeomOpOptions.create();
	
	public BufferFactory() {
		this("the_geom");
	}
	
	public BufferFactory(String geomCol) {
		m_geomCol = geomCol;
	}
	
	public BufferFactory by(Object dist) {
		m_dist = (dist instanceof String)
				? UnitUtils.parseLengthInMeter((String)dist)
				: DataUtils.asDouble(dist);
		return this;
	}
	
	public BufferFactory output(String outCol) {
		m_options.outputColumn(outCol);
		return this;
	}
	
	public BufferFactory segments(int count) {
		m_segCount = FOption.of(count);
		return this;
	}

	@Override
	public OperatorProto create() {
		if ( m_dist == null ) {
			throw new IllegalArgumentException("distance is not defined");
		}
		
		BufferTransformProto.Builder pbldr = BufferTransformProto.newBuilder()
																.setGeometryColumn(m_geomCol)
																.setDistance(m_dist)
																.setOptions(m_options.toProto());
		m_segCount.ifPresent(pbldr::setSegmentCount);
		BufferTransformProto buffer = pbldr.build();
		
		return OperatorProto.newBuilder().setBuffer(buffer).build();
	}
}
