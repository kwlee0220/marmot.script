package marmot.script.plan.operator;

import java.util.Map;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import marmot.geo.GeoClientUtils;
import static marmot.optor.geo.SpatialRelation.*;
import marmot.plan.PredicateOptions;
import marmot.proto.optor.FilterSpatiallyProto;
import marmot.proto.optor.OperatorProto;
import marmot.protobuf.PBUtils;
import marmot.script.ScriptUtils;
import marmot.script.dslobj.DataSet;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class FilterSpatiallyFactory extends GroovyOperatorFactory<FilterSpatiallyFactory> {
	private FilterSpatiallyProto.Builder m_builder;
	private PredicateOptions m_options = PredicateOptions.create();
	
	public FilterSpatiallyFactory() {
		m_builder = FilterSpatiallyProto.newBuilder();
	}
	
	@Override
	public Object getProperty(String name) {
		switch ( name ) {
			case "negated":
				m_options.negated(true);
				return this;
		}
		
		return super.getProperty(name);
	}
	
	public FilterSpatiallyFactory on(String geomCol) {
		m_builder.setGeometryColumn(geomCol);
		
		return this;
	}
	
	public FilterSpatiallyFactory intersects(Envelope bounds) {
		Geometry key = GeoClientUtils.toPolygon(bounds);
		m_builder.setKeyGeometry(PBUtils.toProto(key))
				.setSpatialRelation(INTERSECTS.toStringExpr());
		
		return this;
	}
	
	public FilterSpatiallyFactory intersects(Geometry key) {
		m_builder.setKeyGeometry(PBUtils.toProto(key))
				.setSpatialRelation(INTERSECTS.toStringExpr());
		
		return this;
	}
	
	public FilterSpatiallyFactory intersects(DataSet keyDataset) {
		m_builder.setKeyDataset(keyDataset.getId())
				.setSpatialRelation(INTERSECTS.toStringExpr());
		
		return this;
	}
	
	public FilterSpatiallyFactory withinDistance(Object distObj) {
		double dist = ScriptUtils.parseDistance(distObj);
		m_builder.setSpatialRelation(WITHIN_DISTANCE(dist).toStringExpr());
		
		return this;
	}
	
	public FilterSpatiallyFactory envelope(Map<String,Double> coords) {
		Geometry key = GeoClientUtils.toPolygon(ScriptUtils.parseEnvelope(coords));
		m_builder.setKeyGeometry(PBUtils.toProto(key));
		
		return this;
	}
	
	@Override
	public OperatorProto create() {
		FilterSpatiallyProto filter = m_builder.setOptions(m_options.toProto())
												.build();
		
		return OperatorProto.newBuilder().setFilterSpatially(filter).build();
	}
}
