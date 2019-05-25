package marmot.script.plan;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

import groovy.lang.Closure;
import marmot.PlanBuilder;
import marmot.RecordSchema;
import marmot.RecordScript;
import marmot.StoreDataSetOptions;
import marmot.geo.GeoClientUtils;
import marmot.optor.AggregateFunction;
import marmot.optor.JoinOptions;
import marmot.optor.JoinType;
import marmot.optor.StoreAsCsvOptions;
import marmot.optor.geo.SpatialRelation;
import marmot.optor.geo.SquareGrid;
import marmot.plan.GeomOpOptions;
import marmot.plan.Group;
import marmot.plan.JdbcConnectOptions;
import marmot.plan.LoadJdbcTableOptions;
import marmot.plan.LoadOptions;
import marmot.plan.PredicateOptions;
import marmot.plan.SpatialJoinOptions;
import marmot.script.ScriptUtils;
import marmot.script.dslobj.AggregateFunctionListParser;
import marmot.script.dslobj.GDataSet;
import marmot.script.dslobj.OptionsParser;
import utils.Size2d;
import utils.func.FOption;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class GPlanBuilder extends PlanBuilder {
	public GPlanBuilder(String name) {
		super(name);
	}
	
	public GPlanBuilder load(Map<String,Object> args, String dsId) {
		super.load(dsId, ScriptUtils.parseLoadOptions(args));
		return this;
	}

	public GPlanBuilder loadTextFile(String... paths) {
		super.loadTextFile(Arrays.asList(paths), LoadOptions.create());
		return this;
	}
	public GPlanBuilder loadTextFile(Map<String,Object> args, String... paths) {
		super.loadTextFile(Arrays.asList(paths), ScriptUtils.parseLoadOptions(args));
		return this;
	}

	public GPlanBuilder filter(Map<String,Object> args, String pred) {
		super.filter(ScriptUtils.parseRecordScript(args, pred));
		return this;
	}
	
	public GPlanBuilder defineColumn(Map<String,Object> args, String colDecl, String initValue) {
		RecordScript script = ScriptUtils.getStringOption(args, "initializer")
								.map(init -> RecordScript.of(initValue, init))
								.getOrElse(() -> RecordScript.of(initValue));
		super.defineColumn(colDecl, script);
		return this;
	}
	
	public GPlanBuilder update(Map<String,Object> args, String expr) {
		super.update(ScriptUtils.parseRecordScript(args, expr));
		return this;
	}
	
	public GPlanBuilder expand(Map<String,Object> args, String colDecl, String updExpr) {
		RecordScript script = ScriptUtils.getStringOption(args, "initializer")
								.map(init -> RecordScript.of(updExpr, init))
								.getOrElse(() -> RecordScript.of(updExpr));
		super.expand(colDecl, script);
		return this;
	}
	
	public GPlanBuilder distinct(Map<String,Object> args, String keyCols) {
		ScriptUtils.getIntOption(args, "workerCount")
					.ifPresent(cnt -> super.distinct(keyCols, cnt))
					.ifAbsent(() -> super.distinct(keyCols));
		return this;
	}
	
	public GPlanBuilder aggregate(Closure aggrsDecl) {
		List<AggregateFunction> aggrs = new AggregateFunctionListParser().parse(aggrsDecl);
		super.aggregate(aggrs.toArray(new AggregateFunction[0]));
		return this;
	}
	
	public GPlanBuilder aggregateByGroup(Group group, Closure aggrsDecl) {
		List<AggregateFunction> aggrs = new AggregateFunctionListParser().parse(aggrsDecl);
		super.aggregateByGroup(group, aggrs.toArray(new AggregateFunction[0]));
		return this;
	}
	
	public GPlanBuilder takeByGroup(Group group, int count) {
		super.takeByGroup(group, count);
		return this;
	}
	
	public GPlanBuilder storeByGroup(Map<String,Object> args, Group group, String rootPath) {
		StoreDataSetOptions opts = ScriptUtils.parseStoreDataSetOptions(args);
		super.storeByGroup(group, rootPath, opts);
		return this;
	}
	public GPlanBuilder storeByGroup(Group group, String rootPath) {
		super.storeByGroup(group, rootPath, StoreDataSetOptions.create());
		return this;
	}

	public GPlanBuilder parseCsv(Map<String,Object> args, String csvColumn, Closure csvDecls) {
		if ( csvDecls != null ) {
			args = options(args, csvDecls);
		}
		super.parseCsv(csvColumn, ScriptUtils.parseParseCsvOptions(args));
		return this;
	}
	public GPlanBuilder parseCsv(String csvColumn, Closure csvDecls) {
		return parseCsv(Maps.newHashMap(), csvColumn, csvDecls);
	}
	public GPlanBuilder parseCsv(String csvColumn) {
		return parseCsv(Maps.newHashMap(), csvColumn, null);
	}
	
	public GPlanBuilder storeAsCsv(Map<String,Object> args, String path) {
		StoreAsCsvOptions opts = StoreAsCsvOptions.create();
		ScriptUtils.getStringOption(args, "delim").map(s -> s.charAt(0)).ifPresent(opts::delimiter);
		ScriptUtils.getStringOption(args, "quote").map(s -> s.charAt(0)).ifPresent(opts::quote);
		ScriptUtils.getStringOption(args, "escape").map(s -> s.charAt(0)).ifPresent(opts::escape);
		ScriptUtils.getStringOption(args, "charset").ifPresent(opts::charset);
		ScriptUtils.getBooleanOption(args, "headerFirst").ifPresent(opts::headerFirst);
		ScriptUtils.getLongOption(args, "blockSize").ifPresent(opts::blockSize);
		
		super.storeAsCsv(path, opts);
		return this;
	}

	public GPlanBuilder hashJoin(Map<String,Object> args, String inputJoinCols,
								String paramDataSet, String paramJoinCols) {
		String outJoinCols = ScriptUtils.getOrThrow(args, "output");
		JoinOptions jopts = ScriptUtils.getOption(args, "type")
										.map(o -> {
											if ( o instanceof JoinType ) {
												return (JoinType)o;
											}
											else {
												return JoinType.fromString(o.toString());
											}
										})
										.map(t -> new JoinOptions().joinType(t))
										.getOrElse(() -> JoinOptions.INNER_JOIN());
		ScriptUtils.getIntOption(args, "workerCount").ifPresent(jopts::workerCount);
		
		super.hashJoin(inputJoinCols, paramDataSet, paramJoinCols, outJoinCols, jopts);
		return this;
	}

	public GPlanBuilder loadHashJoinFile(Map<String,Object> args,
										String leftDsId, String leftJoinCols,
										String rightDsId, String rightJoinCols) {
		String outJoinCols = ScriptUtils.getOrThrow(args, "output");
		JoinOptions jopts = ScriptUtils.getOption(args, "type")
										.map(o -> {
											if ( o instanceof JoinType ) {
												return (JoinType)o;
											}
											else {
												return JoinType.fromString(o.toString());
											}
										})
										.map(t -> new JoinOptions().joinType(t))
										.getOrElse(() -> JoinOptions.INNER_JOIN());
		ScriptUtils.getIntOption(args, "workerCount").ifPresent(jopts::workerCount);
		
		super.loadHashJoinFile(leftDsId, leftJoinCols, rightDsId, rightJoinCols, outJoinCols, jopts);
		return this;
	}

	public GPlanBuilder buffer(Map<String,Object> args, String geomCol, double dist) {
		GeomOpOptions opts = ScriptUtils.parseGeomOpOptions(args);
		FOption<Integer> segCount = ScriptUtils.getIntOption(args, "segmentCount");
		super.buffer(geomCol, dist, segCount, opts);
		return this;
	}

	public GPlanBuilder centroid(Map<String,Object> args, String geomCol) {
		GeomOpOptions opts = ScriptUtils.parseGeomOpOptions(args);
		super.centroid(geomCol, opts);
		return this;
	}

	public GPlanBuilder toXY(Map<String,Object> args, String geomCol, String xCol, String yCol) {
		boolean keepGeomColumn = ScriptUtils.getBooleanOption(args, "keepGeomColumn")
											.getOrElse(false);
		super.toXY(geomCol, xCol, yCol, keepGeomColumn);
		return this;
	}

	public GPlanBuilder filterSpatially(String geomCol, SpatialRelation rel, Envelope bounds) {
		filterSpatially(geomCol, rel, GeoClientUtils.toPolygon(bounds));
		return this;
	}
	public GPlanBuilder filterSpatially(Map<String,Object> args, String geomCol,
										SpatialRelation rel, Object key) {
		PredicateOptions opts = PredicateOptions.create();
		ScriptUtils.getBooleanOption(args, "negated").ifPresent(opts::negated);
		
		Geometry geomKey = (key instanceof Envelope )
						? GeoClientUtils.toPolygon((Envelope)key)
						: (Geometry)key;
		
		filterSpatially(geomCol, rel, geomKey, opts);
		return this;
	}

	public GPlanBuilder intersection(Map<String,Object> args, String leftGeomCol,
										String rightGeomCol) {
		String outGeomCol = ScriptUtils.getOrThrow(args, "output");
		super.intersection(leftGeomCol, rightGeomCol, outGeomCol);
		return this;
	}

	public GPlanBuilder query(String dsId, Object key) {
		return query(Maps.newHashMap(), dsId, key);
	}
	public GPlanBuilder query(Map<String,Object> args, String dsId, Object key) {
		PredicateOptions opts = ScriptUtils.parsePredicateOptions(args);
		
		if ( key instanceof Geometry ) {
			super.query(dsId, (Geometry)key, opts);
		}
		else if ( key instanceof GDataSet ) {
			super.query(dsId, ((GDataSet)key).getId(), opts);
		}
		else if ( key instanceof Envelope ) {
			super.query(dsId, (Envelope)key, opts);
		}
		else {
			throw new IllegalArgumentException("invalid key object: " + key);
		}
		
		return this;
	}
	
	public GPlanBuilder spatialJoin(Map<String,Object> args, String geomCol,
									String paramDsId) {
		SpatialJoinOptions opts = ScriptUtils.parseSpatialJoinOptions(args);
		
		super.spatialJoin(geomCol, paramDsId, opts);
		return this;
	}
	public GPlanBuilder spatialSemiJoin(Map<String,Object> args, String geomCol,
									String paramDsId) {
		SpatialJoinOptions opts = ScriptUtils.parseSpatialJoinOptions(args);
		
		super.spatialSemiJoin(geomCol, paramDsId, opts);
		return this;
	}
	public GPlanBuilder spatialOuterJoin(Map<String,Object> args, String geomCol,
										String paramDsId) {
		SpatialJoinOptions opts = ScriptUtils.parseSpatialJoinOptions(args);
		
		super.spatialOuterJoin(geomCol, paramDsId, opts);
		return this;
	}
	public GPlanBuilder intersectionJoin(Map<String,Object> args, String geomCol,
										String paramDsId) {
		SpatialJoinOptions opts = ScriptUtils.parseSpatialJoinOptions(args);
		
		super.intersectionJoin(geomCol, paramDsId, opts);
		return this;
	}
	
	public PlanBuilder loadGrid(Map<String,Object> args, SquareGrid grid) {
		FOption<Integer> splitCount = ScriptUtils.getIntOption(args, "splitCount");
		
		splitCount.ifPresent(cnt -> super.loadGrid(grid, cnt))
					.ifAbsent(() -> super.loadGrid(grid));
		return this;
	}
	public PlanBuilder loadGrid(SquareGrid grid) {
		return loadGrid(Maps.newHashMap(), grid);
	}
	
	public PlanBuilder assignGridCell(Map<String,Object> args, String geomCol, SquareGrid grid) {
		boolean assignOutside = ScriptUtils.getBooleanOption(args, "assignOutside")
											.getOrElse(false);
		super.assignGridCell(geomCol, grid, assignOutside);
		return this;
	}
	public PlanBuilder assignGridCell(String geomCol, SquareGrid grid) {
		return assignGridCell(Maps.newHashMap(), geomCol, grid);
	}
	
	public GPlanBuilder loadJdbcTable(Map<String,Object> args, String tblName, JdbcConnectOptions jdbcOpts) {
		LoadJdbcTableOptions opts = LoadJdbcTableOptions.create();
		ScriptUtils.getStringOption(args, "selectExpr").ifPresent(opts::selectExpr);
		ScriptUtils.getIntOption(args, "mapperCount").ifPresent(opts::mapperCount);
		
		super.loadJdbcTable(tblName, jdbcOpts, opts);
		return this;
	}
	public GPlanBuilder loadJdbcTable(Map<String,Object> args, String tblName) {
		return loadJdbcTable(args, tblName, ScriptUtils.parseJdbcConnectOptions(args));
	}

	public GPlanBuilder storeIntoJdbcTable(Map<String,Object> args, String tblName,
											JdbcConnectOptions jdbcOpts) {
		String valueExpr = ScriptUtils.getOrThrow(args, "valueExpr");
		super.storeIntoJdbcTable(tblName, jdbcOpts, valueExpr);
		return this;
	}
	
	//
	//
	//
	public JoinType getINNER_JOIN() { return JoinType.INNER_JOIN; }
	public JoinType getLEFT_OUTER_JOIN() { return JoinType.LEFT_OUTER_JOIN; }
	public JoinType getRIGHT_OUTER_JOIN() { return JoinType.RIGHT_OUTER_JOIN; }
	public JoinType getFULL_OUTER_JOIN() { return JoinType.FULL_OUTER_JOIN; }
	public JoinType getSEMI_JOIN() { return JoinType.SEMI_JOIN; }
	
	public RecordSchema schema(String decl) {
		return RecordSchema.parse(decl);
	}
	
	public GDataSet dataset(String id) {
		return new GDataSet(id);
	}
	
	public JdbcConnectOptions jdbcConnection(Closure decl) {
		return ScriptUtils.parseJdbcConnectOptions(decl);
	}
	
	public Group group(Map<String,Object> args) {
		return ScriptUtils.parseGroup(args);
	}
	public Group group(Map<String,Object> args, String keys) {
		return ScriptUtils.parseGroup(args, keys);
	}
	public Group group(String keys) {
		return ScriptUtils.parseGroup(Maps.newHashMap(), keys);
	}
	
	public SpatialRelation getIntersects() {
		return SpatialRelation.INTERSECTS;
	}
	
	public SpatialRelation withinDistance(Object distExpr) {
		return SpatialRelation.WITHIN_DISTANCE(distance(distExpr));
	}
	
	public double distance(Object distExpr) {
		return ScriptUtils.parseDistance(distExpr);
	}
	
	public Geometry wkt(String wktStr) throws ParseException {
		return GeoClientUtils.fromWKT(wktStr);
	}
	
	public Envelope envelope(Map<String,Double> coords) {
		return ScriptUtils.parseEnvelope(coords);
	}
	
	public SquareGrid squareGrid(Object bounds, Size2d cellSize) {
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
	
	
	public Size2d size2d(String str) {
		return ScriptUtils.parseSize2d(str);
	}

	
	public Map<String,Object> options(Map<String,Object> args, Closure decl) {
		OptionsParser parser = new OptionsParser(args);
		ScriptUtils.callClosure(decl, parser);
		return parser.getArguments();
	}
	public Map<String,Object> options(Closure decl) {
		OptionsParser parser = new OptionsParser(Maps.newHashMap());
		ScriptUtils.callClosure(decl, parser);
		return parser.getArguments();
	}
}
