package marmot.script.plan.operator;

import marmot.plan.GeomOpOptions;
import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.TransformCrsProto;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class TransformCrsFactory extends GroovyOperatorFactory {
	private final String m_geomCol;
	private final TransformCrsProto.Builder m_builder;
	private GeomOpOptions m_options = GeomOpOptions.create();
	
	public TransformCrsFactory(String geomCol) {
		m_geomCol = geomCol;
		m_builder = TransformCrsProto.newBuilder()
									.setGeometryColumn(m_geomCol);
	}
	
	public TransformCrsFactory from(String srid) {
		m_builder.setSourceSrid(srid);
		return this;
	}
	
	public TransformCrsFactory to(String srid) {
		m_builder.setTargetSrid(srid);
		return this;
	}
	
	public TransformCrsFactory output(String outCol) {
		m_options.outputColumn(outCol);
		return this;
	}

	@Override
	public OperatorProto create() {
		TransformCrsProto trans = m_builder.setOptions(m_options.toProto()).build();
		
		return OperatorProto.newBuilder().setTransformCrs(trans).build();
	}
}
