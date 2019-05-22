package marmot.script.plan.operator;

import marmot.optor.geo.SpatialRelation;
import marmot.plan.SpatialJoinOptions;
import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.SpatialBlockJoinProto;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class SpatialJoinFactory extends GroovyOperatorFactory<SpatialJoinFactory> {
	private static final String DEFAULT_GEOM_COLUMN = "the_geom";
	
	private final SpatialBlockJoinProto.Builder m_builder;
	private SpatialJoinOptions m_options = SpatialJoinOptions.create();
	
	public SpatialJoinFactory(String paramDsId) {
		m_builder =  SpatialBlockJoinProto.newBuilder()
										.setGeomColumn(DEFAULT_GEOM_COLUMN)
										.setParamDataset(paramDsId);
	}
	
	@Override
	public Object getProperty(String name) {
		switch ( name ) {
			case "intersects":
				return SpatialRelation.INTERSECTS;
		}
		
		return super.getProperty(name);
	}
	
	public SpatialJoinFactory geometry(String geomCol) {
		m_builder.setGeomColumn(geomCol);
		return this;
	}
	
	public SpatialJoinFactory joinExpr(SpatialRelation expr) {
		m_options.joinExpr(expr);
		return this;
	}
	
	public SpatialJoinFactory output(String outColsExpr) {
		m_options.outputColumns(outColsExpr);
		return this;
	}

	@Override
	public OperatorProto create() {
		SpatialBlockJoinProto proto = m_builder.setOptions(m_options.toProto())
												.build();
		return OperatorProto.newBuilder()
							.setSpatialBlockJoin(proto)
							.build();
	}
	
	public SpatialRelation withinDistance(Object obj) {
		double dist = ScriptUtils.parseDistance(obj);
		return SpatialRelation.WITHIN_DISTANCE(dist);
	}
}
