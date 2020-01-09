package marmot.script.command;

import java.io.File;

import groovy.lang.Closure;
import marmot.MarmotRuntime;
import marmot.command.ImportParameters;
import marmot.dataset.GeometryColumnInfo;
import marmot.externio.csv.ImportCsv;
import marmot.script.ScriptUtils;
import marmot.script.dslobj.CsvParametersParser;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ImportCsvFileCommand extends CsvParametersParser implements ScriptCommand<Long> {
	private final MarmotRuntime m_marmot;
	private final String m_csvPath;
	private ImportParameters m_importParams = new ImportParameters();
	private String m_glob = "**/*.csv"; 
	
	public ImportCsvFileCommand(MarmotRuntime marmot, String path, String dsId, Closure<?> opts) {
		m_marmot = marmot;
		m_csvPath = path;
		m_importParams.setDataSetId(dsId);
		
		if ( opts != null ) {
			ScriptUtils.callClosure(opts, this);
		}
	}
	
	public ImportCsvFileCommand geometry(String str) {
		m_importParams.setGeometryColumnInfo(GeometryColumnInfo.fromString(str));
		return this;
	}
	
	public ImportCsvFileCommand glob(String glob) {
		m_glob = glob;
		return this;
	}
	
	public ImportCsvFileCommand force(boolean flag) {
		m_importParams.setForce(flag);
		return this;
	}
	
	public ImportCsvFileCommand append(boolean flag) {
		m_importParams.setAppend(flag);
		return this;
	}
	
	public ImportCsvFileCommand compressionCodecName(String codecName) {
		m_importParams.setCompressionCodecName(codecName);
		return this;
	}
	
	public ImportCsvFileCommand blockSize(Object blkSize) {
		m_importParams.setBlockSize(ScriptUtils.parseByteLength(blkSize));
		return this;
	}
	
	public ImportCsvFileCommand reportInterval(int intvl) {
		m_importParams.setReportInterval(intvl);
		return this;
	}

	@Override
	public Long execute() {
		return ImportCsv.from(new File(m_csvPath), m_params, m_importParams, m_glob)
						.run(m_marmot);
	}
	
	@Override
	public String toString() {
		return String.format("importCsvFile('%s','%s',delim='%s')",
							m_csvPath, m_importParams.getDataSetId(), m_params.delimiter());
	}
}
