package marmot.script;

import java.util.Collections;
import java.util.Map;

import com.google.common.collect.Maps;
import com.vividsolutions.jts.geom.Geometry;

import groovy.lang.Closure;
import marmot.ExecutePlanOptions;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.Record;
import marmot.RecordSet;
import marmot.dataset.DataSet;
import marmot.script.command.ClusterDataSetCommand;
import marmot.script.command.CommandReport;
import marmot.script.command.CreateDataSetCommand;
import marmot.script.command.DeleteDataSetCommand;
import marmot.script.command.ExportToCsvFileCommand;
import marmot.script.command.ExportToShapefileCommand;
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
import marmot.script.command.RunProcessCommand;
import marmot.script.command.ScriptCommand;
import marmot.script.dslobj.GDataSet;
import marmot.script.dslobj.GProcess;
import utils.StopWatch;
import utils.Throwables;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public abstract class ScriptCommandRunner extends DslScriptBase {
	private static final Map<String,Object> EMPTY_ARGS = Collections.unmodifiableMap(Maps.newHashMap());
	
	private Boolean m_verbose;
	
	public DataSet createDataSet(Map<String,Object> args, String dsId, Closure<?> optDecl)
		throws Exception {
		MarmotRuntime marmot = getMarmotRuntime();
		
		CreateDataSetCommand cmd = new CreateDataSetCommand(marmot, dsId, args, optDecl);
		return execute(cmd);
	}
	public DataSet createDataSet(Map<String,Object> args, String dsId) throws Exception {
		return createDataSet(args, dsId, null);
	}
	public DataSet createDataSet(String dsId, Closure<?> optDecl) throws Exception {
		return createDataSet(EMPTY_ARGS, dsId, optDecl);
	}
	
	public Void delete(GDataSet ds) throws Exception {
		DeleteDataSetCommand cmd = new DeleteDataSetCommand(getMarmotRuntime(), ds.getId());
		return execute(cmd);
	}
	
	public Void moveDataSet(String srcDsId, String dstDsId) throws Exception {
		MoveDataSetCommand cmd = new MoveDataSetCommand(getMarmotRuntime(), srcDsId, dstDsId);
		return execute(cmd);
	}
	
	public CommandReport cluster(GDataSet ds) throws Exception {
		ClusterDataSetCommand cmd = new ClusterDataSetCommand(getMarmotRuntime(), ds.getId());
		return execute(cmd);
	}
	
	public void run(Plan plan) throws Exception {
		RunPlanCommand cmd = new RunPlanCommand(getMarmotRuntime(), plan);
		execute(cmd);
	}
	public void run(Map<String,Object> args, Plan plan) throws Exception {
		ExecutePlanOptions opts = ScriptUtils.parseExecutePlanOptions(args);
		RunPlanCommand cmd = new RunPlanCommand(getMarmotRuntime(), plan, opts);
		execute(cmd);
	}
	
	public Void run(GProcess gproc) throws Exception {
		RunProcessCommand cmd = new RunProcessCommand(getMarmotRuntime(), gproc);
		return cmd.execute();
	}
	
	public RecordSet runToRecordSet(Map<String,Object> args, Plan plan) throws Exception {
		ExecutePlanOptions opts = ScriptUtils.parseExecutePlanOptions(args);
		RunPlanToRecordSetCommand cmd = new RunPlanToRecordSetCommand(getMarmotRuntime(), plan, opts);
		return execute(cmd);
	}
	public RecordSet runToRecordSet(Plan plan) throws Exception {
		return runToRecordSet(EMPTY_ARGS, plan);
	}
	
	public Record runToRecord(Map<String,Object> args, Plan plan) throws Exception {
		ExecutePlanOptions opts = ScriptUtils.parseExecutePlanOptions(args);
		RunPlanToRecordCommand cmd = new RunPlanToRecordCommand(getMarmotRuntime(), plan, opts);
		return execute(cmd);
	}
	public Record runToRecord(Plan plan) throws Exception {
		return runToRecord(EMPTY_ARGS, plan);
	}
	
	public Geometry runToGeometry(Map<String,Object> args, Plan plan) throws Exception {
		ExecutePlanOptions opts = ScriptUtils.parseExecutePlanOptions(args);
		RunPlanToGeometryCommand cmd = new RunPlanToGeometryCommand(getMarmotRuntime(), plan, opts);
		return execute(cmd);
	}
	public Geometry runToGeometry(Plan plan) throws Exception {
		return runToGeometry(EMPTY_ARGS, plan);
	}
	
	public Long runToLong(Map<String,Object> args, Plan plan) throws Exception {
		ExecutePlanOptions opts = ScriptUtils.parseExecutePlanOptions(args);
		RunPlanToLongCommand cmd = new RunPlanToLongCommand(getMarmotRuntime(), plan, opts);
		return execute(cmd);
	}
	public Long runToLong(Plan plan) throws Exception {
		return runToLong(EMPTY_ARGS, plan);
	}
	
	public String runToString(Map<String,Object> args, Plan plan) throws Exception {
		ExecutePlanOptions opts = ScriptUtils.parseExecutePlanOptions(args);
		RunPlanToStringCommand cmd = new RunPlanToStringCommand(getMarmotRuntime(), plan, opts);
		return execute(cmd);
	}
	public String runToString(Plan plan) throws Exception {
		return runToString(EMPTY_ARGS, plan);
	}
	
	public long importShapefile(String shpPath, String dsId, Closure<?> optDecl) throws Exception {
		MarmotRuntime marmot = getMarmotRuntime();
		return execute(new ImportShapefileCommand(marmot, shpPath, dsId, optDecl));
	}
	
	public long exportToShapefile(Map<String,Object> args, String dsId, String shpPath,
									Closure<?> optDecl) throws Exception {
		MarmotRuntime marmot = getMarmotRuntime();
		ExportToShapefileCommand cmd
				= new ExportToShapefileCommand(marmot, dsId, shpPath, args, optDecl);
		return execute(cmd);
	}
	public long exportToShapefile(Map<String,Object> args, String dsId, String shpPath)
		throws Exception {
		return exportToShapefile(args, dsId, shpPath, null);
	}
	public long exportToShapefile(String dsId, String shpPath, Closure<?> optDecl)
		throws Exception {
		return exportToShapefile(EMPTY_ARGS, dsId, shpPath, optDecl);
	}

	public long importCsvFile(String csvPath, String dsId, Closure<?> optDecl) throws Exception {
		MarmotRuntime marmot = getMarmotRuntime();
		return execute(new ImportCsvFileCommand(marmot, csvPath, dsId, optDecl));
	}
	public long importCsvFile(String csvPath, String dsId) throws Exception {
		return importCsvFile(csvPath, dsId, null);
	}
	
	public long exportToCsvFile(Map<String,Object> args, String dsId, String csvPath,
								Closure<?> optDecl) throws Exception {
		MarmotRuntime marmot = getMarmotRuntime();
		ExportToCsvFileCommand cmd
							= new ExportToCsvFileCommand(marmot, dsId, csvPath, args, optDecl);
		return execute(cmd);
	}
	public long exportToCsvFile(Map<String,Object> args, String dsId, String csvPath) throws Exception {
		return exportToCsvFile(args, dsId, csvPath, null);
	}
	public long exportToCsvFile(String dsId, String csvPath, Closure<?> optDecl)
		throws Exception {
		return exportToCsvFile(EMPTY_ARGS, dsId, csvPath, optDecl);
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
	
	private <T> T execute(ScriptCommand<T> cmd) throws Exception {
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
				String msg = ret != null ? ret.toString() : "done";
				System.out.printf(" -> %s (elapsed=%s)%n", msg, watch.getElapsedSecondString());
			}
			
			return ret;
		}
		catch ( Exception e ) {
			Throwable cause = Throwables.unwrapThrowable(e);
			if ( m_verbose ) {
				System.out.printf(" -> cause=%s%n", cmd, cause);
			}
			
			Throwables.sneakyThrow(cause);
			throw new AssertionError();
		}
	}
}
