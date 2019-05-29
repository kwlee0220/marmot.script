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
import marmot.script.ScriptParsingObject;
import marmot.script.ScriptUtils;
import marmot.script.dslobj.AggregateFunctionListParser;
import marmot.script.dslobj.GDataSet;
import utils.func.FOption;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class GPlanBuilder extends ScriptParsingObject {
	private final PlanBuilder m_builder;
	
	public GPlanBuilder(MarmotRuntime marmot, String name) {
		super(marmot);
		
		m_builder = new PlanBuilder(name);
	}
	
	public Plan build() {
		return m_builder.build();
	}
	
	public GPlanBuilder load(Map<String,Object> args, String dsId) {
		m_builder.load(dsId, ScriptUtils.parseLoadOptions(args));
		return this;
	}
	public GPlanBuilder load(String dsId) {
		return load(new HashMap<>(), dsId);
	}

	public GPlanBuilder loadTextFile(String... paths) {
		m_builder.loadTextFile(Arrays.asList(paths), LoadOptions.create());
		return this;
	}
	public GPlanBuilder loadTextFile(Map<String,Object> args, String... paths) {
		m_builder.loadTextFile(Arrays.asList(paths), ScriptUtils.parseLoadOptions(args));
		return this;
	}

	public GPlanBuilder filter(Map<String,Object> args, String pred) {
		m_builder.filter(ScriptUtils.parseRecordScript(args, pred));
		return this;
	}
	public GPlanBuilder filter(String pred) {
		m_builder.filter(pred);
		return this;
	}
	
	public GPlanBuilder project(String columnSelection) {
		m_builder.project(columnSelection);
		return this;
	}
	
	public GPlanBuilder defineColumn(Map<String,Object> args, String colDecl, String initValue) {
		RecordScript script = ScriptUtils.getStringOption(args, "initializer")
								.map(init -> RecordScript.of(initValue, init))
								.getOrElse(() -> RecordScript.of(initValue));
		m_builder.defineColumn(colDecl, script);
		return this;
	}
	public GPlanBuilder defineColumn(String colDecl) {
		m_builder.defineColumn(colDecl);
		return this;
	}
	public GPlanBuilder defineColumn(String colDecl, String initValue) {
		m_builder.defineColumn(colDecl, initValue);
		return this;
	}
	
	public GPlanBuilder update(Map<String,Object> args, String expr) {
		m_builder.update(ScriptUtils.parseRecordScript(args, expr));
		return this;
	}
	public GPlanBuilder update(String expr) {
		m_builder.update(expr);
		return this;
	}
	
	public GPlanBuilder expand(Map<String,Object> args, String colDecl, String updExpr) {
		RecordScript script = ScriptUtils.getStringOption(args, "initializer")
								.map(init -> RecordScript.of(updExpr, init))
								.getOrElse(() -> RecordScript.of(updExpr));
		m_builder.expand(colDecl, script);
		return this;
	}
	public GPlanBuilder expand(String colDecl) {
		m_builder.expand(colDecl);
		return this;
	}
	public GPlanBuilder expand(String colDecl, String updExpr) {
		m_builder.expand(colDecl, updExpr);
		return this;
	}
	
	public GPlanBuilder take(long count) {
		m_builder.take(count);
		return this;
	}
	
	public GPlanBuilder assignUid(String uidColName) {
		m_builder.assignUid(uidColName	);
		return this;
	}
	
	public GPlanBuilder sample(double sampleRatio) {
		m_builder.sample(sampleRatio);
		return this;
	}
	
	public GPlanBuilder shard(int partCount) {
		m_builder.shard(partCount);
		return this;
	}
	
	public GPlanBuilder pickTopK(String keyCols, int count) {
		m_builder.pickTopK(keyCols, count);
		return this;
	}
	
	public GPlanBuilder sort(String cmpKeyCols) {
		m_builder.sort(cmpKeyCols);
		return this;
	}
	
	public GPlanBuilder distinct(Map<String,Object> args, String keyCols) {
		ScriptUtils.getIntOption(args, "workerCount")
					.ifPresent(cnt -> m_builder.distinct(keyCols, cnt))
					.ifAbsent(() -> m_builder.distinct(keyCols));
		return this;
	}
	public GPlanBuilder distinct(String keyCols) {
		m_builder.distinct(keyCols);
		return this;
	}
	
	public GPlanBuilder aggregate(Closure<?> aggrsDecl) {
		List<AggregateFunction> aggrs = new AggregateFunctionListParser().parse(aggrsDecl);
		m_builder.aggregate(aggrs.toArray(new AggregateFunction[0]));
		return this;
	}
	
	public GPlanBuilder aggregateByGroup(Map<String,Object> args, String keyCols,
										Closure<?> aggrsDecl) {
		Group group = ScriptUtils.parseGroup(args, keyCols);
		List<AggregateFunction> aggrs = new AggregateFunctionListParser().parse(aggrsDecl);
		m_builder.aggregateByGroup(group, aggrs.toArray(new AggregateFunction[0]));
		return this;
	}
	public GPlanBuilder aggregateByGroup(String keyCols, Closure<?> aggrsDecl) {
		return aggregateByGroup(Maps.newHashMap(), keyCols, aggrsDecl);
	}

	public GPlanBuilder aggregateByGroup(Map<String,Object> args, String keyCols,
										List<AggregateFunction> aggrList) {
		Group group = ScriptUtils.parseGroup(args, keyCols);
		m_builder.aggregateByGroup(group, aggrList.toArray(new AggregateFunction[0]));
		return this;
	}
	public GPlanBuilder aggregateByGroup(String keyCols, List<AggregateFunction> aggrList) {
		return aggregateByGroup(Maps.newHashMap(), keyCols, aggrList);
	}
	
	public GPlanBuilder takeByGroup(Map<String,Object> args, String keyCols, int count) {
		Group group = ScriptUtils.parseGroup(args, keyCols);
		m_builder.takeByGroup(group, count);
		return this;
	}
	public GPlanBuilder takeByGroup(String keyCols, int count) {
		return takeByGroup(Maps.newHashMap(), keyCols, count);
	}
	
	public GPlanBuilder listByGroup(Map<String,Object> args, String keyCols) {
		Group group = ScriptUtils.parseGroup(args, keyCols);
		m_builder.listByGroup(group);
		return this;
	}
	public GPlanBuilder listByGroup(String keyCols) {
		return listByGroup(Maps.newHashMap(), keyCols);
	}
	
	public GPlanBuilder storeByGroup(Map<String,Object> args, String keyCols, String rootPath) {
		Group group = ScriptUtils.parseGroup(args, keyCols);
		StoreDataSetOptions opts = ScriptUtils.parseStoreDataSetOptions(args);
		m_builder.storeByGroup(group, rootPath, opts);
		return this;
	}
	public GPlanBuilder storeByGroup(String keyCols, String rootPath) {
		return storeByGroup(Maps.newHashMap(), keyCols, rootPath);
	}
	
	public GPlanBuilder reduceToSingleRecordByGroup(Map<String,Object> args, String keyCols,
													RecordSchema outSchema,
													String tagCol, String valueCol) {
		Group group = ScriptUtils.parseGroup(args, keyCols);
		m_builder.reduceToSingleRecordByGroup(group, outSchema, tagCol, valueCol);
		return this;
	}
	public GPlanBuilder reduceToSingleRecordByGroup(String keyCols, RecordSchema outSchema,
													String tagCol, String valueCol) {
		return reduceToSingleRecordByGroup(Maps.newHashMap(), keyCols, outSchema, tagCol, valueCol);
	}

	public GPlanBuilder parseCsv(Map<String,Object> args, String csvColumn, Closure<?> csvDecls) {
		m_builder.parseCsv(csvColumn, ScriptUtils.parseParseCsvOptions(args));
		return this;
	}
	public GPlanBuilder parseCsv(String csvColumn, Closure<?> csvDecls) {
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
		
		m_builder.storeAsCsv(path, opts);
		return this;
	}
	public GPlanBuilder storeAsCsv(String path) {
		return storeAsCsv(Maps.newHashMap(), path);
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
		
		m_builder.hashJoin(inputJoinCols, paramDataSet, paramJoinCols, outJoinCols, jopts);
		return this;
	}

	public GPlanBuilder loadHashJoinFile(Map<String,Object> args,
										String leftDsId, String leftJoinCols,
										String rightDsId, String rightJoinCols) {
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
		
		m_builder.loadHashJoinFile(leftDsId, leftJoinCols, rightDsId, rightJoinCols, outJoinCols, jopts);
		return this;
	}

	public GPlanBuilder buffer(Map<String,Object> args, String geomCol, double dist) {
		GeomOpOptions opts = ScriptUtils.parseGeomOpOptions(args);
		FOption<Integer> segCount = ScriptUtils.getIntOption(args, "segmentCount");
		m_builder.buffer(geomCol, dist, segCount, opts);
		return this;
	}
	public GPlanBuilder buffer(String geomCol, double dist) {
		m_builder.buffer(geomCol, dist);
		return this;
	}

	public GPlanBuilder centroid(Map<String,Object> args, String geomCol) {
		GeomOpOptions opts = ScriptUtils.parseGeomOpOptions(args);
		m_builder.centroid(geomCol, opts);
		return this;
	}
	public GPlanBuilder centroid(String geomCol) {
		m_builder.centroid(geomCol);
		return this;
	}

	public GPlanBuilder toXY(Map<String,Object> args, String geomCol, String xCol, String yCol) {
		boolean keepGeomColumn = ScriptUtils.getBooleanOption(args, "keepGeomColumn")
											.getOrElse(false);
		m_builder.toXY(geomCol, xCol, yCol, keepGeomColumn);
		return this;
	}
	public GPlanBuilder toXY(String geomCol, String xCol, String yCol) {
		m_builder.toXY(geomCol, xCol, yCol);
		return this;
	}
	
	public GPlanBuilder toPoint(String xCol, String yCol, String geomCol) {
		m_builder.toPoint(xCol, yCol, geomCol);
		return this;
	}

	public GPlanBuilder filterSpatially(String geomCol, SpatialRelation rel, Object key) {
		m_builder.filterSpatially(geomCol, rel, Geometry(key));
		return this;
	}
	public GPlanBuilder filterSpatially(Map<String,Object> args, String geomCol,
										SpatialRelation rel, Object key) {
		PredicateOptions opts = PredicateOptions.create();
		ScriptUtils.getBooleanOption(args, "negated").ifPresent(opts::negated);
		
		m_builder.filterSpatially(geomCol, rel, Geometry(key), opts);
		return this;
	}

	public GPlanBuilder transformCrs(String geomCol, String srcSrid, String tarSrid) {
		m_builder.transformCrs(geomCol, srcSrid, tarSrid);
		return this;
	}

	public GPlanBuilder intersection(Map<String,Object> args, String leftGeomCol,
										Geometry geom) {
		GeomOpOptions opts = ScriptUtils.parseGeomOpOptions(args);
		m_builder.intersection(leftGeomCol, geom, opts);
		return this;
	}
	public GPlanBuilder intersection(String leftGeomCol, Geometry geom) {
		m_builder.intersection(leftGeomCol, geom);
		return this;
	}

	public GPlanBuilder intersection(Map<String,Object> args, String leftGeomCol,
										String rightGeomCol) {
		String outGeomCol = ScriptUtils.getOrThrow(args, "output");
		m_builder.intersection(leftGeomCol, rightGeomCol, outGeomCol);
		return this;
	}

	public GPlanBuilder query(String dsId, Object key) {
		return query(Maps.newHashMap(), dsId, key);
	}
	public GPlanBuilder query(Map<String,Object> args, String dsId, Object key) {
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
		
		return this;
	}
	
	public GPlanBuilder spatialJoin(Map<String,Object> args, String geomCol,
									String paramDsId) {
		SpatialJoinOptions opts = ScriptUtils.parseSpatialJoinOptions(args);
		
		m_builder.spatialJoin(geomCol, paramDsId, opts);
		return this;
	}
	public GPlanBuilder spatialSemiJoin(Map<String,Object> args, String geomCol,
									String paramDsId) {
		SpatialJoinOptions opts = ScriptUtils.parseSpatialJoinOptions(args);
		
		m_builder.spatialSemiJoin(geomCol, paramDsId, opts);
		return this;
	}
	public GPlanBuilder spatialSemiJoin(String geomCol, String paramDsId) {
		return spatialSemiJoin(Maps.newHashMap(), geomCol, paramDsId);
	}
	public GPlanBuilder spatialOuterJoin(Map<String,Object> args, String geomCol,
										String paramDsId) {
		SpatialJoinOptions opts = ScriptUtils.parseSpatialJoinOptions(args);
		
		m_builder.spatialOuterJoin(geomCol, paramDsId, opts);
		return this;
	}
	public GPlanBuilder intersectionJoin(Map<String,Object> args, String geomCol,
										String paramDsId) {
		SpatialJoinOptions opts = ScriptUtils.parseSpatialJoinOptions(args);
		
		m_builder.intersectionJoin(geomCol, paramDsId, opts);
		return this;
	}
	public GPlanBuilder clipJoin(String geomCol, String paramDsId) {
		m_builder.clipJoin(geomCol, paramDsId);
		return this;
	}
	
	public GPlanBuilder loadGrid(Map<String,Object> args, SquareGrid grid) {
		FOption<Integer> splitCount = ScriptUtils.getIntOption(args, "splitCount");
		
		splitCount.ifPresent(cnt -> m_builder.loadGrid(grid, cnt))
					.ifAbsent(() -> m_builder.loadGrid(grid));
		return this;
	}
	public GPlanBuilder loadGrid(SquareGrid grid) {
		return loadGrid(Maps.newHashMap(), grid);
	}
	
	public GPlanBuilder assignGridCell(Map<String,Object> args, String geomCol, SquareGrid grid) {
		boolean assignOutside = ScriptUtils.getBooleanOption(args, "assignOutside")
											.getOrElse(false);
		m_builder.assignGridCell(geomCol, grid, assignOutside);
		return this;
	}
	public GPlanBuilder assignGridCell(String geomCol, SquareGrid grid) {
		return assignGridCell(Maps.newHashMap(), geomCol, grid);
	}
	
	public GPlanBuilder loadJdbcTable(Map<String,Object> args, String tblName, JdbcConnectOptions jdbcOpts) {
		LoadJdbcTableOptions opts = LoadJdbcTableOptions.create();
		ScriptUtils.getStringOption(args, "selectExpr").ifPresent(opts::selectExpr);
		ScriptUtils.getIntOption(args, "mapperCount").ifPresent(opts::mapperCount);
		
		m_builder.loadJdbcTable(tblName, jdbcOpts, opts);
		return this;
	}
	public GPlanBuilder loadJdbcTable(Map<String,Object> args, String tblName) {
		return loadJdbcTable(args, tblName, ScriptUtils.parseJdbcConnectOptions(args));
	}

	public GPlanBuilder storeIntoJdbcTable(Map<String,Object> args, String tblName,
											JdbcConnectOptions jdbcOpts) {
		String valueExpr = ScriptUtils.getOrThrow(args, "valueExpr");
		m_builder.storeIntoJdbcTable(tblName, jdbcOpts, valueExpr);
		return this;
	}
	
	//
	//
	//
}
