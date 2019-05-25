package marmot.script.command;

import java.io.File;
import java.util.Map;

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
									Map<String,Object> args) {
		m_marmot = marmot;
		m_path = path;

		ScriptUtils.getBooleanOption(args, "headerFirst").ifPresent(m_excelParams::headerFirst);
		ScriptUtils.getStringOption(args, "nullString").ifPresent(m_excelParams::nullString);
		ScriptUtils.getStringOption(args, "shpSrid").ifPresent(m_excelParams::srid);
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
