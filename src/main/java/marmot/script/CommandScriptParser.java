package marmot.script;

import java.util.Map;

import com.google.common.collect.Maps;

import groovy.lang.Closure;
import marmot.ExecutePlanOptions;
import marmot.MarmotRuntime;
import marmot.Plan;
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


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class CommandScriptParser extends GroovyDslClass {
	private final MarmotRuntime m_marmot;
	private final boolean m_verbose;
	
	public CommandScriptParser(MarmotRuntime marmot, boolean verbose) {
		m_marmot = marmot;
		m_verbose = verbose;
	}
	
	public CreateDataSetCommand createDataset(Map<String,Object> args, String dsId) {
		CreateDataSetCommand cmd = new CreateDataSetCommand(m_marmot, dsId, args);
		if ( m_verbose ) {
			System.out.println(cmd);
		}
		return cmd;
	}
	public CreateDataSetCommand createDataset(String dsId, Closure optDecl) {
		CreateDataSetCommand cmd = new CreateDataSetCommand(m_marmot, dsId, Maps.newHashMap());
		ScriptUtils.callClosure(optDecl, cmd);
		
		if ( m_verbose ) {
			System.out.println(cmd);
		}
		return cmd;
	}
	
	public DeleteDataSetCommand deleteDataset(String... dsId) {
		DeleteDataSetCommand cmd = new DeleteDataSetCommand(m_marmot, dsId);
		if ( m_verbose ) {
			System.out.println(cmd);
		}
		return cmd;
	}
	
	public MoveDataSetCommand moveDataset(String srcDsId, String dstDsId) {
		MoveDataSetCommand cmd = new MoveDataSetCommand(m_marmot, srcDsId, dstDsId);
		if ( m_verbose ) {
			System.out.println(cmd);
		}
		return cmd;
	}
	
	public ClusterDataSetCommand clusterDataset(String dsId) {
		ClusterDataSetCommand cmd = new ClusterDataSetCommand(m_marmot, dsId);
		if ( m_verbose ) {
			System.out.println(cmd);
		}
		return cmd;
	}
	
	public RunPlanCommand run(Plan plan) {
		RunPlanCommand cmd = new RunPlanCommand(m_marmot, plan);
		if ( m_verbose ) {
			System.out.println(cmd);
		}
		return cmd;
	}
	
	public RunPlanToRecordSet runPlanToRecordSet(Map<String,Object> args, Plan plan) {
		ExecutePlanOptions opts = parseExecutePlanOptions(args);
		RunPlanToRecordSet cmd = new RunPlanToRecordSet(m_marmot, plan, opts);
		if ( m_verbose ) {
			System.out.println(cmd);
		}
		return cmd;
	}
	public RunPlanToRecordSet runPlanToRecordSet(Plan plan) {
		return runPlanToRecordSet(Maps.newHashMap(), plan);
	}
	
	public RunPlanToRecord runPlanToRecord(Map<String,Object> args, Plan plan) {
		ExecutePlanOptions opts = parseExecutePlanOptions(args);
		RunPlanToRecord cmd = new RunPlanToRecord(m_marmot, plan, opts);
		if ( m_verbose ) {
			System.out.println(cmd);
		}
		return cmd;
	}
	public RunPlanToRecord runPlanToRecord(Plan plan) {
		return runPlanToRecord(Maps.newHashMap(), plan);
	}
	
	public RunPlanToGeometry runPlanToGeometry(Map<String,Object> args, Plan plan) {
		ExecutePlanOptions opts = parseExecutePlanOptions(args);
		RunPlanToGeometry cmd = new RunPlanToGeometry(m_marmot, plan, opts);
		if ( m_verbose ) {
			System.out.println(cmd);
		}
		return cmd;
	}
	public RunPlanToGeometry runPlanToGeometry(Plan plan) {
		return runPlanToGeometry(Maps.newHashMap(), plan);
	}
	
	public RunPlanToLong runPlanToLong(Map<String,Object> args, Plan plan) {
		ExecutePlanOptions opts = parseExecutePlanOptions(args);
		RunPlanToLong cmd = new RunPlanToLong(m_marmot, plan, opts);
		if ( m_verbose ) {
			System.out.println(cmd);
		}
		return cmd;
	}
	public RunPlanToLong runPlanToLong(Plan plan) {
		return runPlanToLong(Maps.newHashMap(), plan);
	}
	
	public RunPlanToString runPlanToString(Map<String,Object> args, Plan plan) {
		ExecutePlanOptions opts = parseExecutePlanOptions(args);
		RunPlanToString cmd = new RunPlanToString(m_marmot, plan, opts);
		if ( m_verbose ) {
			System.out.println(cmd);
		}
		return cmd;
	}
	public RunPlanToString runPlanToString(Plan plan) {
		return runPlanToString(Maps.newHashMap(), plan);
	}
	
	public ImportShapefileCommand importShapefile(Map<String,Object> args, String shpPath,
												String dsId) {
		ImportShapefileCommand cmd = new ImportShapefileCommand(m_marmot, shpPath, dsId, args);
		if ( m_verbose ) {
			System.out.println(cmd);
		}
		return cmd;
	}
	public ImportShapefileCommand importShapefile(String shpPath, String dsId, Closure optDecl) {
		return importShapefile(ScriptUtils.options(optDecl), shpPath, dsId);
	}
	
	public ImportCsvFileCommand importCsvFile(String csvPath, String dsId, Closure optDecl) {
		ImportCsvFileCommand cmd = new ImportCsvFileCommand(m_marmot, csvPath, dsId);
		ScriptUtils.callClosure(optDecl, cmd);
		if ( m_verbose ) {
			System.out.println(cmd);
		}
		return cmd;
	}
	
	public ImportExcelFileCommand importExcelFile(Map<String,Object> args, String excelPath, String dsId) {
		ImportExcelFileCommand cmd = new ImportExcelFileCommand(m_marmot, excelPath, dsId, args);
		if ( m_verbose ) {
			System.out.println(cmd);
		}
		return cmd;
	}
	public ImportExcelFileCommand importExcelFile(String shpPath, String dsId, Closure optDecl) {
		return importExcelFile(ScriptUtils.options(optDecl), shpPath, dsId);
	}
	
	public ExecuteProcessCommand executeProcess(Map<String,Object> args, String procName) {
		return new ExecuteProcessCommand(m_marmot, procName, args);
	}
	
	public Plan plan(String name, Closure script) {
		return ScriptUtils.parsePlan(m_marmot, name, script);
	}
	
	private ExecutePlanOptions parseExecutePlanOptions(Map<String,Object> args) {
		ExecutePlanOptions opts = ExecutePlanOptions.create();
		ScriptUtils.getBooleanOption(args, "disableLocalExec")
					.ifPresent(opts::disableLocalExecution);
		ScriptUtils.getStringOption(args, "mapOutputCompressionCodec")
					.ifPresent(opts::mapOutputCompressionCodec);
		
		return opts;
	}
}
