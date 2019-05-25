package marmot.script;

import java.util.Map;

import com.google.common.collect.Maps;

import groovy.lang.Closure;
import marmot.DataSet;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.script.command.ClusterDataSetCommand;
import marmot.script.command.CreateDataSetCommand;
import marmot.script.command.DeleteDataSetCommand;
import marmot.script.command.ImportCsvFileCommand;
import marmot.script.command.ImportExcelFileCommand;
import marmot.script.command.ImportShapefileCommand;
import marmot.script.command.MoveDataSetCommand;
import marmot.script.command.RunPlanCommand;
import marmot.script.command.ScriptCommand;
import utils.StopWatch;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class CommandScriptRunner extends GroovyDslClass {
	private final MarmotRuntime m_marmot;
	private final boolean m_verbose;
	
	public CommandScriptRunner(MarmotRuntime marmot, boolean verbose) {
		m_marmot = marmot;
		m_verbose = verbose;
	}
	
	public DataSet createDataset(Map<String,Object> args, String dsId) {
		CreateDataSetCommand cmd = new CreateDataSetCommand(m_marmot, dsId, args);
		return execute(cmd);
	}
	public DataSet createDataset(String dsId, Closure optDecl) {
		CreateDataSetCommand cmd = new CreateDataSetCommand(m_marmot, dsId, Maps.newHashMap());
		ScriptUtils.callClosure(optDecl, cmd);
		return execute(cmd);
	}
	
	public Void deleteDataset(String... dsId) {
		DeleteDataSetCommand cmd = new DeleteDataSetCommand(m_marmot, dsId);
		return execute(cmd);
	}
	
	public Void moveDataset(String srcDsId, String dstDsId) {
		MoveDataSetCommand cmd = new MoveDataSetCommand(m_marmot, srcDsId, dstDsId);
		return execute(cmd);
	}
	
	public Void clusterDataset(String dsId) {
		ClusterDataSetCommand cmd = new ClusterDataSetCommand(m_marmot, dsId);
		return execute(cmd);
	}
	
	public Void run(Plan plan) {
		RunPlanCommand cmd = new RunPlanCommand(m_marmot, plan);
		return execute(cmd);
	}
	
	public long importShapefile(Map<String,Object> args, String shpPath,
												String dsId) {
		ImportShapefileCommand cmd = new ImportShapefileCommand(m_marmot, shpPath, dsId, args);
		return execute(cmd);
	}
	public long importShapefile(String shpPath, String dsId, Closure optDecl) {
		return importShapefile(ScriptUtils.options(optDecl), shpPath, dsId);
	}
	
	public long importCsvFile(String csvPath, String dsId) {
		ImportCsvFileCommand cmd = new ImportCsvFileCommand(m_marmot, csvPath, dsId);
		return execute(cmd);
	}
	
	public long importExcelFile(Map<String,Object> args, String excelPath,
								String dsId) {
		return new ImportExcelFileCommand(m_marmot, excelPath, dsId, args).execute();
	}
	public long importExcelFile(String shpPath, String dsId, Closure optDecl) {
		return importExcelFile(ScriptUtils.options(optDecl), shpPath, dsId);
	}
	
	public Plan plan(String name, Closure script) {
		return ScriptUtils.parsePlan(name, script);
	}
	
	public <T> T execute(ScriptCommand<T> cmd) {
		if ( m_verbose ) {
			System.out.printf("%s...", cmd);
		}
		StopWatch watch = StopWatch.start();
		
		try {
			T ret = cmd.execute();
			if ( m_verbose ) {
				System.out.printf(" -> done: %s (elapsed=%s)%n",
									ret, watch.getElapsedSecondString());
			}
			
			return ret;
		}
		catch ( Exception e ) {
			if ( m_verbose ) {
				System.out.printf(" -> cause=%s%n", cmd, e);
			}
			throw e;
		}
	}
}
