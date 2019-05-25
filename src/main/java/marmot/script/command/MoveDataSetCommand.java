package marmot.script.command;

import marmot.MarmotRuntime;
import marmot.script.GroovyDslClass;
import utils.Utilities;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class MoveDataSetCommand extends GroovyDslClass
								implements ScriptCommand<Void> {
	private final MarmotRuntime m_marmot;
	private final String m_srcDsId;
	private String m_destDsId;
	
	public MoveDataSetCommand(MarmotRuntime marmot, String srcDsId, String dstDsId) {
		m_marmot = marmot;
		m_srcDsId = srcDsId;
		m_destDsId = dstDsId;
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
	public Void execute() {
		Utilities.checkNotNullArgument(m_destDsId, "destination dataset has not been set");
		
		m_marmot.moveDataSet(m_srcDsId, m_destDsId);
		return null;
	}
	
	@Override
	public String toString() {
		return String.format("move dataset '%s' to '%s'",
							m_srcDsId, m_destDsId != null ? m_destDsId : "unknown");
	}
}
