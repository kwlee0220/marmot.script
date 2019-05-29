package marmot.script.command;

import java.io.File;
import java.util.Map;

import groovy.lang.Closure;
import marmot.GeometryColumnInfo;
import marmot.MarmotRuntime;
import marmot.command.ImportParameters;
import marmot.externio.shp.ImportShapefile;
import marmot.externio.shp.ShapefileParameters;
import marmot.script.GroovyDslClass;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ImportShapefileCommand extends GroovyDslClass
									implements ScriptCommand<Long> {
	private final MarmotRuntime m_marmot;
	private final String m_shpPath;
	private ShapefileParameters m_params = ShapefileParameters.create();
	private ImportParameters m_importParams = new ImportParameters();
	
	public ImportShapefileCommand(MarmotRuntime marmot, String shpPath, String dsId,
									Map<String,Object> args, Closure<?> optDecl) {
		m_marmot = marmot;
		m_shpPath = shpPath;
		
		ScriptUtils.getStringOption(args, "charset").ifPresent(m_params::charset);
		ScriptUtils.getStringOption(args, "shpSrid").ifPresent(m_params::shpSrid);

		m_importParams.setDataSetId(dsId);
		ScriptUtils.getStringOption(args, "geometry").map(GeometryColumnInfo::fromString)
													.ifPresent(m_importParams::setGeometryColumnInfo);
		ScriptUtils.getBooleanOption(args, "force").ifPresent(m_importParams::setForce);
		ScriptUtils.getBooleanOption(args, "append").ifPresent(m_importParams::setAppend);
		ScriptUtils.getBooleanOption(args, "compression").ifPresent(m_importParams::setCompression);
		ScriptUtils.getOption(args, "blockSize")
					.map(ScriptUtils::parseByteLength).ifPresent(m_importParams::setBlockSize);
		ScriptUtils.getIntOption(args, "reportInterval").ifPresent(m_importParams::setReportInterval);
		
		if ( optDecl != null ) {
			ScriptUtils.callClosure(optDecl, this);
		}
	}
	
	public ImportShapefileCommand shpSrid(String srid) {
		m_params.shpSrid(srid);
		return this;
	}
	
	public ImportShapefileCommand charset(String charset) {
		m_params.charset(charset);
		return this;
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
	
	public ImportShapefileCommand compression(boolean flag) {
		m_importParams.setCompression(flag);
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
