package marmot.script.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import marmot.DataSet;
import marmot.ExecutePlanOptions;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.StoreDataSetOptions;
import marmot.proto.optor.StoreDataSetProto;
import marmot.script.GroovyDslClass;
import marmot.script.command.RunPlanCommand.RunPlanReport;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class RunPlanCommand extends GroovyDslClass implements ScriptCommand<RunPlanReport> {
	private static final Logger s_logger = LoggerFactory.getLogger(RunPlanCommand.class);
	
	private final MarmotRuntime m_marmot;
	private final Plan m_plan;
	private final ExecutePlanOptions m_opts;
	
	public RunPlanCommand(MarmotRuntime marmot, Plan plan) {
		this(marmot, plan, ExecutePlanOptions.DEFAULT);
	}
	
	public RunPlanCommand(MarmotRuntime marmot, Plan plan, ExecutePlanOptions opts) {
		m_marmot = marmot;
		m_plan = plan;
		m_opts  = opts;
	}

	@Override
	public RunPlanReport execute() {
		m_marmot.execute(m_plan, m_opts);

		RunPlanReport report = new RunPlanReport();
		if ( s_logger.isInfoEnabled() ) {
			m_plan.getLastOperator().ifPresent(last -> {
				switch ( last.getOperatorCase() ) {
					case STORE_DATASET:
						log(last.getStoreDataset());
					default:
						break;
				}
			});
		}
		
		return report;
	}
	
	@Override
	public String toString() {
		return String.format("run plan '%s'", m_plan != null ? m_plan.getName() : "unknown");
	}
	
	private void log(StoreDataSetProto store) {
		DataSet ds = m_marmot.getDataSet(store.getId());
		
		StoreDataSetOptions opts = StoreDataSetOptions.fromProto(store.getOptions());
		String cmdStr = opts.append().getOrElse(false) ? "append" : "create";
		String geomStr = ds.hasGeometryColumn() ? ", " + ds.getGeometryColumnInfo() : "";
		String msg = String.format("%s: %s%s, nrecords=%d", cmdStr, ds.getId(), geomStr,
															ds.getRecordCount());
		s_logger.info(msg);
	}
	
	public class RunPlanReport implements CommandReport {
		@Override
		public String toString() {
			return m_plan.getLastOperator().map(last -> {
				switch ( last.getOperatorCase() ) {
					case STORE_DATASET:
						return reportStore(last.getStoreDataset());
					default:
						return "";
				}
			}).getOrElse("");
		}
		
		private String reportStore(StoreDataSetProto store) {
			DataSet ds = m_marmot.getDataSet(store.getId());
			
			StoreDataSetOptions opts = StoreDataSetOptions.fromProto(store.getOptions());
			String cmdStr = opts.append().getOrElse(false) ? "append" : "create";
			String geomStr = ds.hasGeometryColumn() ? ", " + ds.getGeometryColumnInfo() : "";
			return String.format("%s: %s%s, nrecords=%d", cmdStr, ds.getId(), geomStr, ds.getRecordCount());
		}
	}
}
