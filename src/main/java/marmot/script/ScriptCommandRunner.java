package marmot.script;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Geometry;

import groovy.lang.Closure;
import marmot.DataSet;
import marmot.ExecutePlanOptions;
import marmot.Plan;
import marmot.Record;
import marmot.RecordSet;
import marmot.script.command.AppendRecordSetIntoDataSetCommand;
import marmot.script.command.ClusterDataSetCommand;
import marmot.script.command.CreateDataSetCommand;
import marmot.script.command.DeleteDataSetCommand;
import marmot.script.command.ExecuteProcessCommand;
import marmot.script.command.ExportDataSetAsCsvCommand;
import marmot.script.command.ExportDataSetAsShapefileCommand;
import marmot.script.command.ImportCsvFileCommand;
import marmot.script.command.ImportExcelFileCommand;
import marmot.script.command.ImportShapefileCommand;
import marmot.script.command.MoveDataSetCommand;
import marmot.script.command.RunPlanCommand;
import marmot.script.command.RunPlanToGeometryCommand;
import marmot.script.command.RunPlanToLongCommand;
import marmot.script.command.RunPlanToRecordCommand;
import marmot.script.command.RunPlanToRecordSetCommand;
import marmot.script.command.RunPlanToStringCommand;
import marmot.script.command.ScriptCommand;
import utils.StopWatch;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public abstract class ScriptCommandRunner extends DslScriptBase {
	private static final Map<String,Object> EMPTY_ARGS = Collections.unmodifiableMap(Maps.newHashMap());
	
	private Boolean m_verbose;
	
	public DataSet createDataSet(Map<String,Object> args, String dsId) throws Exception {
		CreateDataSetCommand cmd = new CreateDataSetCommand(getMarmotRuntime(), dsId, args);
		return execute(cmd);
	}
	public DataSet createDataSet(String dsId, Closure<?> optDecl) throws Exception {
		CreateDataSetCommand cmd = new CreateDataSetCommand(getMarmotRuntime(), dsId, EMPTY_ARGS);
		ScriptUtils.callClosure(optDecl, cmd);
		return execute(cmd);
	}
	
	public DataSet appendRecordSetIntoDataSet(String dsId, Closure<?> optDecl) throws Exception {
		AppendRecordSetIntoDataSetCommand cmd = new AppendRecordSetIntoDataSetCommand(getMarmotRuntime(), dsId,
																	EMPTY_ARGS);
		ScriptUtils.callClosure(optDecl, cmd);
		return execute(cmd);
	}
	
	public Void deleteDataSet(String... dsId) throws Exception {
		DeleteDataSetCommand cmd = new DeleteDataSetCommand(getMarmotRuntime(), dsId);
		return execute(cmd);
	}
	
	public Void moveDataSet(String srcDsId, String dstDsId) throws Exception {
		MoveDataSetCommand cmd = new MoveDataSetCommand(getMarmotRuntime(), srcDsId, dstDsId);
		return execute(cmd);
	}
	
	public Void clusterDataSet(String dsId) throws Exception {
		ClusterDataSetCommand cmd = new ClusterDataSetCommand(getMarmotRuntime(), dsId);
		return execute(cmd);
	}
	
	public Void run(Plan plan) throws Exception {
		RunPlanCommand cmd = new RunPlanCommand(getMarmotRuntime(), plan);
		return execute(cmd);
	}
	
	public RecordSet runPlanToRecordSet(Map<String,Object> args, Plan plan) throws Exception {
		ExecutePlanOptions opts = ScriptUtils.parseExecutePlanOptions(args);
		RunPlanToRecordSetCommand cmd = new RunPlanToRecordSetCommand(getMarmotRuntime(), plan, opts);
		return execute(cmd);
	}
	public RecordSet runPlanToRecordSet(Plan plan) throws Exception {
		return runPlanToRecordSet(EMPTY_ARGS, plan);
	}
	
	public Record runPlanToRecord(Map<String,Object> args, Plan plan) throws Exception {
		ExecutePlanOptions opts = ScriptUtils.parseExecutePlanOptions(args);
		RunPlanToRecordCommand cmd = new RunPlanToRecordCommand(getMarmotRuntime(), plan, opts);
		return execute(cmd);
	}
	public Record runPlanToRecord(Plan plan) throws Exception {
		return runPlanToRecord(EMPTY_ARGS, plan);
	}
	
	public Geometry runPlanToGeometry(Map<String,Object> args, Plan plan) throws Exception {
		ExecutePlanOptions opts = ScriptUtils.parseExecutePlanOptions(args);
		RunPlanToGeometryCommand cmd = new RunPlanToGeometryCommand(getMarmotRuntime(), plan, opts);
		return execute(cmd);
	}
	public Geometry runPlanToGeometry(Plan plan) throws Exception {
		return runPlanToGeometry(EMPTY_ARGS, plan);
	}
	
	public Long runPlanToLong(Map<String,Object> args, Plan plan) throws Exception {
		ExecutePlanOptions opts = ScriptUtils.parseExecutePlanOptions(args);
		RunPlanToLongCommand cmd = new RunPlanToLongCommand(getMarmotRuntime(), plan, opts);
		return execute(cmd);
	}
	public Long runPlanToLong(Plan plan) throws Exception {
		return runPlanToLong(EMPTY_ARGS, plan);
	}
	
	public String runPlanToString(Map<String,Object> args, Plan plan) throws Exception {
		ExecutePlanOptions opts = ScriptUtils.parseExecutePlanOptions(args);
		RunPlanToStringCommand cmd = new RunPlanToStringCommand(getMarmotRuntime(), plan, opts);
		return execute(cmd);
	}
	public String runPlanToString(Plan plan) throws Exception {
		return runPlanToString(EMPTY_ARGS, plan);
	}
	
	public long importShapefile(Map<String,Object> args, String shpPath,
								String dsId, Closure<?> optDecl) throws Exception {
		ImportShapefileCommand cmd = new ImportShapefileCommand(getMarmotRuntime(), shpPath, dsId, args, optDecl);
		return execute(cmd);
	}
	public long importShapefile(String shpPath, String dsId, Closure<?> optDecl) throws Exception {
		return importShapefile(EMPTY_ARGS, shpPath, dsId, optDecl);
	}
	
	public long exportDataSetToShapefile(Map<String,Object> args, String dsId,
										String shpPath, Closure<?> optDecl)
		throws Exception {
		ExportDataSetAsShapefileCommand cmd
								= new ExportDataSetAsShapefileCommand(getMarmotRuntime(), dsId, shpPath,
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
						= new ExportDataSetAsShapefileCommand(getMarmotRuntime(), dsId, shpPath,
																EMPTY_ARGS, optDecl);
		return execute(cmd);
	}
	
	public long importCsvFile(String csvPath, String dsId) throws Exception {
		ImportCsvFileCommand cmd = new ImportCsvFileCommand(getMarmotRuntime(), csvPath, dsId);
		return execute(cmd);
	}
	
	public long exportDataSetAsCsv(Map<String,Object> args, String dsId,
									String csvPath, Closure<?> optDecl)
		throws Exception {
		ExportDataSetAsCsvCommand cmd
							= new ExportDataSetAsCsvCommand(getMarmotRuntime(), dsId, csvPath,
															args, optDecl);
		return execute(cmd);
	}
	public long exportDataSetAsCsv(Map<String,Object> args, String dsId,
										String csvPath) throws Exception {
		return exportDataSetToShapefile(args, dsId, csvPath, null);
	}
	public long exportDataSetAsCsv(String dsId, String csvPath, Closure<?> optDecl)
		throws Exception {
		ExportDataSetAsCsvCommand cmd
						= new ExportDataSetAsCsvCommand(getMarmotRuntime(), dsId, csvPath,
														EMPTY_ARGS, optDecl);
		return execute(cmd);
	}
	
	public long importExcelFile(Map<String,Object> args, String excelPath, String dsId,
								Closure<?> optDecls) throws Exception {
		ImportExcelFileCommand cmd = new ImportExcelFileCommand(getMarmotRuntime(), excelPath, dsId,
																args, optDecls);
		return execute(cmd);
	}
	public long importExcelFile(String shpPath, String dsId, Closure<?> optDecl) throws Exception {
		return importExcelFile(EMPTY_ARGS, shpPath, dsId, optDecl);
	}
	
	public Void executeProcess(Map<String,Object> args, String procName) throws Exception {
		ExecuteProcessCommand cmd = new ExecuteProcessCommand(getMarmotRuntime(), procName, args);
		return execute(cmd);
	}
	
	public <T> T execute(ScriptCommand<T> cmd) throws Exception {
		if ( m_verbose == null ) {
			m_verbose = (Boolean)getBinding().getProperty("MARMOT_VERBOSE");
		}
		
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
