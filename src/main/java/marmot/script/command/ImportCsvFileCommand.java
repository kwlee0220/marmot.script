package marmot.script.command;

import java.io.File;

import groovy.lang.Closure;
import marmot.GeometryColumnInfo;
import marmot.MarmotRuntime;
import marmot.command.ImportParameters;
import marmot.externio.csv.ImportCsv;
import marmot.script.ScriptUtils;
import marmot.script.dslobj.CsvOptionsParser;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ImportCsvFileCommand extends CsvOptionsParser implements MarmotScriptCommand {
	private final MarmotRuntime m_marmot;
	private final String m_csvPath;
	private ImportParameters m_importParams = new ImportParameters();
	
	public ImportCsvFileCommand(MarmotRuntime marmot, String path) {
		m_marmot = marmot;
		m_csvPath = path;
	}
	
	@Override
	public Object getProperty(String name) {
		switch ( name ) {
			case "force":
				m_importParams.setForce(true);
				return this;
		}
		
		return super.getProperty(name);
	}

	@Override
    public void setProperty(String name, Object newValue) {
		switch ( name ) {
			case "force":
				m_importParams.setForce((Boolean)newValue);
				return;
		}
		
		super.setProperty(name, newValue);
	}
	
	public ImportCsvFileCommand to(String dsId) {
		m_importParams.setDataSetId(dsId);
		return this;
	}
	
	public ImportCsvFileCommand geometry(String str) {
		m_importParams.setGeometryColumnInfo(GeometryColumnInfo.fromString(str));
		return this;
	}
	
	public ImportCsvFileCommand blockSize(Object obj) {
		m_importParams.setBlockSize(ScriptUtils.parseByteLength(obj));
		return this;
	}
	
	public ImportCsvFileCommand compression(boolean flag) {
		m_importParams.setCompression(flag);
		return this;
	}
	
	public ImportCsvFileCommand options(Closure script) {
		ScriptUtils.callClosure(script, this);
		return this;
	}

	@Override
	public void execute() {
		ImportCsv.from(new File(m_csvPath), m_options, m_importParams)
						.run(m_marmot);
	}
	
	@Override
	public String toString() {
		return String.format("import_shapefile '%s' into '%s'",
							m_csvPath, m_importParams.getDataSetId());
	}
}
