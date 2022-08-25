package marmot.script;

import java.util.Map;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

import groovy.lang.Closure;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.RecordSchema;
import marmot.geo.GeoClientUtils;
import marmot.optor.JoinType;
import marmot.optor.StoreDataSetOptions;
import marmot.optor.geo.SpatialRelation;
import marmot.optor.geo.SquareGrid;
import marmot.plan.JdbcConnectOptions;
import marmot.script.dslobj.GDataSet;
import utils.Size2d;
import utils.func.FOption;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ScriptParsingObject extends GroovyDslClass {
	protected MarmotRuntime m_marmot;
	
	protected ScriptParsingObject(MarmotRuntime marmot) {
		m_marmot = marmot;
	}
	
	@Override
    public Object getProperty(String name) {
		switch ( name ) {
			case "INNER_JOIN":
				return JoinType.INNER_JOIN;
			case "LEFT_OUTER_JOIN":
				return JoinType.LEFT_OUTER_JOIN;
			case "RIGHT_OUTER_JOIN":
				return JoinType.RIGHT_OUTER_JOIN;
			case "FULL_OUTER_JOIN":
				return JoinType.FULL_OUTER_JOIN;
			case "SEMI_JOIN":
				return JoinType.SEMI_JOIN;
				
			case "intersects":
				return SpatialRelation.INTERSECTS;
		}
		
		return super.getProperty(name);
    }
	
	public GDataSet DataSet(String id) {
		return new GDataSet(m_marmot, id);
	}
	
	public RecordSchema RecordSchema(String decl) {
		return RecordSchema.parse(decl);
	}
	
	public double distance(Object distExpr) {
		return ScriptUtils.parseDistance(distExpr);
	}
	
	public Geometry Geometry(Object obj) {
		if ( obj instanceof Geometry ) {
			return (Geometry)obj;
		}
		else if ( obj instanceof Envelope ) {
			return GeoClientUtils.toPolygon((Envelope)obj);
		}
		else {
			throw new IllegalArgumentException("unsupported Geometry object: " + obj);
		}
	}
	
	public Plan plan(String name, Closure script) {
		return ScriptUtils.parsePlan(m_marmot, name, script);
	}
	
	public Envelope Envelope(Map<String,Double> coords) {
		return ScriptUtils.parseEnvelope(coords);
	}
	
	public Geometry WKT(String wktStr) throws ParseException {
		return GeoClientUtils.fromWKT(wktStr);
	}
	
	public SpatialRelation withinDistance(Object distExpr) {
		return SpatialRelation.WITHIN_DISTANCE(distance(distExpr));
	}
	
	public Size2d Size2d(String str) {
		return ScriptUtils.parseSize2d(str);
	}
	public Size2d Size2d(Object widthExpr, Object heightExpr) {
		double width = ScriptUtils.parseDistance(widthExpr);
		double height = ScriptUtils.parseDistance(heightExpr);
		return new Size2d(width, height);
	}
	
	public SquareGrid SquareGrid(Object bounds, Size2d cellSize) {
		if ( bounds instanceof GDataSet ) {
			return new SquareGrid(((GDataSet)bounds).getId(), cellSize);
		}
		else if ( bounds instanceof Envelope ) {
			return new SquareGrid((Envelope)bounds, cellSize);
		}
		else {
			throw new IllegalArgumentException("invalid square-grid bounds: " + bounds);
		}
	}
	
	public StoreDataSetOptions StoreDataSetOptions(Map<String,Object> args) {
		return ScriptUtils.parseStoreDataSetOptions(args);
	}
	
	public JdbcConnectOptions JDBC(Closure<?> decl) {
		return ScriptUtils.parseJdbcConnectOptions(decl);
	}

	protected <T> FOption<T> getOption(Map<String,Object> args, String name) {
		return FOption.ofNullable((T)args.get(name));
	}
	protected FOption<String> getStringOption(Map<String,Object> args, String key) {
		return FOption.ofNullable((String)args.get(key));
	}
	protected FOption<Integer> getIntOption(Map<String,Object> args, String name) {
		return FOption.ofNullable((Integer)args.get(name));
	}
	protected FOption<Long> getLongOption(Map<String,Object> args, String name) {
		return FOption.ofNullable((Long)args.get(name));
	}
	protected FOption<Boolean> getBooleanOption(Map<String,Object> args, String name) {
		return FOption.ofNullable((Boolean)args.get(name));
	}
}
