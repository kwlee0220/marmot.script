package marmot.script.command;

import marmot.ExecutePlanOptions;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.script.GroovyDslClass;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class RunPlanToLongCommand extends GroovyDslClass implements ScriptCommand<Long> {
	private final MarmotRuntime m_marmot;
	private final Plan m_plan;
	private final ExecutePlanOptions m_opts;
	
	public RunPlanToLongCommand(MarmotRuntime marmot, Plan plan, ExecutePlanOptions opts) {
		m_marmot = marmot;
		m_plan = plan;
		m_opts = opts;
	}

	@Override
	public Long execute() {
		return m_marmot.executeToLong(m_plan, m_opts).getOrNull();
	}
	
	@Override
	public String toString() {
		return String.format("%s '%s'", getClass().getSimpleName(),
							m_plan != null ? m_plan.getName() : "unknown");
	}
}
