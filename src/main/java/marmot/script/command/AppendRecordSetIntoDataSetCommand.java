package marmot.script.command;

import java.util.Map;

import groovy.lang.Closure;
import marmot.ExecutePlanOptions;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.dataset.DataSet;
import marmot.script.GroovyDslClass;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class AppendRecordSetIntoDataSetCommand extends GroovyDslClass
												implements ScriptCommand<DataSet> {
	private final MarmotRuntime m_marmot;
	private final String m_dsId;
	private final ExecutePlanOptions m_options;
	private Plan m_plan;
	
	public AppendRecordSetIntoDataSetCommand(MarmotRuntime marmot, String dsId,
											Map<String,Object> args) {
		m_marmot = marmot;
		m_dsId = dsId;
		
		m_options = ScriptUtils.parseExecutePlanOptions(args);
		ScriptUtils.getOption(args, "from")
					.cast(Plan.class)
					.ifPresent(p -> m_plan = p);
	}
	
	@Override
	public Object getProperty(String name) {
		switch ( name ) {
			case "plan":
				return name;
		}
		
		return super.getProperty(name);
	}
	
	public Plan plan(String name, Closure script) {
		return ScriptUtils.parsePlan(m_marmot, name, script);
	}
	
	@Override
	public Object invokeMethod(String name, Object args) {
		switch ( name ) {
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
		throw new AssertionError();
//		if ( m_plan != null ) {
//			DataSet ds = m_marmot.getDataSet(m_dsId);
//			ds.appendPlanResult(m_plan, m_options);
//			
//			return m_marmot.getDataSet(m_dsId);
//		}
//		else {
//			throw new IllegalStateException("cannot run " + this);
//		}
	}
	
	@Override
	public String toString() {
		return String.format("appendIntoDataSet %s from plan(%s)",
							m_dsId, m_plan.getName());
	}
}
