package marmot.script.command;

import marmot.DataSet;
import marmot.MarmotRuntime;
import marmot.script.GroovyDslClass;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ClusterDataSetCommand extends GroovyDslClass
									implements ScriptCommand<Void> {
	private final MarmotRuntime m_marmot;
	private final String m_dsId;
	
	public ClusterDataSetCommand(MarmotRuntime marmot, String dsId) {
		m_marmot = marmot;
		m_dsId = dsId;
	}

	@Override
	public Void execute() {
		DataSet ds = m_marmot.getDataSet(m_dsId);
		ds.cluster();
		
		return null;
	}
	
	@Override
	public String toString() {
		return String.format("clusterDataset '%s'", m_dsId);
	}
}
