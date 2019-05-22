package marmot.script.command;

import marmot.Plan;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class AppendDataSetCommand implements MarmotScriptCommand {
	private String m_dsId;
	private Plan m_plan;
	
	public AppendDataSetCommand() {
	}
	
	public AppendDataSetCommand dataset(String dsId) {
		m_dsId = dsId;
		return this;
	}
	
	public AppendDataSetCommand from(Plan plan) {
		m_plan = plan;
		return this;
	}

	@Override
	public void execute() {
	}
}
