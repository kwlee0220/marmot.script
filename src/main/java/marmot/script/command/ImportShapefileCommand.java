package marmot.script.command;

import java.io.File;

import groovy.lang.Closure;
import marmot.MarmotRuntime;
import marmot.command.ImportParameters;
import marmot.dataset.GeometryColumnInfo;
import marmot.externio.shp.ImportShapefile;
import marmot.externio.shp.ShapefileParameters;
import marmot.script.ScriptUtils;
import marmot.script.dslobj.ShapefileParametersParser;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ImportShapefileCommand extends ShapefileParametersParser
									implements ScriptCommand<Long> {
	private final MarmotRuntime m_marmot;
	private final String m_shpPath;
	private ImportParameters m_importParams = new ImportParameters();
	
	public ImportShapefileCommand(MarmotRuntime marmot, String shpPath, String dsId,
									Closure<?> optDecl) {
		m_marmot = marmot;
		m_shpPath = shpPath;
		m_importParams.setDataSetId(dsId);
		
		if ( optDecl != null ) {
			ScriptUtils.callClosure(optDecl, this);
		}
	}
	
	public ImportShapefileCommand geometry(String gcInfoStr) {
		m_importParams.setGeometryColumnInfo(GeometryColumnInfo.fromString(gcInfoStr));
		return this;
	}
	
	public ImportShapefileCommand force(boolean flag) {
		m_importParams.setForce(flag);
		return this;
	}
	
	public ImportShapefileCommand append(boolean flag) {
		m_importParams.setAppend(flag);
		return this;
	}
	
	public ImportShapefileCommand compressionCodecName(String codecName) {
		m_importParams.setCompressionCodecName(codecName);
		return this;
	}
	
	public ImportShapefileCommand blockSize(Object blkSize) {
		m_importParams.setBlockSize(ScriptUtils.parseByteLength(blkSize));
		return this;
	}
	
	public ImportShapefileCommand reportInterval(int intvl) {
		m_importParams.setReportInterval(intvl);
		return this;
	}

	@Override
	public Long execute() {
		return ImportShapefile.from(new File(m_shpPath), m_params, m_importParams)
								.run(m_marmot);
	}
	
	@Override
	public String toString() {
		return String.format("importShapefile '%s' into '%s'",
							m_shpPath, m_importParams.getDataSetId());
	}
}
