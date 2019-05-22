package marmot.script.command;

import java.util.ArrayList;
import java.util.List;

import groovy.lang.Closure;
import marmot.DataSetOption;
import marmot.GeometryColumnInfo;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.RecordSchema;
import marmot.script.GroovyDslClass;
import marmot.script.ScriptUtils;
import marmot.script.dslobj.RecordSchemaParser;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class CreateDataSetCommand extends GroovyDslClass implements MarmotScriptCommand {
	private final MarmotRuntime m_marmot;
	private final String m_dsId;
	private final List<DataSetOption> m_options = new ArrayList<>();
	private RecordSchema m_schema;
	private Plan m_plan;
	
	public CreateDataSetCommand(MarmotRuntime marmot, String dsId) {
		m_marmot = marmot;
		m_dsId = dsId;
	}
	
	@Override
	public Object getProperty(String name) {
		switch ( name ) {
			case "force":
				m_options.add(DataSetOption.FORCE);
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
					m_options.add(DataSetOption.FORCE);
				}
				return;
		}
		
		super.setProperty(name, value);
	}
	
	public CreateDataSetCommand geometry(String str) {
		GeometryColumnInfo gcInfo = GeometryColumnInfo.fromString(str);
		m_options.add(DataSetOption.GEOMETRY(gcInfo));
		
		return this;
	}
	
	public Plan plan(String name, Closure script) {
		return ScriptUtils.parsePlan(name, script);
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
	public void execute() {
		DataSetOption[] opts = m_options.toArray(new DataSetOption[0]);
		if ( m_plan != null ) {
			m_marmot.createDataSet(m_dsId, m_plan, opts);
		}
		else if ( m_schema != null) {
			m_marmot.createDataSet(m_dsId, m_schema, opts);
		}
		else {
			throw new IllegalStateException("cannot run " + this);
		}
	}
	
	@Override
	public String toString() {
		String gcInfoStr = DataSetOption.getGeometryColumnInfo(m_options)
										.map(gcInfo -> String.format("[%s]", gcInfo))
										.getOrElse("");
		if ( m_plan != null ) {
			return String.format("create dataset %s%s from plan(%s)",
								m_dsId, gcInfoStr, m_plan.getName());
		}
		else if ( m_schema != null) {
			return String.format("create dataset %s%s, schema: %s",
									m_dsId, gcInfoStr, m_schema);
		}
		else {
			return String.format("create dataset %s%s, undefined",
								m_dsId, gcInfoStr);
		}
	}
}
