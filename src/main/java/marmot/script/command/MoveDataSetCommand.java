package marmot.script.command;

import marmot.MarmotRuntime;
import marmot.script.GroovyDslClass;
import utils.Utilities;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class MoveDataSetCommand extends GroovyDslClass implements MarmotScriptCommand {
	private final MarmotRuntime m_marmot;
	private final String m_dsId;
	private String m_destDsId;
	
	public MoveDataSetCommand(MarmotRuntime marmot, String dsId) {
		m_marmot = marmot;
		m_dsId = dsId;
	}
	
	public MoveDataSetCommand to(String destDsId) {
		m_destDsId = destDsId;
		return this;
	}
	
	@Override
	public Object getProperty(String name) {
		return super.getProperty(name);
	}
	
	@Override
	public Object invokeMethod(String name, Object args) {
		return super.invokeMethod(name, args);
	}

	@Override
	public void execute() {
		Utilities.checkNotNullArgument(m_destDsId, "destination dataset has not been set");
		
		m_marmot.moveDataSet(m_dsId, m_destDsId);
	}
	
	@Override
	public String toString() {
		return String.format("move dataset '%s' to '%s'",
							m_dsId, m_destDsId != null ? m_destDsId : "unknown");
	}
}
