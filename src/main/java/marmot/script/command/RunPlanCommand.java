package marmot.script.command;

import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.script.GroovyDslClass;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class RunPlanCommand extends GroovyDslClass implements MarmotScriptCommand {
	private final MarmotRuntime m_marmot;
	private final Plan m_plan;
	
	public RunPlanCommand(MarmotRuntime marmot, Plan plan) {
		m_marmot = marmot;
		m_plan = plan;
	}

	@Override
	public void execute() {
		m_marmot.execute(m_plan);
	}
	
	@Override
	public String toString() {
		return String.format("run plan '%s'", m_plan != null ? m_plan.getName() : "unknown");
	}
}
