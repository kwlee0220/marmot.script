package marmot.script.command;

import java.util.Map;

import groovy.lang.Closure;
import marmot.DataSet;
import marmot.GeometryColumnInfo;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.RecordSchema;
import marmot.StoreDataSetOptions;
import marmot.script.GroovyDslClass;
import marmot.script.ScriptUtils;
import marmot.script.dslobj.RecordSchemaParser;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class CreateDataSetCommand extends GroovyDslClass
									implements ScriptCommand<DataSet> {
	private final MarmotRuntime m_marmot;
	private final String m_dsId;
	private StoreDataSetOptions m_options;
	private RecordSchema m_schema;
	private Plan m_plan;
	
	public CreateDataSetCommand(MarmotRuntime marmot, String dsId, Map<String,Object> args) {
		m_marmot = marmot;
		m_dsId = dsId;
		
		ScriptUtils.getOption(args, "from")
					.cast(Plan.class)
					.ifPresent(p -> m_plan = p);
		ScriptUtils.getOption(args, "schema")
					.cast(RecordSchema.class)
					.ifPresent(s -> m_schema = s);
		m_options = ScriptUtils.parseStoreDataSetOptions(args);
	}
	
	@Override
	public Object getProperty(String name) {
		switch ( name ) {
			case "force":
				m_options = m_options.force(true);
				return this;
			case "plan":
			case "multi_polygon":
				return name;
		}
		
		return super.getProperty(name);
	}
	
	@Override
	public void setProperty(String name, Object value) {
		switch ( name ) {
			case "force":
				if ( (boolean)value ) {
					m_options = m_options.force(true);
				}
				return;
		}
		
		super.setProperty(name, value);
	}
	
	public CreateDataSetCommand geometry(String str) {
		GeometryColumnInfo gcInfo = GeometryColumnInfo.fromString(str);
		m_options = m_options.geometryColumnInfo(gcInfo);
		
		return this;
	}
	
	public Plan plan(String name, Closure script) {
		return ScriptUtils.parsePlan(m_marmot, name, script);
	}
	
	public void schema(Closure decl) {
		RecordSchemaParser parser = new RecordSchemaParser();
		ScriptUtils.callClosure(decl, parser);
		m_schema = parser.parse();
	}
	
	@Override
	public Object invokeMethod(String name, Object args) {
		switch ( name ) {
			case "options":
				ScriptUtils.callClosure(getArgs(args, 0), this);
				return this;
			case "from":
				Object planObj = getArgs(args, 0);
				if ( planObj instanceof Plan ) {
					m_plan = (Plan)planObj;
				}
				return this;
		}
		
		return super.invokeMethod(name, args);
	}

	@Override
	public DataSet execute() {
		if ( m_plan != null ) {
			return m_marmot.createDataSet(m_dsId, m_plan, m_options);
		}
		else if ( m_schema != null) {
			return m_marmot.createDataSet(m_dsId, m_schema, m_options);
		}
		else {
			throw new IllegalStateException("cannot run " + this);
		}
	}
	
	@Override
	public String toString() {
		String gcInfoStr = m_options.geometryColumnInfo()
									.map(gcInfo -> String.format("[%s]", gcInfo))
									.getOrElse("");
		if ( m_plan != null ) {
			return String.format("createDataset %s%s from plan(%s)",
								m_dsId, gcInfoStr, m_plan.getName());
		}
		else if ( m_schema != null) {
			return String.format("createDataset %s%s, schema: %s",
									m_dsId, gcInfoStr, m_schema);
		}
		else {
			return String.format("createDataset %s%s, undefined",
								m_dsId, gcInfoStr);
		}
	}
}
