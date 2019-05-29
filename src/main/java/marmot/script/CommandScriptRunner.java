package marmot.script;

import java.util.Map;

import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Geometry;

import groovy.lang.Closure;
import marmot.DataSet;
import marmot.ExecutePlanOptions;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.Record;
import marmot.RecordSet;
import marmot.script.command.AppendIntoDataSetCommand;
import marmot.script.command.ClusterDataSetCommand;
import marmot.script.command.CreateDataSetCommand;
import marmot.script.command.DeleteDataSetCommand;
import marmot.script.command.ExecuteProcessCommand;
import marmot.script.command.ExportDataSetAsShapefileCommand;
import marmot.script.command.ImportCsvFileCommand;
import marmot.script.command.ImportExcelFileCommand;
import marmot.script.command.ImportShapefileCommand;
import marmot.script.command.MoveDataSetCommand;
import marmot.script.command.RunPlanCommand;
import marmot.script.command.RunPlanToGeometry;
import marmot.script.command.RunPlanToLong;
import marmot.script.command.RunPlanToRecord;
import marmot.script.command.RunPlanToRecordSetCommand;
import marmot.script.command.RunPlanToString;
import marmot.script.command.ScriptCommand;
import utils.StopWatch;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class CommandScriptRunner extends ScriptParsingObject {
	private final boolean m_verbose;
	
	public CommandScriptRunner(MarmotRuntime marmot, boolean verbose) {
		super(marmot);
		
		m_verbose = verbose;
	}
	
	public DataSet createDataSet(Map<String,Object> args, String dsId) throws Exception {
		CreateDataSetCommand cmd = new CreateDataSetCommand(m_marmot, dsId, args);
		return execute(cmd);
	}
	public DataSet createDataSet(String dsId, Closure<?> optDecl) throws Exception {
		CreateDataSetCommand cmd = new CreateDataSetCommand(m_marmot, dsId, Maps.newHashMap());
		ScriptUtils.callClosure(optDecl, cmd);
		return execute(cmd);
	}
	
	public DataSet appendIntoDataSet(String dsId, Closure<?> optDecl) throws Exception {
		AppendIntoDataSetCommand cmd = new AppendIntoDataSetCommand(m_marmot, dsId, Maps.newHashMap());
		ScriptUtils.callClosure(optDecl, cmd);
		return execute(cmd);
	}
	
	public Void deleteDataSet(String... dsId) throws Exception {
		DeleteDataSetCommand cmd = new DeleteDataSetCommand(m_marmot, dsId);
		return execute(cmd);
	}
	
	public Void moveDataSet(String srcDsId, String dstDsId) throws Exception {
		MoveDataSetCommand cmd = new MoveDataSetCommand(m_marmot, srcDsId, dstDsId);
		return execute(cmd);
	}
	
	public Void clusterDataSet(String dsId) throws Exception {
		ClusterDataSetCommand cmd = new ClusterDataSetCommand(m_marmot, dsId);
		return execute(cmd);
	}
	
	public Void run(Plan plan) throws Exception {
		RunPlanCommand cmd = new RunPlanCommand(m_marmot, plan);
		return execute(cmd);
	}
	
	public RecordSet runPlanToRecordSet(Map<String,Object> args, Plan plan) throws Exception {
		ExecutePlanOptions opts = ScriptUtils.parseExecutePlanOptions(args);
		RunPlanToRecordSetCommand cmd = new RunPlanToRecordSetCommand(m_marmot, plan, opts);
		return execute(cmd);
	}
	public RecordSet runPlanToRecordSet(Plan plan) throws Exception {
		return runPlanToRecordSet(Maps.newHashMap(), plan);
	}
	
	public Record runPlanToRecord(Map<String,Object> args, Plan plan) throws Exception {
		ExecutePlanOptions opts = ScriptUtils.parseExecutePlanOptions(args);
		RunPlanToRecord cmd = new RunPlanToRecord(m_marmot, plan, opts);
		return execute(cmd);
	}
	public Record runPlanToRecord(Plan plan) throws Exception {
		return runPlanToRecord(Maps.newHashMap(), plan);
	}
	
	public Geometry runPlanToGeometry(Map<String,Object> args, Plan plan) throws Exception {
		ExecutePlanOptions opts = ScriptUtils.parseExecutePlanOptions(args);
		RunPlanToGeometry cmd = new RunPlanToGeometry(m_marmot, plan, opts);
		return execute(cmd);
	}
	public Geometry runPlanToGeometry(Plan plan) throws Exception {
		return runPlanToGeometry(Maps.newHashMap(), plan);
	}
	
	public Long runPlanToLong(Map<String,Object> args, Plan plan) throws Exception {
		ExecutePlanOptions opts = ScriptUtils.parseExecutePlanOptions(args);
		RunPlanToLong cmd = new RunPlanToLong(m_marmot, plan, opts);
		return execute(cmd);
	}
	public Long runPlanToLong(Plan plan) throws Exception {
		return runPlanToLong(Maps.newHashMap(), plan);
	}
	
	public String runPlanToString(Map<String,Object> args, Plan plan) throws Exception {
		ExecutePlanOptions opts = ScriptUtils.parseExecutePlanOptions(args);
		RunPlanToString cmd = new RunPlanToString(m_marmot, plan, opts);
		return execute(cmd);
	}
	public String runPlanToString(Plan plan) throws Exception {
		return runPlanToString(Maps.newHashMap(), plan);
	}
	
	public long importShapefile(Map<String,Object> args, String shpPath,
												String dsId) throws Exception {
		ImportShapefileCommand cmd = new ImportShapefileCommand(m_marmot, shpPath, dsId, args);
		return execute(cmd);
	}
	public long importShapefile(String shpPath, String dsId, Closure<?> optDecl) throws Exception {
		return importShapefile(ScriptUtils.options(optDecl), shpPath, dsId);
	}
	
	public long exportDataSetToShapefile(Map<String,Object> args, String dsId,
										String shpPath, Closure<?> optDecl)
		throws Exception {
		ExportDataSetAsShapefileCommand cmd
								= new ExportDataSetAsShapefileCommand(m_marmot, dsId, shpPath,
																		args, optDecl);
		return execute(cmd);
	}
	public long exportDataSetToShapefile(Map<String,Object> args, String dsId,
										String shpPath) throws Exception {
		return exportDataSetToShapefile(args, dsId, shpPath, null);
	}
	public long exportDataSetToShapefile(String dsId, String shpPath, Closure<?> optDecl)
		throws Exception {
		ExportDataSetAsShapefileCommand cmd
						= new ExportDataSetAsShapefileCommand(m_marmot, dsId, shpPath,
																Maps.newHashMap(), optDecl);
		return execute(cmd);
	}
	
	public long importCsvFile(String csvPath, String dsId) throws Exception {
		ImportCsvFileCommand cmd = new ImportCsvFileCommand(m_marmot, csvPath, dsId);
		return execute(cmd);
	}
	
	public long importExcelFile(Map<String,Object> args, String excelPath,
								String dsId) {
		return new ImportExcelFileCommand(m_marmot, excelPath, dsId, args).execute();
	}
	public long importExcelFile(String shpPath, String dsId, Closure<?> optDecl) {
		return importExcelFile(ScriptUtils.options(optDecl), shpPath, dsId);
	}
	
	public Void executeProcess(Map<String,Object> args, String procName) throws Exception {
		ExecuteProcessCommand cmd = new ExecuteProcessCommand(m_marmot, procName, args);
		return execute(cmd);
	}
	
	public <T> T execute(ScriptCommand<T> cmd) throws Exception {
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
