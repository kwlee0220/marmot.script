package marmot.script;

import java.util.Map;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

import groovy.lang.Closure;
import marmot.Plan;
import marmot.RecordSchema;
import marmot.RecordScript;
import marmot.geo.GeoClientUtils;
import marmot.plan.JdbcConnectOptions;
import marmot.proto.optor.GroupByKeyProto;
import marmot.script.dslobj.RecordSchemaParser;
import marmot.script.plan.GroupParser;
import marmot.script.plan.JdbcConnectionParser;
import marmot.script.plan.OperatorFactoryAssembler;
import marmot.script.plan.Size2dParser;
import marmot.support.DataUtils;
import utils.Size2d;
import utils.UnitUtils;
import utils.Utilities;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ScriptUtils {
	private ScriptUtils() {
		throw new AssertionError("Cannot create " + ScriptUtils.class);
	}
	
	public static double parseDistance(Object distExpr) {
		return (distExpr instanceof String)
					? UnitUtils.parseLengthInMeter((String)distExpr)
					: DataUtils.asDouble(distExpr);
	}
	
	public static Envelope parseEnvelope(Map<String,Double> coords) {
		double minX = DataUtils.asDouble(coords.get("minx"));
		double minY = DataUtils.asDouble(coords.get("miny"));
		double maxX = DataUtils.asDouble(coords.get("maxx"));
		double maxY = DataUtils.asDouble(coords.get("maxy"));
		
		return new Envelope(minX, maxX, minY, maxY);
	}
	
	public static Geometry parseWkt(String wktStr) throws ParseException {
		return GeoClientUtils.fromWKT(wktStr);
	}
	
	public static Object callClosure(Closure script, Object delegate) {
		Utilities.checkNotNullArgument(script, "script closure is null");
		Utilities.checkNotNullArgument(delegate, "script closure delegate is null");
		
		script.setDelegate(delegate);
		script.setResolveStrategy(Closure.DELEGATE_ONLY);
		return script.call();
	}
	
	public static Plan parsePlan(String planName, Closure script) {
		Utilities.checkNotNullArgument(script, "plan script closure is null");
		
		OperatorFactoryAssembler assembler = new OperatorFactoryAssembler(planName);
		callClosure(script, assembler);
		return assembler.assemble();
	}
	
	public static RecordSchema parseSchemaScript(Closure script) {
		RecordSchemaParser parser = new RecordSchemaParser();
		return (RecordSchema)callClosure(script, parser);
	}
	
	public static Size2d parseSize2d(Object obj) {
		if ( obj instanceof String ) {
			return Size2d.fromString((String)obj);
		}
		else if ( obj instanceof Closure ) {
			Size2dParser parser = new Size2dParser();
			callClosure(((Closure)obj), parser);
			return parser.parse();
		}
		else if ( obj instanceof Map ) {
			Map<String,Object> info = (Map<String,Object>)obj;
			double width = DataUtils.asDouble(info.get("width"));
			double height = DataUtils.asDouble(info.get("height"));
			
			return new Size2d(width, height);
		}
		else {
			throw new IllegalArgumentException("invalid Size2d: " + obj);
		}
	}
	
	public static GroupByKeyProto parseGroup(Object obj) {
		if ( obj instanceof Closure ) {
			GroupParser parser = new GroupParser();
			callClosure(((Closure)obj), parser);
			return parser.parse();
		}
		else if ( obj instanceof Map ) {
			Map<String,Object> info = (Map<String,Object>)obj;
			return GroupParser.parse(info);
		}
		else {
			throw new IllegalArgumentException("invalid Group: " + obj);
		}
	}
	
	public static long parseByteLength(Object obj) {
		if ( obj instanceof String ) {
			return UnitUtils.parseByteSize((String)obj);
		}
		else if ( obj instanceof Number ) {
			return DataUtils.asLong(obj);
		}
		else {
			throw new IllegalArgumentException("invalid byte-length: " + obj);
		}
	}
	
	public static JdbcConnectOptions parseJdbcConnectOptions(Object obj) {
		if ( obj instanceof Closure ) {
			JdbcConnectionParser parser = new JdbcConnectionParser();
			callClosure(((Closure)obj), parser);
			return parser.parse();
		}
		else if ( obj instanceof Map ) {
			Map<String,Object> info = (Map<String,Object>)obj;
			return JdbcConnectionParser.parse(info);
		}
		else {
			throw new IllegalArgumentException("invalid byte-length: " + obj);
		}
	}
	
	public static RecordScript parseRecordScript(Map<String,Object> args, String expr) {
		String initializer = ScriptUtils.getOrThrow(args, "initializer").toString();
		return RecordScript.of(initializer, expr);
	}
	
	public static Object getOrThrow(Map<String,Object> info, String name) {
		Object value = info.get(name);
		if ( value == null ) {
			throw new IllegalArgumentException("property is missing: " + name);
		}
		
		return value;
	}
}
