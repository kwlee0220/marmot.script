package marmot.script.plan;

import java.util.Map;

import groovy.lang.Closure;
import marmot.Plan;
import marmot.PlanBuilder;
import marmot.RecordScript;
import marmot.optor.geo.SquareGrid;
import marmot.script.ScriptUtils;
import marmot.script.dslobj.Column;
import marmot.script.dslobj.DataSet;
import marmot.script.dslobj.JdbcTable;
import marmot.script.dslobj.Keyword;
import marmot.script.dslobj.TextFile;
import marmot.script.plan.operator.AggregateFactory;
import marmot.script.plan.operator.AssignSquareGridFactory;
import marmot.script.plan.operator.AssignUidFactory;
import marmot.script.plan.operator.BufferFactory;
import marmot.script.plan.operator.CentroidFactory;
import marmot.script.plan.operator.DefineColumnFactory;
import marmot.script.plan.operator.DistinctFactory;
import marmot.script.plan.operator.ExpandFactory;
import marmot.script.plan.operator.FilterFactory;
import marmot.script.plan.operator.FilterSpatiallyFactory;
import marmot.script.plan.operator.HashJoinFactory;
import marmot.script.plan.operator.ListByGroupFactory;
import marmot.script.plan.operator.LoadDataSetFactory;
import marmot.script.plan.operator.LoadHashJoinFactory;
import marmot.script.plan.operator.LoadJdbcTableFactory;
import marmot.script.plan.operator.LoadSquareGridFactory;
import marmot.script.plan.operator.LoadTextFileFactory;
import marmot.script.plan.operator.OperatorFactory;
import marmot.script.plan.operator.ParseCsvFactory;
import marmot.script.plan.operator.PickTopKFactory;
import marmot.script.plan.operator.ProjectFactory;
import marmot.script.plan.operator.QueryDataSetFactory;
import marmot.script.plan.operator.SampleFactory;
import marmot.script.plan.operator.ShardFactory;
import marmot.script.plan.operator.SortFactory;
import marmot.script.plan.operator.SpatialJoinFactory;
import marmot.script.plan.operator.SpatialOuterJoinFactory;
import marmot.script.plan.operator.SpatialSemiJoinFactory;
import marmot.script.plan.operator.StoreAsCsvFactory;
import marmot.script.plan.operator.StoreIntoJdbcTableFactory;
import marmot.script.plan.operator.TakeFactory;
import marmot.script.plan.operator.ToPointFactory;
import marmot.script.plan.operator.ToXyCoordinatesFactory;
import marmot.script.plan.operator.TransformCrsFactory;
import marmot.script.plan.operator.UpdateFactory;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class OperatorFactoryAssembler extends DslTagParser {
	private final PlanBuilder m_builder;
	private OperatorFactory m_lastFact = null;
	
	public OperatorFactoryAssembler(String name) {
		m_builder = new PlanBuilder(name);
	}
	
	@Override
	public Object getProperty(String name) {
		switch ( name ) {
			case "geometry":
			case "spatially":
			case "all":
				return new Keyword(name);
		}
		
		return super.getProperty(name);
	}
	
	public Plan assemble() {
		createLatest();
		return m_builder.build();
	}
	
	public LoadDataSetFactory load(String dsId) {
		return setNextFactory(new LoadDataSetFactory(dsId));
	}
	
	public LoadDataSetFactory load(DataSet dataset) {
		return load(dataset.getId());
	}
	
	public LoadTextFileFactory load(TextFile file) {
		return setNextFactory(new LoadTextFileFactory(file.getPath()));
	}
	
	public LoadJdbcTableFactory load(JdbcTable table) {
		return setNextFactory(new LoadJdbcTableFactory(table.getName(), table.getOptions()));
	}
	
	public LoadHashJoinFactory load(LoadHashJoinFactory fact) {
		return setNextFactory(fact);
	}
	
	public StoreIntoJdbcTableFactory storeInto(JdbcTable table) {
		return setNextFactory(new StoreIntoJdbcTableFactory(table.getName(), table.getOptions()));
	}
	
	public FilterFactory filter(String pred) {
		return setNextFactory(new FilterFactory(RecordScript.of(pred)));
	}
	
	public FilterFactory filter(Map<String,Object> args, String pred) {
		RecordScript script = ScriptUtils.parseRecordScript(args, pred);
		return setNextFactory(new FilterFactory(script));
	}
	
	public ProjectFactory project(String colExpr) {
		return setNextFactory(new ProjectFactory(colExpr));
	}
	
	public DefineColumnFactory defineColumn(String colName) {
		return setNextFactory(new DefineColumnFactory(colName));
	}
	
	public UpdateFactory update(String expr) {
		RecordScript script = RecordScript.of(expr);
		return setNextFactory(new UpdateFactory(script));
	}
	
	public UpdateFactory update(Map<String,Object> args, String pred) {
		String initializer = ScriptUtils.getOrThrow(args, "initializer").toString();
		RecordScript script = RecordScript.of(initializer, pred);
		return setNextFactory(new UpdateFactory(script));
	}
	
	public ExpandFactory expand(String colDecls) {
		return setNextFactory(new ExpandFactory(colDecls));
	}
	
	public TakeFactory take(long count) {
		return setNextFactory(new TakeFactory(count));
	}
	
	public ListByGroupFactory list(Keyword all) {
		return setNextFactory(new ListByGroupFactory());
	}
	
	public PickTopKFactory pickTop(int count) {
		return setNextFactory(new PickTopKFactory(count));
	}
	
	public SortFactory sort(String sortColsExpr) {
		return setNextFactory(new SortFactory(sortColsExpr));
	}
	
	public DistinctFactory distinct(String keyCols) {
		return setNextFactory(new DistinctFactory(keyCols));
	}
	
	public AssignUidFactory assignUid(String colName) {
		return setNextFactory(new AssignUidFactory(colName));
	}
	
	public SampleFactory sample(double ratio) {
		return setNextFactory(new SampleFactory(ratio));
	}
	
	public ShardFactory shard(int count) {
		return setNextFactory(new ShardFactory(count));
	}
	
	public AggregateFactory aggregate(Closure aggrsDecl) {
		return setNextFactory(new AggregateFactory(aggrsDecl));
	}
	
	public ParseCsvFactory parseCsv(String colName) {
		return setNextFactory(new ParseCsvFactory(colName));
	}
	
	public HashJoinFactory hashJoin(String joinCols, String paramDsId, String paramJoinCols) {
		return setNextFactory(new HashJoinFactory(joinCols, paramDsId, paramJoinCols));
	}
	
	public LoadHashJoinFactory hashJoinFile(Closure decl) {
		return new LoadHashJoinFactory(decl);
	}
	
	public BufferFactory buffer(Column geomCol) {
		return setNextFactory(new BufferFactory(geomCol.getName()));
	}
	
	public BufferFactory buffer(String geomCol) {
		return setNextFactory(new BufferFactory(geomCol));
	}
	
	public CentroidFactory centroid(String geomCol) {
		return setNextFactory(new CentroidFactory(geomCol));
	}
	
	public CentroidFactory centroid(Column geomCol) {
		return centroid(geomCol.getName());
	}
	
	public ToXyCoordinatesFactory toXY(String geomCol) {
		return setNextFactory(new ToXyCoordinatesFactory(geomCol));
	}
	
	public ToXyCoordinatesFactory toXY(Column geomCol) {
		return toXY(geomCol.getName());
	}
	
	public TransformCrsFactory transformCrs(String geomCol) {
		return setNextFactory(new TransformCrsFactory(geomCol));
	}
	public TransformCrsFactory transformCrs(Column geomCol) {
		return transformCrs(geomCol.getName());
	}
	
	public ToPointFactory toPoint(String geomCol) {
		return setNextFactory(new ToPointFactory(geomCol));
	}
	public ToPointFactory toPoint(Column geomCol) {
		return toPoint(geomCol.getName());
	}
	
	public FilterSpatiallyFactory filter(Keyword keyword) {
		switch ( keyword.getLiteral() ) {
			case "spatially":
				return setNextFactory(new FilterSpatiallyFactory());
		}
		
		throw new IllegalArgumentException("unexpected keyword: " + keyword);
	}
	
	public QueryDataSetFactory query(String dsId) {
		return setNextFactory(new QueryDataSetFactory(dsId));
	}
	
	public SpatialJoinFactory spatialJoin(String paramDsId) {
		return setNextFactory(new SpatialJoinFactory(paramDsId));
	}
	
	public SpatialSemiJoinFactory spatialSemiJoin(String paramDsId) {
		return setNextFactory(new SpatialSemiJoinFactory(paramDsId));
	}
	
	public SpatialOuterJoinFactory spatialOuterJoin(String paramDsId) {
		return setNextFactory(new SpatialOuterJoinFactory(paramDsId));
	}
	
	public LoadSquareGridFactory load(SquareGrid grid) {
		return setNextFactory(new LoadSquareGridFactory(grid));
	}
	
	public AssignSquareGridFactory assign(SquareGrid grid) {
		return setNextFactory(new AssignSquareGridFactory(grid));
	}
	
	public StoreAsCsvFactory storeAsCsv(String path) {
		return setNextFactory(new StoreAsCsvFactory(path));
	}
	
	public void createLatest() {
		if ( m_lastFact != null ) {
			m_builder.add(m_lastFact.create());
			m_lastFact = null;
		}
	}
	
	protected <T extends OperatorFactory> T setNextFactory(T fact) {
		createLatest();
		
		m_lastFact = fact;
		return fact;
	}
}
