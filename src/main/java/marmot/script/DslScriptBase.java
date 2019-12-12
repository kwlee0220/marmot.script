package marmot.script;

import java.io.File;
import java.util.Map;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

import groovy.lang.Closure;
import groovy.lang.Script;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.RecordSchema;
import marmot.RecordScript;
import marmot.geo.GeoClientUtils;
import marmot.optor.AggregateFunction;
import marmot.optor.JoinType;
import marmot.optor.StoreDataSetOptions;
import marmot.optor.geo.SpatialRelation;
import marmot.optor.geo.SquareGrid;
import marmot.plan.JdbcConnectOptions;
import marmot.script.dslobj.GDataSet;
import marmot.script.dslobj.GProcess;
import utils.Size2d;
import utils.Utilities;
import utils.func.FOption;
import utils.func.Lazy;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public abstract class DslScriptBase extends Script {
	private final MarmotRuntime m_marmot;
	
	protected DslScriptBase() {
		m_marmot = Lazy.wrap(() -> (MarmotRuntime)getBinding().getProperty("MARMOT"), MarmotRuntime.class);
	}
	protected DslScriptBase(MarmotRuntime marmot) {
		m_marmot = marmot;
	}
	
	protected MarmotRuntime getMarmotRuntime() {
		return m_marmot;
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
	
	public GDataSet dataset(String id) {
		return new GDataSet(getMarmotRuntime(), id);
	}
	
	public RecordSchema RecordSchema(String decl) {
		return RecordSchema.parse(decl);
	}
	
	public RecordScript RecordScript(String init, String expr) {
		return RecordScript.of(init, expr);
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
	
	public GProcess process(String name, Closure script) {
		Utilities.checkNotNullArgument(script, "process script closure is null");
		
		GProcess gproc = new GProcess(name);
		ScriptUtils.callClosure(script, gproc);
		return gproc;
	}
	
	public File file(String path) {
		return new File(path);
	}
	
	public Plan plan(String name, Closure script) {
		return ScriptUtils.parsePlan(m_marmot, name, script);
	}
	
	public Envelope Envelope(Map<String,Double> coords) {
		return ScriptUtils.parseEnvelope(coords);
	}
	
	public Envelope Envelope(Object obj) {
		if ( obj instanceof Envelope ) {
			return (Envelope)obj;
		}
		else if ( obj instanceof Geometry ) {
			return ((Geometry)obj).getEnvelopeInternal();
		}
		else {
			throw new IllegalArgumentException("not Envelope object: " + obj);
		}
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
	
	public AggregateFunction count() {
		return AggregateFunction.COUNT();
	}
	
	public AggregateFunction SUM(String colName) {
		return AggregateFunction.SUM(colName);
	}
	
	public AggregateFunction MAX(String colName) {
		return AggregateFunction.MAX(colName);
	}
	
	public AggregateFunction MIN(String colName) {
		return AggregateFunction.MIN(colName);
	}
	
	public AggregateFunction AVG(String colName) {
		return AggregateFunction.AVG(colName);
	}
	
	public AggregateFunction STDDEV(String colName) {
		return AggregateFunction.STDDEV(colName);
	}
	
	public AggregateFunction UNION_GEOM(String colName) {
		return AggregateFunction.UNION_GEOM(colName);
	}
	
	// aggregation function
	public AggregateFunction ENVELOPE(String colName) {
		return AggregateFunction.ENVELOPE(colName);
	}
	
	public AggregateFunction CONVEX_HULL(String colName) {
		return AggregateFunction.CONVEX_HULL(colName);
	}
	
	public AggregateFunction CONCAT_STR(String colName, String delim) {
		return AggregateFunction.CONCAT_STR(colName, delim);
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
	protected FOption<Character> getCharOption(Map<String,Object> args, String key) {
		return FOption.ofNullable((String)args.get(key)).map(s -> s.charAt(0));
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
