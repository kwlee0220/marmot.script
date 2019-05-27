package marmot.script.command;

import java.util.Map;

import marmot.MarmotRuntime;
import marmot.script.GroovyDslClass;
import utils.stream.KVFStream;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ExecuteProcessCommand extends GroovyDslClass
									implements ScriptCommand<Void> {
	private final MarmotRuntime m_marmot;
	private final String m_procName;
	private final Map<String,String> m_params;
	
	public ExecuteProcessCommand(MarmotRuntime marmot, String procName, Map<String,Object> params) {
		m_marmot = marmot;
		m_procName = procName;
		m_params = KVFStream.of(params).mapValue(v -> v.toString()).toMap();
	}

	@Override
	public Void execute() {
		m_marmot.executeProcess(m_procName, m_params);
		return null;
	}
	
	@Override
	public String toString() {
		return String.format("executeProcess: %s(%s)", m_procName, m_params);
	}
}
