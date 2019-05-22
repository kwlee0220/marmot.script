package marmot.script.command;

import marmot.DataSet;
import marmot.MarmotRuntime;
import marmot.geo.command.ClusterDataSetOptions;
import marmot.script.GroovyDslClass;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ClusterDataSetCommand extends GroovyDslClass implements MarmotScriptCommand {
	private final MarmotRuntime m_marmot;
	private final String m_dsId;
	private final ClusterDataSetOptions m_options = new ClusterDataSetOptions();
	
	public ClusterDataSetCommand(MarmotRuntime marmot, String dsId) {
		m_marmot = marmot;
		m_dsId = dsId;
	}
	
	@Override
	public Object getProperty(String name) {
		
		return super.getProperty(name);
	}

	@Override
	public void execute() {
		DataSet ds = m_marmot.getDataSet(m_dsId);
//		ds.cluster(m_options);
		ds.cluster();
	}
	
	@Override
	public String toString() {
		return String.format("cluster_dataset '%s'", m_dsId);
	}
}
