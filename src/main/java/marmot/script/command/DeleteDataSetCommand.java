package marmot.script.command;

import marmot.MarmotRuntime;
import marmot.script.GroovyDslClass;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class DeleteDataSetCommand extends GroovyDslClass implements ScriptCommand<Void> {
	private final MarmotRuntime m_marmot;
	private String m_dsId;
	
	public DeleteDataSetCommand(MarmotRuntime marmot, String dsId) {
		m_marmot = marmot;
		m_dsId = dsId;
	}

	@Override
	public Void execute() {
		try {
			m_marmot.getDataSet(m_dsId);
			m_marmot.deleteDataSet(m_dsId);
		}
		catch ( Exception e ) {
			m_marmot.deleteDir(m_dsId);
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return String.format("delete dataset(%s)", m_dsId);
	}
}
