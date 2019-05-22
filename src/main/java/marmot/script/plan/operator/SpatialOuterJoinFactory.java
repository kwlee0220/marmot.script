package marmot.script.plan.operator;

import marmot.optor.geo.SpatialRelation;
import marmot.plan.SpatialJoinOptions;
import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.SpatialOuterJoinProto;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class SpatialOuterJoinFactory extends GroovyOperatorFactory<SpatialOuterJoinFactory> {
	private static final String DEFAULT_GEOM_COLUMN = "the_geom";
	
	private final SpatialOuterJoinProto.Builder m_builder;
	private SpatialJoinOptions m_options = SpatialJoinOptions.create();
	
	public SpatialOuterJoinFactory(String paramDsId) {
		m_builder =  SpatialOuterJoinProto.newBuilder()
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
	
	public SpatialOuterJoinFactory geometry(String geomCol) {
		m_builder.setGeomColumn(geomCol);
		return this;
	}
	
	public SpatialOuterJoinFactory joinExpr(SpatialRelation expr) {
		m_options.joinExpr(expr);
		return this;
	}
	
	public SpatialOuterJoinFactory output(String outColsExpr) {
		m_options.outputColumns(outColsExpr);
		return this;
	}

	@Override
	public OperatorProto create() {
		SpatialOuterJoinProto proto = m_builder.setOptions(m_options.toProto())
												.build();
		return OperatorProto.newBuilder()
							.setSpatialOuterJoin(proto)
							.build();
	}
	
	public SpatialRelation withinDistance(Object obj) {
		double dist = ScriptUtils.parseDistance(obj);
		return SpatialRelation.WITHIN_DISTANCE(dist);
	}
}
