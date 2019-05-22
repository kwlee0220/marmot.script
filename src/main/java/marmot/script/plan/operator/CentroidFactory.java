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
	private final String m_geomCol;
	private GeomOpOptions m_options = GeomOpOptions.create();
	
	public CentroidFactory(String geomCol) {
		m_geomCol = geomCol;
	}

	@Override
	public OperatorProto create() {
		CentroidTransformProto centroid  = CentroidTransformProto.newBuilder()
																.setGeometryColumn(m_geomCol)
																.setOptions(m_options.toProto())
																.build();
		return OperatorProto.newBuilder().setCentroid(centroid).build();
	}
}
