package marmot.script.command;

import marmot.MarmotRuntime;
import marmot.script.GroovyDslClass;
import marmot.script.dslobj.GProcess;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class RunProcessCommand extends GroovyDslClass
									implements ScriptCommand<Void> {
	private final MarmotRuntime m_marmot;
	private final GProcess m_proc;
	
	public RunProcessCommand(MarmotRuntime marmot, GProcess proc) {
		m_marmot = marmot;
		m_proc = proc;
	}

	@Override
	public Void execute() {
		m_marmot.executeProcess(m_proc.getName(), m_proc.getParameters());
		return null;
	}
	
	@Override
	public String toString() {
		return String.format("run process %s(%s)", m_proc.getName(), m_proc.getParameters());
	}
}
