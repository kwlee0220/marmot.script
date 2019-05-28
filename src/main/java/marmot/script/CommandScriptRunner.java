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
import marmot.RecordSchema;
import marmot.RecordSet;
import marmot.script.command.ClusterDataSetCommand;
import marmot.script.command.CreateDataSetCommand;
import marmot.script.command.DeleteDataSetCommand;
import marmot.script.command.ExecuteProcessCommand;
import marmot.script.command.ImportCsvFileCommand;
import marmot.script.command.ImportExcelFileCommand;
import marmot.script.command.ImportShapefileCommand;
import marmot.script.command.MoveDataSetCommand;
import marmot.script.command.RunPlanCommand;
import marmot.script.command.RunPlanToGeometry;
import marmot.script.command.RunPlanToLong;
import marmot.script.command.RunPlanToRecord;
import marmot.script.command.RunPlanToRecordSet;
import marmot.script.command.RunPlanToString;
import marmot.script.command.ScriptCommand;
import utils.Size2d;
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
	
	public RecordSet runPlanToRecordSet(Map<String,Object> args, Plan plan) {
		ExecutePlanOptions opts = parseExecutePlanOptions(args);
		RunPlanToRecordSet cmd = new RunPlanToRecordSet(m_marmot, plan, opts);
		return execute(cmd);
	}
	public RecordSet runPlanToRecordSet(Plan plan) {
		return runPlanToRecordSet(Maps.newHashMap(), plan);
	}
	
	public Record runPlanToRecord(Map<String,Object> args, Plan plan) {
		ExecutePlanOptions opts = parseExecutePlanOptions(args);
		RunPlanToRecord cmd = new RunPlanToRecord(m_marmot, plan, opts);
		return execute(cmd);
	}
	public Record runPlanToRecord(Plan plan) {
		return runPlanToRecord(Maps.newHashMap(), plan);
	}
	
	public Geometry runPlanToGeometry(Map<String,Object> args, Plan plan) {
		ExecutePlanOptions opts = parseExecutePlanOptions(args);
		RunPlanToGeometry cmd = new RunPlanToGeometry(m_marmot, plan, opts);
		return execute(cmd);
	}
	public Geometry runPlanToGeometry(Plan plan) {
		return runPlanToGeometry(Maps.newHashMap(), plan);
	}
	
	public Long runPlanToLong(Map<String,Object> args, Plan plan) {
		ExecutePlanOptions opts = parseExecutePlanOptions(args);
		RunPlanToLong cmd = new RunPlanToLong(m_marmot, plan, opts);
		return execute(cmd);
	}
	public Long runPlanToLong(Plan plan) {
		return runPlanToLong(Maps.newHashMap(), plan);
	}
	
	public String runPlanToString(Map<String,Object> args, Plan plan) {
		ExecutePlanOptions opts = parseExecutePlanOptions(args);
		RunPlanToString cmd = new RunPlanToString(m_marmot, plan, opts);
		return execute(cmd);
	}
	public String runPlanToString(Plan plan) {
		return runPlanToString(Maps.newHashMap(), plan);
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
	
	public Void executeProcess(Map<String,Object> args, String procName) {
		ExecuteProcessCommand cmd = new ExecuteProcessCommand(m_marmot, procName, args);
		return execute(cmd);
	}
	
	//
	//
	//
	
	public Plan plan(String name, Closure script) {
		return ScriptUtils.parsePlan(m_marmot, name, script);
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
	
	private ExecutePlanOptions parseExecutePlanOptions(Map<String,Object> args) {
		ExecutePlanOptions opts = ExecutePlanOptions.create();
		ScriptUtils.getBooleanOption(args, "disableLocalExec")
					.ifPresent(opts::disableLocalExecution);
		ScriptUtils.getStringOption(args, "mapOutputCompressionCodec")
					.ifPresent(opts::mapOutputCompressionCodec);
		
		return opts;
	}
	
	public Size2d size2d(String str) {
		return ScriptUtils.parseSize2d(str);
	}
	public Size2d size2d(Object widthExpr, Object heightExpr) {
		double width = ScriptUtils.parseDistance(widthExpr);
		double height = ScriptUtils.parseDistance(heightExpr);
		return new Size2d(width, height);
	}
	
	public RecordSchema schema(String decl) {
		return RecordSchema.parse(decl);
	}
}
