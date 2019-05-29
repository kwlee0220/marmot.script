package marmot.script;

import java.util.Map;

import com.vividsolutions.jts.geom.Envelope;

import groovy.lang.Closure;
import marmot.ExecutePlanOptions;
import marmot.GeometryColumnInfo;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.RecordScript;
import marmot.StoreDataSetOptions;
import marmot.optor.CsvOptions;
import marmot.optor.ParseCsvOptions;
import marmot.optor.geo.SpatialRelation;
import marmot.plan.GeomOpOptions;
import marmot.plan.Group;
import marmot.plan.JdbcConnectOptions;
import marmot.plan.LoadOptions;
import marmot.plan.PredicateOptions;
import marmot.plan.SpatialJoinOptions;
import marmot.script.plan.GPlanBuilder;
import marmot.script.plan.JdbcConnectionParser;
import marmot.support.DataUtils;
import utils.Size2d;
import utils.UnitUtils;
import utils.Utilities;
import utils.func.FOption;

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
	
	public static Object callClosure(Closure script, Object delegate) {
		Utilities.checkNotNullArgument(script, "script closure is null");
		Utilities.checkNotNullArgument(delegate, "script closure delegate is null");
		
		script.setDelegate(delegate);
		script.setResolveStrategy(Closure.DELEGATE_FIRST);
		return script.call();
	}
	
	public static Plan parsePlan(MarmotRuntime marmot, String planName, Closure<?> script) {
		Utilities.checkNotNullArgument(script, "plan script closure is null");
		
		GPlanBuilder pbldr = new GPlanBuilder(marmot, planName);
		callClosure(script, pbldr);
		return pbldr.build();
	}
	
	public static Size2d parseSize2d(Map<String,Object> args) {
		double width = parseDistance(getOrThrow(args, "width"));
		double height = parseDistance(getOrThrow(args, "height"));
		
		return new Size2d(width, height);
	}
	public static Size2d parseSize2d(String str) {
		return Size2d.fromString(str);
	}
	
	public static Group parseGroup(Map<String,Object> args, String keys) {
		Group group = Group.keyColumns(keys);
		
		ScriptUtils.getStringOption(args, "tags").ifPresent(group::tagColumns);
		ScriptUtils.getStringOption(args, "orderBy").ifPresent(group::orderByColumns);
		ScriptUtils.getIntOption(args, "workerCount").ifPresent(group::workerCount);
		
		return group;
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
	
	public static JdbcConnectOptions parseJdbcConnectOptions(Map<String,Object> args) {
		return JdbcConnectOptions.create()
								.jdbcUrl(ScriptUtils.getOrThrow(args, "url"))
								.user(ScriptUtils.getOrThrow(args, "user"))
								.passwd(ScriptUtils.getOrThrow(args, "passwd"))
								.driverClassName(ScriptUtils.getOrThrow(args, "driverClass"));
	}
	public static JdbcConnectOptions parseJdbcConnectOptions(Closure decl) {
		JdbcConnectionParser parser = new JdbcConnectionParser();
		callClosure(decl, parser);
		return parser.parse();
	}
	
	public static RecordScript parseRecordScript(Map<String,Object> args, String expr) {
		String initializer = ScriptUtils.getOrThrow(args, "initializer").toString();
		return RecordScript.of(initializer, expr);
	}
	
	public static PredicateOptions parsePredicateOptions(Map<String,Object> args) {
		PredicateOptions opts = PredicateOptions.create();
		ScriptUtils.getBooleanOption(args, "negated").ifPresent(opts::negated);
		
		return opts;
	}
	
	public static LoadOptions parseLoadOptions(Map<String,Object> args) {
		LoadOptions opts = LoadOptions.create();
		getIntOption(args, "splitCount").ifPresent(opts::splitCount);
		
		return opts;
	}
	
	public static StoreDataSetOptions parseStoreDataSetOptions(Map<String,Object> args) {
		StoreDataSetOptions opts = StoreDataSetOptions.create();
		
		FOption<Boolean> force = getOption(args, "force");
		force.ifPresent(opts::force);
		
		FOption<Object> gcInfo = getOption(args, "geometry");
		gcInfo.ifPresent(info -> {
			if ( info instanceof String ) {
				opts.geometryColumnInfo(GeometryColumnInfo.fromString((String)info));
			}
			else if ( info instanceof GeometryColumnInfo ) {
				opts.geometryColumnInfo((GeometryColumnInfo)info);
			}
			else {
				throw new IllegalArgumentException("incorrect GeometryColumnInfo: " + info);
			}
		});
		
		return opts;
	}
	
	public static ExecutePlanOptions parseExecutePlanOptions(Map<String,Object> args) {
		ExecutePlanOptions opts = ExecutePlanOptions.create();
		ScriptUtils.getBooleanOption(args, "disableLocalExec")
					.ifPresent(opts::disableLocalExecution);
		ScriptUtils.getStringOption(args, "mapOutputCompressionCodec")
					.ifPresent(opts::mapOutputCompressionCodec);
		
		return opts;
	}
	
	public static CsvOptions parseCsvOptions(Map<String,Object> args) {
		CsvOptions opts = CsvOptions.create();
		
		getStringOption(args, "delim").map(s -> s.charAt(0)).ifPresent(opts::delimiter);
		getStringOption(args, "quote").map(s -> s.charAt(0)).ifPresent(opts::quote);
		getStringOption(args, "escape").map(s -> s.charAt(0)).ifPresent(opts::escape);
		getStringOption(args, "charset").ifPresent(opts::charset);
		
		return opts;
	}
	
	public static ParseCsvOptions parseParseCsvOptions(Map<String,Object> args) {
		ParseCsvOptions opts = ParseCsvOptions.create();
		
		getStringOption(args, "delim").map(s -> s.charAt(0)).ifPresent(opts::delimiter);
		getStringOption(args, "quote").map(s -> s.charAt(0)).ifPresent(opts::quote);
		getStringOption(args, "escape").map(s -> s.charAt(0)).ifPresent(opts::escape);
		getStringOption(args, "charset").ifPresent(opts::charset);
		getStringOption(args, "header").ifPresent(opts::header);
		getBooleanOption(args, "headerFirst").ifPresent(opts::headerFirst);
		getBooleanOption(args, "trimColumns").ifPresent(opts::trimColumns);
		getStringOption(args, "nullValue").ifPresent(opts::nullValue);
		getIntOption(args, "maxColumnLength").ifPresent(opts::maxColumnLength);
		getBooleanOption(args, "throwParseError").ifPresent(opts::throwParseError);
		
		return opts;
	}
	
	public static GeomOpOptions parseGeomOpOptions(Map<String,Object> args) {
		GeomOpOptions opts = GeomOpOptions.create();
		
		getStringOption(args, "output").ifPresent(opts::outputColumn);
		getBooleanOption(args, "throwOpError").ifPresent(opts::throwOpError);
		
		return opts;
	}
	
	public static SpatialJoinOptions parseSpatialJoinOptions(Map<String,Object> args) {
		SpatialJoinOptions opts = SpatialJoinOptions.create();
				
		getOption(args, "joinExpr").cast(SpatialRelation.class).ifPresent(opts::joinExpr);
		getBooleanOption(args, "negated").ifPresent(opts::negated);
		getStringOption(args, "output").ifPresent(opts::outputColumns);
		
		return opts;
	}
	
	public static <T> T getOrThrow(Map<String,Object> args, String name) {
		Object value = args.get(name);
		if ( value == null ) {
			throw new IllegalArgumentException("property is missing: " + name);
		}
		
		return (T)value;
	}
	
	public static <T> FOption<T> getOption(Map<String,Object> args, String name) {
		return FOption.ofNullable((T)args.get(name));
	}
	public static FOption<Integer> getIntOption(Map<String,Object> args, String name) {
		return FOption.ofNullable((Integer)args.get(name));
	}
	public static FOption<Long> getLongOption(Map<String,Object> args, String name) {
		return FOption.ofNullable((Long)args.get(name));
	}
	public static FOption<String> getStringOption(Map<String,Object> args, String name) {
		return FOption.ofNullable((String)args.get(name));
	}
	public static FOption<Boolean> getBooleanOption(Map<String,Object> args, String name) {
		return FOption.ofNullable((Boolean)args.get(name));
	}
}
