package marmot.script.plan.operator;

import marmot.optor.geo.SpatialRelation;
import marmot.plan.SpatialJoinOptions;
import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.SpatialSemiJoinProto;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class SpatialSemiJoinFactory extends GroovyOperatorFactory<SpatialSemiJoinFactory> {
	private static final String DEFAULT_GEOM_COLUMN = "the_geom";
	
	private final SpatialSemiJoinProto.Builder m_builder;
	private SpatialJoinOptions m_options = SpatialJoinOptions.create();
	
	public SpatialSemiJoinFactory(String paramDsId) {
		m_builder =  SpatialSemiJoinProto.newBuilder()
										.setGeomColumn(DEFAULT_GEOM_COLUMN)
										.setParamDataset(paramDsId);
	}
	
	@Override
	public Object getProperty(String name) {
		switch ( name ) {
			case "intersects":
				return SpatialRelation.INTERSECTS;
			case "negated":
				m_options.negated(true);
				return this;
		}
		
		return super.getProperty(name);
	}
	
	public SpatialSemiJoinFactory geometry(String geomCol) {
		m_builder.setGeomColumn(geomCol);
		return this;
	}
	
	public SpatialSemiJoinFactory joinExpr(SpatialRelation expr) {
		m_options.joinExpr(expr);
		return this;
	}
	
	public SpatialSemiJoinFactory output(String outColsExpr) {
		m_options.outputColumns(outColsExpr);
		return this;
	}
	
	public SpatialSemiJoinFactory negated(boolean flag) {
		m_options.negated(flag);
		return this;
	}

	@Override
	public OperatorProto create() {
		SpatialSemiJoinProto proto = m_builder.setOptions(m_options.toProto()).build();
		return OperatorProto.newBuilder()
							.setSpatialSemiJoin(proto)
							.build();
	}
	
	public SpatialRelation withinDistance(Object obj) {
		double dist = ScriptUtils.parseDistance(obj);
		return SpatialRelation.WITHIN_DISTANCE(dist);
	}
}
