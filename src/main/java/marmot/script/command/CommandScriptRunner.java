package marmot.script.command;

import groovy.lang.Closure;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.script.GroovyDslClass;
import marmot.script.ScriptUtils;
import utils.StopWatch;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class CommandScriptRunner extends GroovyDslClass {
	private final MarmotRuntime m_marmot;
	private final boolean m_verbose;
	private MarmotScriptCommand<?> m_lastFact = null;
	
	public CommandScriptRunner(MarmotRuntime marmot, boolean verbose) {
		m_marmot = marmot;
		m_verbose = verbose;
	}
	
	@Override
	public Object getProperty(String name) {
		switch ( name ) {
			case "to":
			case "plan":
				return name;
		}
		
		return super.getProperty(name);
	}
	
	public CreateDataSetCommand createDataset(String dsId) {
		return setNextFactory(new CreateDataSetCommand(m_marmot, dsId));
	}
	
	public DeleteDataSetCommand deleteDataset(String... dsId) {
		return setNextFactory(new DeleteDataSetCommand(m_marmot, dsId));
	}
	
	public MoveDataSetCommand moveDataset(String dsId) {
		return setNextFactory(new MoveDataSetCommand(m_marmot, dsId));
	}
	
	public ClusterDataSetCommand clusterDataset(String dsId) {
		return setNextFactory(new ClusterDataSetCommand(m_marmot, dsId));
	}
	
	public RunPlanCommand run(Plan plan) {
		return setNextFactory(new RunPlanCommand(m_marmot, plan));
	}
	
	public ImportShapefileCommand importShapefile(String shpPath) {
		return setNextFactory(new ImportShapefileCommand(m_marmot, shpPath));
	}
	
	public ImportCsvFileCommand importCsvFile(String csvPath) {
		return setNextFactory(new ImportCsvFileCommand(m_marmot, csvPath));
	}
	
	public ImportExcelFileCommand importExcelFile(String excelPath) {
		return setNextFactory(new ImportExcelFileCommand(m_marmot, excelPath));
	}
	
	public Plan plan(String name, Closure script) {
		return ScriptUtils.parsePlan(name, script);
	}
	
	public void runLatest() {
		if ( m_lastFact != null ) {
			if ( m_verbose ) {
				System.out.printf("starting: %s%n", m_lastFact);
			}
			StopWatch watch = StopWatch.start();
			
			try {
				Object ret = m_lastFact.execute();
				if ( m_verbose ) {
					System.out.printf("done: output=%s, elapsed=%s%n",
										ret, watch.getElapsedSecondString());
				}
			}
			catch ( Exception e ) {
				if ( m_verbose ) {
					System.out.printf("failed: %s, cause=%s%n", m_lastFact, e);
				}
				throw e;
			}
			
			m_lastFact = null;
		}
	}
	
	public void run() {
		runLatest();
	}
	
	protected <T extends MarmotScriptCommand> T setNextFactory(T fact) {
		runLatest();
		
		m_lastFact = fact;
		return fact;
	}
}
