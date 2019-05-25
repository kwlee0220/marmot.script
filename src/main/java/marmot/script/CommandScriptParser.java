package marmot.script;

import java.util.Map;

import com.google.common.collect.Maps;

import groovy.lang.Closure;
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
	public void importExcelFile(String shpPath, String dsId, Closure optDecl) {
		importExcelFile(ScriptUtils.options(optDecl), shpPath, dsId);
	}
	
	public Plan plan(String name, Closure script) {
		return ScriptUtils.parsePlan(name, script);
	}
}
