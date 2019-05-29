package marmot.script.command;

import java.io.File;
import java.util.Map;

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
									implements ScriptCommand<Long> {
	private final MarmotRuntime m_marmot;
	private final String m_path;
	private ExcelParameters m_excelParams = new ExcelParameters();
	private ImportParameters m_importParams = new ImportParameters();
	
	public ImportExcelFileCommand(MarmotRuntime marmot, String path, String dsId,
									Map<String,Object> args, Closure<?> optDecls) {
		m_marmot = marmot;
		m_path = path;

		ScriptUtils.getBooleanOption(args, "headerFirst").ifPresent(m_excelParams::headerFirst);
		ScriptUtils.getStringOption(args, "nullString").ifPresent(m_excelParams::nullString);
		ScriptUtils.getStringOption(args, "srid").ifPresent(m_excelParams::srid);
		ScriptUtils.getStringOption(args, "pointColumns").ifPresent(m_excelParams::pointColumns);

		m_importParams.setDataSetId(dsId);
		ScriptUtils.getStringOption(args, "geometry").map(GeometryColumnInfo::fromString)
													.ifPresent(m_importParams::setGeometryColumnInfo);
		ScriptUtils.getBooleanOption(args, "force").ifPresent(m_importParams::setForce);
		ScriptUtils.getBooleanOption(args, "append").ifPresent(m_importParams::setAppend);
		ScriptUtils.getBooleanOption(args, "compression").ifPresent(m_importParams::setCompression);
		ScriptUtils.getOption(args, "blockSize")
					.map(ScriptUtils::parseByteLength).ifPresent(m_importParams::setBlockSize);
		ScriptUtils.getIntOption(args, "reportInterval").ifPresent(m_importParams::setReportInterval);
		
		if ( optDecls != null ) {
			ScriptUtils.callClosure(optDecls, this);
		}
	}

	public ImportExcelFileCommand headerFirst(boolean flag) {
		m_excelParams.headerFirst(flag);
		return this;
	}
	
	public ImportExcelFileCommand srid(String srid) {
		m_excelParams.srid(srid);
		return this;
	}
	
	public ImportExcelFileCommand nullString(String value) {
		m_excelParams.nullString(value);
		return this;
	}
	
	public ImportExcelFileCommand pointColumns(String cols) {
		m_excelParams.pointColumns(cols);
		return this;
	}
	
	public ImportExcelFileCommand geometry(String gcInfoStr) {
		m_importParams.setGeometryColumnInfo(GeometryColumnInfo.fromString(gcInfoStr));
		return this;
	}
	
	public ImportExcelFileCommand force(boolean flag) {
		m_importParams.setForce(flag);
		return this;
	}
	
	public ImportExcelFileCommand append(boolean flag) {
		m_importParams.setAppend(flag);
		return this;
	}
	
	public ImportExcelFileCommand compression(boolean flag) {
		m_importParams.setCompression(flag);
		return this;
	}
	
	public ImportExcelFileCommand blockSize(Object blkSize) {
		m_importParams.setBlockSize(ScriptUtils.parseByteLength(blkSize));
		return this;
	}
	
	public ImportExcelFileCommand reportInterval(int intvl) {
		m_importParams.setReportInterval(intvl);
		return this;
	}

	@Override
	public Long execute() {
		return ImportExcel.from(new File(m_path), m_excelParams, m_importParams)
							.run(m_marmot);
	}
	
	@Override
	public String toString() {
		return String.format("importExcelFile '%s' into '%s'",
							m_path, m_importParams.getDataSetId());
	}
}
