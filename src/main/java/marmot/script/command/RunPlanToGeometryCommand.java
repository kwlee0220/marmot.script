package marmot.script.command;

import com.vividsolutions.jts.geom.Geometry;

import marmot.ExecutePlanOptions;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.script.GroovyDslClass;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class RunPlanToGeometryCommand extends GroovyDslClass implements ScriptCommand<Geometry> {
	private final MarmotRuntime m_marmot;
	private final Plan m_plan;
	private final ExecutePlanOptions m_opts;
	
	public RunPlanToGeometryCommand(MarmotRuntime marmot, Plan plan, ExecutePlanOptions opts) {
		m_marmot = marmot;
		m_plan = plan;
		m_opts = opts;
	}

	@Override
	public Geometry execute() {
		return m_marmot.executeToGeometry(m_plan, m_opts).getOrNull();
	}
	
	@Override
	public String toString() {
		return String.format("run plan '%s'", m_plan != null ? m_plan.getName() : "unknown");
	}
}
