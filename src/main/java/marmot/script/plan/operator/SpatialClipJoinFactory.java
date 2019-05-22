package marmot.script.plan.operator;

import marmot.optor.geo.SpatialRelation;
import marmot.plan.SpatialJoinOptions;
import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.SpatialClipJoinProto;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class SpatialClipJoinFactory extends GroovyOperatorFactory<SpatialClipJoinFactory> {
	private static final String DEFAULT_GEOM_COLUMN = "the_geom";
	
	private final SpatialClipJoinProto.Builder m_builder;
	private SpatialJoinOptions m_options = SpatialJoinOptions.create();
	
	public SpatialClipJoinFactory(String paramDsId) {
		m_builder =  SpatialClipJoinProto.newBuilder()
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
	
	public SpatialClipJoinFactory geometry(String geomCol) {
		m_builder.setGeomColumn(geomCol);
		return this;
	}
	
	public SpatialClipJoinFactory joinExpr(SpatialRelation expr) {
		m_options.joinExpr(expr);
		return this;
	}

	@Override
	public OperatorProto create() {
		SpatialClipJoinProto proto = m_builder.setOptions(m_options.toProto())
												.build();
		return OperatorProto.newBuilder()
							.setSpatialClipJoin(proto)
							.build();
	}
	
	public SpatialRelation withinDistance(Object obj) {
		double dist = ScriptUtils.parseDistance(obj);
		return SpatialRelation.WITHIN_DISTANCE(dist);
	}
}
