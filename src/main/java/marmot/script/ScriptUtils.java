package marmot.script;

import java.util.Map;

import com.vividsolutions.jts.geom.Envelope;

import groovy.lang.Closure;
import marmot.ExecutePlanOptions;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.RecordScript;
import marmot.optor.CsvOptions;
import marmot.optor.ParseCsvOptions;
import marmot.optor.StoreDataSetOptions;
import marmot.optor.geo.SpatialRelation;
import marmot.plan.GeomOpOptions;
import marmot.plan.Group;
import marmot.plan.JdbcConnectOptions;
import marmot.plan.LoadOptions;
import marmot.plan.PredicateOptions;
import marmot.plan.SpatialJoinOptions;
import marmot.script.dslobj.JdbcConnectionParser;
import marmot.script.dslobj.StoreDataSetOptionsParser;
import marmot.script.plan.GPlanBuilder;
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
		Group group = Group.ofKeys(keys);
		
		ScriptUtils.getStringOption(args, "tags").ifPresent(group::tags);
		ScriptUtils.getStringOption(args, "orderBy").ifPresent(group::orderBy);
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
		return new JdbcConnectOptions(getOrThrow(args, "url").toString(),
										getOrThrow(args, "user").toString(),
										getOrThrow(args, "passwd").toString(),
										getOrThrow(args, "driverClass").toString());
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
		return ScriptUtils.getBooleanOption(args, "negated")
						.map(PredicateOptions::NEGATED)
						.getOrElse(PredicateOptions.DEFAULT);
	}
	
	public static LoadOptions parseLoadOptions(Map<String,Object> args) {
		LoadOptions opts = LoadOptions.DEFAULT;
		opts = getIntOption(args, "splitCount").transform(opts, LoadOptions::splitCount);
		
		return opts;
	}
	
	public static StoreDataSetOptions parseStoreDataSetOptions(Map<String,Object> args) {
		return StoreDataSetOptionsParser.parse(args);
	}
	
	public static ExecutePlanOptions parseExecutePlanOptions(Map<String,Object> args) {
		ExecutePlanOptions opts = ExecutePlanOptions.DEFAULT;
		opts = getBooleanOption(args, "disableLocalExec").transform(opts, ExecutePlanOptions::disableLocalExecution);
		opts = getStringOption(args, "mapOutputCompressionCodec").transform(opts, ExecutePlanOptions::mapOutputCompressionCodec);
		
		return opts;
	}
	
	public static CsvOptions parseCsvOptions(Map<String,Object> args) {
		CsvOptions opts = getCharOption(args, "delim").map(CsvOptions::DEFAULT)
							.getOrElse(CsvOptions::DEFAULT);
		opts = getCharOption(args, "quote").transform(opts, CsvOptions::quote);
		opts = getCharOption(args, "escape").transform(opts, CsvOptions::escape);
		opts = getStringOption(args, "charset").transform(opts, CsvOptions::charset);
		
		return opts;
	}
	
	public static ParseCsvOptions parseParseCsvOptions(Map<String,Object> args) {
		ParseCsvOptions opts = ParseCsvOptions.DEFAULT();
		
		opts = getCharOption(args, "delim").transform(opts, ParseCsvOptions::delimiter);
		opts = getCharOption(args, "quote").transform(opts, ParseCsvOptions::quote);
		opts = getCharOption(args, "escape").transform(opts, ParseCsvOptions::escape);
		opts = getStringOption(args, "charset").transform(opts, ParseCsvOptions::charset);
		opts = getStringOption(args, "header").transform(opts, ParseCsvOptions::header);
		opts = getBooleanOption(args, "headerFirst").transform(opts, ParseCsvOptions::headerFirst);
		opts = getBooleanOption(args, "trimColumns").transform(opts, ParseCsvOptions::trimColumns);
		opts = getStringOption(args, "nullValue").transform(opts, ParseCsvOptions::nullValue);
		opts = getBooleanOption(args, "throwParseError").transform(opts, ParseCsvOptions::throwParseError);
		
		return opts;
	}
	
	public static GeomOpOptions parseGeomOpOptions(Map<String,Object> args) {
		GeomOpOptions opts = GeomOpOptions.DEFAULT;
		
		opts = getStringOption(args, "output").transform(opts, GeomOpOptions::outputColumn);
		opts = getBooleanOption(args, "throwOpError").transform(opts, GeomOpOptions::throwOpError);
		
		return opts;
	}
	
	public static SpatialJoinOptions parseSpatialJoinOptions(Map<String,Object> args) {
		SpatialJoinOptions opts = SpatialJoinOptions.EMPTY;
		
		opts = getOption(args, "joinExpr")
				.transform(opts, (o,expr) -> {
					if ( expr instanceof SpatialRelation ) {
						return o.joinExpr((SpatialRelation)expr);
					}
					else if ( expr instanceof String ) {
						return o.joinExpr((String)expr);
					}
					else {
						throw new IllegalArgumentException("unexpected join expression: " + expr);
					}
				});
		opts = getBooleanOption(args, "negated").transform(opts, SpatialJoinOptions::negated);
		opts = getStringOption(args, "output").transform(opts, SpatialJoinOptions::outputColumns);
		
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
	public static FOption<Character> getCharOption(Map<String,Object> args, String name) {
		return FOption.ofNullable((String)args.get(name)).map(s -> s.charAt(0));
	}
	public static FOption<Boolean> getBooleanOption(Map<String,Object> args, String name) {
		return FOption.ofNullable((Boolean)args.get(name));
	}
}
