package marmot.script.command;

import marmot.MarmotRuntime;
import marmot.dataset.DataSet;
import marmot.geo.catalog.SpatialIndexInfo;
import marmot.script.GroovyDslClass;
import marmot.script.command.ClusterDataSetCommand.ClusterReport;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ClusterDataSetCommand extends GroovyDslClass
									implements ScriptCommand<ClusterReport> {
	private final MarmotRuntime m_marmot;
	private final String m_dsId;
	
	public ClusterDataSetCommand(MarmotRuntime marmot, String dsId) {
		m_marmot = marmot;
		m_dsId = dsId;
	}

	@Override
	public ClusterReport execute() {
		DataSet ds = m_marmot.getDataSet(m_dsId);
		SpatialIndexInfo idxInfo = ds.createSpatialIndex();
		
		return new ClusterReport(this, idxInfo);
	}
	
	@Override
	public String toString() {
		return String.format("cluster dataset '%s'", m_dsId);
	}
	
	public static class ClusterReport implements CommandReport {
		private final ClusterDataSetCommand m_cmd;
		private final SpatialIndexInfo m_idxInfo;
		
		ClusterReport(ClusterDataSetCommand cmd, SpatialIndexInfo idxInfo) {
			m_cmd = cmd;
			m_idxInfo = idxInfo;
		}

		@Override
		public String toString() {
			return String.format("clustered: nclusters=%d nrecords=%d, non-duplicated=%d",
								m_idxInfo.getClusterCount(), m_idxInfo.getRecordCount(),
								m_idxInfo.getNonDuplicatedRecordCount());
		}
	}
}
