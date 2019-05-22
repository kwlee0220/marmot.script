package marmot.script.plan.operator;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import marmot.geo.GeoClientUtils;
import marmot.plan.PredicateOptions;
import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.QueryDataSetProto;
import marmot.protobuf.PBUtils;
import marmot.script.GroovyDslClass;
import marmot.script.dslobj.DataSet;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class QueryDataSetFactory extends GroovyDslClass implements OperatorFactory {
	private QueryDataSetProto.Builder m_builder;
	private PredicateOptions m_options = PredicateOptions.create();
	
	public QueryDataSetFactory(String dsId) {
		m_builder = QueryDataSetProto.newBuilder().setDsId(dsId);
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
	
	public QueryDataSetFactory range(Envelope bounds) {
		Geometry key = GeoClientUtils.toPolygon(bounds);
		m_builder.setKeyGeometry(PBUtils.toProto(key));
		
		return this;
	}
	
	public QueryDataSetFactory range(Geometry key) {
		m_builder.setKeyGeometry(PBUtils.toProto(key));
		
		return this;
	}
	
	public QueryDataSetFactory range(DataSet keyDataset) {
		m_builder.setKeyDataset(keyDataset.getId());
		
		return this;
	}
	
	@Override
	public OperatorProto create() {
		QueryDataSetProto query = m_builder.setOptions(m_options.toProto()).build();
		
		return OperatorProto.newBuilder().setQueryDataset(query).build();
	}
}
