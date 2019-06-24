package marmot.script.plan;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

import groovy.lang.Closure;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.PlanBuilder;
import marmot.RecordSchema;
import marmot.RecordScript;
import marmot.StoreDataSetOptions;
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
import marmot.script.DslScriptBase;
import marmot.script.ScriptUtils;
import marmot.script.dslobj.AggregateFunctionListParser;
import marmot.script.dslobj.GDataSet;
import utils.func.FOption;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public abstract class PlanDslHandler extends DslScriptBase {
	private PlanBuilder m_builder;
	
	public PlanDslHandler() { }
	
	protected PlanDslHandler(MarmotRuntime marmot, String name) {
		super(marmot);
		
		m_builder = new PlanBuilder(name);
	}
	
	public Plan build() {
		return m_builder.build();
	}
	
	public PlanBuilder load(Map<String,Object> args, String dsId) {
		setupState();
		
		m_builder.load(dsId, ScriptUtils.parseLoadOptions(args));
		return m_builder;
	}
	public PlanBuilder load(String dsId) {
		return load(new HashMap<>(), dsId);
	}

	public PlanBuilder loadTextFile(String... paths) {
		setupState();
		
		m_builder.loadTextFile(Arrays.asList(paths), LoadOptions.DEFAULT());
		return m_builder;
	}
	public PlanBuilder loadTextFile(Map<String,Object> args, String... paths) {
		setupState();
		
		m_builder.loadTextFile(Arrays.asList(paths), ScriptUtils.parseLoadOptions(args));
		return m_builder;
	}

	public PlanBuilder filter(Map<String,Object> args, String pred) {
		setupState();
		
		m_builder.filter(ScriptUtils.parseRecordScript(args, pred));
		return m_builder;
	}
	public PlanBuilder filter(String pred) {
		setupState();
		
		m_builder.filter(pred);
		return m_builder;
	}
	
	public PlanBuilder project(String columnSelection) {
		setupState();
		
		m_builder.project(columnSelection);
		return m_builder;
	}
	
	public PlanBuilder defineColumn(Map<String,Object> args, String colDecl, String initValue) {
		setupState();
		
		RecordScript script = ScriptUtils.getStringOption(args, "initializer")
								.map(init -> RecordScript.of(initValue, init))
								.getOrElse(() -> RecordScript.of(initValue));
		m_builder.defineColumn(colDecl, script);
		return m_builder;
	}
	public PlanBuilder defineColumn(String colDecl) {
		setupState();
		
		m_builder.defineColumn(colDecl);
		return m_builder;
	}
	public PlanBuilder defineColumn(String colDecl, String initValue) {
		setupState();
		
		m_builder.defineColumn(colDecl, initValue);
		return m_builder;
	}
	
	public PlanBuilder update(Map<String,Object> args, String expr) {
		setupState();
		
		m_builder.update(ScriptUtils.parseRecordScript(args, expr));
		return m_builder;
	}
	public PlanBuilder update(String expr) {
		setupState();
		
		m_builder.update(expr);
		return m_builder;
	}
	
	public PlanBuilder expand(String colDecl, RecordScript initScript) {
		setupState();
		
		m_builder.expand(colDecl, initScript);
		return m_builder;
	}
	public PlanBuilder expand(String colDecl) {
		setupState();
		
		m_builder.expand(colDecl);
		return m_builder;
	}
	public PlanBuilder expand(String colDecl, String updExpr) {
		return expand(colDecl, RecordScript.of(updExpr));
	}
	
	public PlanBuilder take(long count) {
		setupState();
		
		m_builder.take(count);
		return m_builder;
	}
	
	public PlanBuilder assignUid(String uidColName) {
		setupState();
		
		m_builder.assignUid(uidColName	);
		return m_builder;
	}
	
	public PlanBuilder sample(double sampleRatio) {
		setupState();
		
		m_builder.sample(sampleRatio);
		return m_builder;
	}
	
	public PlanBuilder shard(int partCount) {
		setupState();
		
		m_builder.shard(partCount);
		return m_builder;
	}
	
	public PlanBuilder pickTopK(String keyCols, int count) {
		setupState();
		
		m_builder.pickTopK(keyCols, count);
		return m_builder;
	}
	
	public PlanBuilder sort(String cmpKeyCols) {
		setupState();
		
		m_builder.sort(cmpKeyCols);
		return m_builder;
	}
	
	public PlanBuilder distinct(Map<String,Object> args, String keyCols) {
		setupState();
		
		ScriptUtils.getIntOption(args, "workerCount")
					.ifPresent(cnt -> m_builder.distinct(keyCols, cnt))
					.ifAbsent(() -> m_builder.distinct(keyCols));
		return m_builder;
	}
	public PlanBuilder distinct(String keyCols) {
		setupState();
		
		m_builder.distinct(keyCols);
		return m_builder;
	}
	
	public PlanBuilder aggregate(Closure<?> aggrsDecl) {
		setupState();
		
		List<AggregateFunction> aggrs = new AggregateFunctionListParser().parse(aggrsDecl);
		m_builder.aggregate(aggrs.toArray(new AggregateFunction[0]));
		return m_builder;
	}
	
	public PlanBuilder aggregateByGroup(Map<String,Object> args, String keyCols,
										Closure<?> aggrsDecl) {
		setupState();
		
		Group group = ScriptUtils.parseGroup(args, keyCols);
		List<AggregateFunction> aggrs = new AggregateFunctionListParser().parse(aggrsDecl);
		m_builder.aggregateByGroup(group, aggrs.toArray(new AggregateFunction[0]));
		return m_builder;
	}
	public PlanBuilder aggregateByGroup(String keyCols, Closure<?> aggrsDecl) {
		setupState();
		
		return aggregateByGroup(Maps.newHashMap(), keyCols, aggrsDecl);
	}

	public PlanBuilder aggregateByGroup(Map<String,Object> args, String keyCols,
										List<AggregateFunction> aggrList) {
		setupState();
		
		Group group = ScriptUtils.parseGroup(args, keyCols);
		m_builder.aggregateByGroup(group, aggrList);
		return m_builder;
	}
	public PlanBuilder aggregateByGroup(String keyCols, List<AggregateFunction> aggrList) {
		return aggregateByGroup(Maps.newHashMap(), keyCols, aggrList);
	}
	
	public PlanBuilder takeByGroup(Map<String,Object> args, String keyCols, int count) {
		setupState();
		
		Group group = ScriptUtils.parseGroup(args, keyCols);
		m_builder.takeByGroup(group, count);
		return m_builder;
	}
	public PlanBuilder takeByGroup(String keyCols, int count) {
		setupState();
		
		return takeByGroup(Maps.newHashMap(), keyCols, count);
	}
	
	public PlanBuilder listByGroup(Map<String,Object> args, String keyCols) {
		setupState();
		
		Group group = ScriptUtils.parseGroup(args, keyCols);
		m_builder.listByGroup(group);
		return m_builder;
	}
	public PlanBuilder listByGroup(String keyCols) {
		return listByGroup(Maps.newHashMap(), keyCols);
	}
	
	public PlanBuilder storeByGroup(Map<String,Object> args, String keyCols, String rootPath) {
		setupState();
		
		Group group = ScriptUtils.parseGroup(args, keyCols);
		StoreDataSetOptions opts = ScriptUtils.parseStoreDataSetOptions(args);
		m_builder.storeByGroup(group, rootPath, opts);
		return m_builder;
	}
	public PlanBuilder storeByGroup(String keyCols, String rootPath) {
		return storeByGroup(Maps.newHashMap(), keyCols, rootPath);
	}
	
	public PlanBuilder reduceToSingleRecordByGroup(Map<String,Object> args, String keyCols,
													RecordSchema outSchema,
													String tagCol, String valueCol) {
		setupState();
		
		Group group = ScriptUtils.parseGroup(args, keyCols);
		m_builder.reduceToSingleRecordByGroup(group, outSchema, tagCol, valueCol);
		return m_builder;
	}
	public PlanBuilder reduceToSingleRecordByGroup(String keyCols, RecordSchema outSchema,
													String tagCol, String valueCol) {
		return reduceToSingleRecordByGroup(Maps.newHashMap(), keyCols, outSchema, tagCol, valueCol);
	}

	public PlanBuilder parseCsv(Map<String,Object> args, String csvColumn, Closure<?> csvDecls) {
		setupState();
		
		m_builder.parseCsv(csvColumn, ScriptUtils.parseParseCsvOptions(args));
		return m_builder;
	}
	public PlanBuilder parseCsv(String csvColumn, Closure<?> csvDecls) {
		return parseCsv(Maps.newHashMap(), csvColumn, csvDecls);
	}
	public PlanBuilder parseCsv(String csvColumn) {
		return parseCsv(Maps.newHashMap(), csvColumn, null);
	}
	
	public PlanBuilder storeAsCsv(Map<String,Object> args, String path) {
		setupState();
		
		StoreAsCsvOptions opts = StoreAsCsvOptions.create();
		ScriptUtils.getStringOption(args, "delim").map(s -> s.charAt(0)).ifPresent(opts::delimiter);
		ScriptUtils.getStringOption(args, "quote").map(s -> s.charAt(0)).ifPresent(opts::quote);
		ScriptUtils.getStringOption(args, "escape").map(s -> s.charAt(0)).ifPresent(opts::escape);
		ScriptUtils.getStringOption(args, "charset").ifPresent(opts::charset);
		ScriptUtils.getBooleanOption(args, "headerFirst").ifPresent(opts::headerFirst);
		ScriptUtils.getLongOption(args, "blockSize").ifPresent(opts::blockSize);
		
		m_builder.storeAsCsv(path, opts);
		return m_builder;
	}
	public PlanBuilder storeAsCsv(String path) {
		return storeAsCsv(Maps.newHashMap(), path);
	}

	public PlanBuilder hashJoin(Map<String,Object> args, String inputJoinCols,
								String paramDataSet, String paramJoinCols) {
		setupState();
		
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
		
		m_builder.hashJoin(inputJoinCols, paramDataSet, paramJoinCols, outJoinCols, jopts);
		return m_builder;
	}

	public PlanBuilder loadHashJoinFile(Map<String,Object> args,
										String leftDsId, String leftJoinCols,
										String rightDsId, String rightJoinCols) {
		setupState();
		
		String outJoinCols = ScriptUtils.getOrThrow(args, "output").toString();
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
		
		m_builder.loadHashJoin(leftDsId, leftJoinCols, rightDsId, rightJoinCols, outJoinCols, jopts);
		return m_builder;
	}

	public PlanBuilder buffer(Map<String,Object> args, String geomCol, double dist) {
		setupState();
		
		GeomOpOptions opts = ScriptUtils.parseGeomOpOptions(args);
		FOption<Integer> segCount = ScriptUtils.getIntOption(args, "segmentCount");
		m_builder.buffer(geomCol, dist, segCount, opts);
		return m_builder;
	}
	public PlanBuilder buffer(String geomCol, double dist) {
		setupState();
		
		m_builder.buffer(geomCol, dist);
		return m_builder;
	}

	public PlanBuilder centroid(Map<String,Object> args, String geomCol) {
		setupState();

		boolean inside = getBooleanOption(args, "inside").getOrElse(false);
		GeomOpOptions opts = ScriptUtils.parseGeomOpOptions(args);
		m_builder.centroid(geomCol, inside, opts);
		return m_builder;
	}
	public PlanBuilder centroid(String geomCol) {
		setupState();
		
		m_builder.centroid(geomCol);
		return m_builder;
	}

	public PlanBuilder toXY(Map<String,Object> args, String geomCol, String xCol, String yCol) {
		setupState();
		
		boolean keepGeomColumn = ScriptUtils.getBooleanOption(args, "keepGeomColumn")
											.getOrElse(false);
		m_builder.toXY(geomCol, xCol, yCol, keepGeomColumn);
		return m_builder;
	}
	public PlanBuilder toXY(String geomCol, String xCol, String yCol) {
		setupState();
		
		m_builder.toXY(geomCol, xCol, yCol);
		return m_builder;
	}
	
	public PlanBuilder toPoint(String xCol, String yCol, String geomCol) {
		setupState();
		
		m_builder.toPoint(xCol, yCol, geomCol);
		return m_builder;
	}

	public PlanBuilder filterSpatially(String geomCol, SpatialRelation rel, Object key) {
		setupState();
		
		m_builder.filterSpatially(geomCol, rel, Geometry(key));
		return m_builder;
	}
	public PlanBuilder filterSpatially(Map<String,Object> args, String geomCol,
										SpatialRelation rel, Object key) {
		setupState();
		
		PredicateOptions opts = ScriptUtils.getBooleanOption(args, "negated")
											.map(PredicateOptions::NEGATED)
											.getOrElse(PredicateOptions.EMPTY);
		
		m_builder.filterSpatially(geomCol, rel, Geometry(key), opts);
		return m_builder;
	}

	public PlanBuilder transformCrs(String geomCol, String srcSrid, String tarSrid) {
		setupState();
		
		m_builder.transformCrs(geomCol, srcSrid, tarSrid);
		return m_builder;
	}

	public PlanBuilder intersection(Map<String,Object> args, String leftGeomCol,
										Geometry geom) {
		setupState();
		
		GeomOpOptions opts = ScriptUtils.parseGeomOpOptions(args);
		m_builder.intersection(leftGeomCol, geom, opts);
		return m_builder;
	}
	public PlanBuilder intersection(String leftGeomCol, Geometry geom) {
		setupState();
		
		m_builder.intersection(leftGeomCol, geom);
		return m_builder;
	}

	public PlanBuilder intersection(Map<String,Object> args, String leftGeomCol,
										String rightGeomCol) {
		setupState();
		
		String outGeomCol = ScriptUtils.getOrThrow(args, "output");
		m_builder.intersection(leftGeomCol, rightGeomCol, outGeomCol);
		return m_builder;
	}

	public PlanBuilder query(String dsId, Object key) {
		setupState();
		
		return query(Maps.newHashMap(), dsId, key);
	}
	public PlanBuilder query(Map<String,Object> args, String dsId, Object key) {
		setupState();
		
		PredicateOptions opts = ScriptUtils.parsePredicateOptions(args);
		
		if ( key instanceof Geometry ) {
			m_builder.query(dsId, (Geometry)key, opts);
		}
		else if ( key instanceof GDataSet ) {
			m_builder.query(dsId, ((GDataSet)key).getId(), opts);
		}
		else if ( key instanceof Envelope ) {
			m_builder.query(dsId, (Envelope)key, opts);
		}
		else {
			throw new IllegalArgumentException("invalid key object: " + key);
		}
		
		return m_builder;
	}
	
	public PlanBuilder spatialJoin(Map<String,Object> args, String geomCol,
									String paramDsId) {
		setupState();
		
		SpatialJoinOptions opts = ScriptUtils.parseSpatialJoinOptions(args);
		
		m_builder.spatialJoin(geomCol, paramDsId, opts);
		return m_builder;
	}
	public PlanBuilder spatialSemiJoin(Map<String,Object> args, String geomCol,
									String paramDsId) {
		setupState();
		
		SpatialJoinOptions opts = ScriptUtils.parseSpatialJoinOptions(args);
		
		m_builder.spatialSemiJoin(geomCol, paramDsId, opts);
		return m_builder;
	}
	public PlanBuilder spatialSemiJoin(String geomCol, String paramDsId) {
		return spatialSemiJoin(Maps.newHashMap(), geomCol, paramDsId);
	}
	public PlanBuilder spatialOuterJoin(Map<String,Object> args, String geomCol,
										String paramDsId) {
		setupState();
		
		SpatialJoinOptions opts = ScriptUtils.parseSpatialJoinOptions(args);
		
		m_builder.spatialOuterJoin(geomCol, paramDsId, opts);
		return m_builder;
	}
	public PlanBuilder intersectionJoin(Map<String,Object> args, String geomCol,
										String paramDsId) {
		setupState();
		
		SpatialJoinOptions opts = ScriptUtils.parseSpatialJoinOptions(args);
		
		m_builder.intersectionJoin(geomCol, paramDsId, opts);
		return m_builder;
	}
	public PlanBuilder clipJoin(String geomCol, String paramDsId) {
		setupState();
		
		m_builder.arcClip(geomCol, paramDsId);
		return m_builder;
	}
	
	public PlanBuilder loadGrid(Map<String,Object> args, SquareGrid grid) {
		setupState();
		
		FOption<Integer> splitCount = ScriptUtils.getIntOption(args, "splitCount");
		
		splitCount.ifPresent(cnt -> m_builder.loadGrid(grid, cnt))
					.ifAbsent(() -> m_builder.loadGrid(grid));
		return m_builder;
	}
	public PlanBuilder loadGrid(SquareGrid grid) {
		return loadGrid(Maps.newHashMap(), grid);
	}
	
	public PlanBuilder assignGridCell(Map<String,Object> args, String geomCol, SquareGrid grid) {
		setupState();
		
		boolean assignOutside = ScriptUtils.getBooleanOption(args, "assignOutside")
											.getOrElse(false);
		m_builder.assignGridCell(geomCol, grid, assignOutside);
		return m_builder;
	}
	public PlanBuilder assignGridCell(String geomCol, SquareGrid grid) {
		return assignGridCell(Maps.newHashMap(), geomCol, grid);
	}
	
	public PlanBuilder loadJdbcTable(Map<String,Object> args, String tblName,
										JdbcConnectOptions jdbcOpts) {
		setupState();
		
		LoadJdbcTableOptions opts = LoadJdbcTableOptions.create();
		ScriptUtils.getStringOption(args, "selectExpr").ifPresent(opts::selectExpr);
		ScriptUtils.getIntOption(args, "mapperCount").ifPresent(opts::mapperCount);
		
		m_builder.loadJdbcTable(tblName, jdbcOpts, opts);
		return m_builder;
	}
	public PlanBuilder loadJdbcTable(Map<String,Object> args, String tblName) {
		return loadJdbcTable(args, tblName, ScriptUtils.parseJdbcConnectOptions(args));
	}

	public PlanBuilder storeIntoJdbcTable(Map<String,Object> args, String tblName,
											JdbcConnectOptions jdbcOpts) {
		setupState();
		
		String valueExpr = ScriptUtils.getOrThrow(args, "valueExpr");
		m_builder.storeIntoJdbcTable(tblName, jdbcOpts, valueExpr);
		return m_builder;
	}
	
	//
	//
	//
	
	private void setupState() {
		if ( m_builder == null ) {
			String planName = (String)getBinding().getProperty("MARMOT_PLAN_NAME");
			m_builder = new PlanBuilder(planName);
		}
	}
}
