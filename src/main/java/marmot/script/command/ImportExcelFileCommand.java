package marmot.script.command;

import java.io.File;

import groovy.lang.Closure;
import marmot.GeometryColumnInfo;
import marmot.MarmotRuntime;
import marmot.command.ImportParameters;
import marmot.externio.excel.ExcelParameters;
import marmot.externio.excel.ImportExcel;
import marmot.script.GroovyDslClass;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ImportExcelFileCommand extends GroovyDslClass
									implements MarmotScriptCommand<Long> {
	private final MarmotRuntime m_marmot;
	private final String m_path;
	private ExcelParameters m_excelParams = new ExcelParameters();
	private ImportParameters m_importParams = new ImportParameters();
	
	public ImportExcelFileCommand(MarmotRuntime marmot, String path) {
		m_marmot = marmot;
		m_path = path;
	}
	
	@Override
	public Object getProperty(String name) {
		switch ( name ) {
			case "force":
				m_importParams.setForce(true);
				return this;
			case "headerFirst":
				return headerFirst(true);
		}
		
		return super.getProperty(name);
	}

	@Override
    public void setProperty(String name, Object value) {
		switch ( name ) {
			case "force":
				m_importParams.setForce((Boolean)value);
				return;
			case "headerFirst":
				headerFirst((Boolean)value);
				break;
		}
		
		super.setProperty(name, value);
	}
	
	public ImportExcelFileCommand to(String dsId) {
		m_importParams.setDataSetId(dsId);
		return this;
	}
	
	public ImportExcelFileCommand geometry(String str) {
		m_importParams.setGeometryColumnInfo(GeometryColumnInfo.fromString(str));
		return this;
	}
	
	public ImportExcelFileCommand headerFirst(boolean flag) {
		m_excelParams.headerFirst(flag);
		return this;
	}
	
	public ImportExcelFileCommand pointColumns(String pointCols) {
		m_excelParams.pointColumns(pointCols);
		return this;
	}
	
	public ImportExcelFileCommand srid(String srid) {
		m_excelParams.srid(srid);
		return this;
	}
	
	public ImportExcelFileCommand blockSize(Object obj) {
		m_importParams.setBlockSize(ScriptUtils.parseByteLength(obj));
		return this;
	}
	
	public ImportExcelFileCommand compression(boolean flag) {
		m_importParams.setCompression(flag);
		return this;
	}
	
	public ImportExcelFileCommand force(boolean flag) {
		m_importParams.setForce(flag);
		return this;
	}
	
	public ImportExcelFileCommand options(Closure script) {
		ScriptUtils.callClosure(script, this);
		return this;
	}

	@Override
	public Long execute() {
		return ImportExcel.from(new File(m_path), m_excelParams, m_importParams)
							.run(m_marmot);
	}
	
	@Override
	public String toString() {
		return String.format("import_excelfile '%s' into '%s'",
							m_path, m_importParams.getDataSetId());
	}
}
