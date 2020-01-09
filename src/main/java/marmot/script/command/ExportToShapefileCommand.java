package marmot.script.command;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import groovy.lang.Closure;
import marmot.MarmotRuntime;
import marmot.externio.shp.ExportDataSetAsShapefile;
import marmot.externio.shp.ExportShapefileParameters;
import marmot.script.ScriptParsingObject;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ExportToShapefileCommand extends ScriptParsingObject
											implements ScriptCommand<Long> {
	private final String m_dsId;
	private final String m_shpPath;
	private ExportShapefileParameters m_params = ExportShapefileParameters.create();
	private long m_progressInterval = -1;
	private boolean m_force = false;
	
	public ExportToShapefileCommand(MarmotRuntime marmot, String dsId, String shpPath,
									Map<String,Object> args, Closure<?> optDecl) {
		super(marmot);
		
		m_dsId = dsId;
		m_shpPath = shpPath;
		
		getStringOption(args, "charset").ifPresent(m_params::charset);
		getStringOption(args, "shpSrid").ifPresent(m_params::shpSrid);
		getStringOption(args, "typeName").ifPresent(m_params::typeName);
		getOption(args, "splitSize").map(ScriptUtils::parseByteLength)
									.ifPresent(m_params::splitSize);
		getLongOption(args, "progressInterval").ifPresent(cnt -> m_progressInterval = cnt);
		getBooleanOption(args, "force").ifPresent(f -> m_force = f);
		
		if ( optDecl != null ) {
			ScriptUtils.callClosure(optDecl, this);
		}
	}
	
	public ExportToShapefileCommand charset(String charsetName) {
		m_params.charset(charsetName);
		return this;
	}
	
	public ExportToShapefileCommand typeName(String name) {
		m_params.typeName(name);
		return this;
	}
	
	public ExportToShapefileCommand shpSrid(String srid) {
		m_params.shpSrid(srid);
		return this;
	}
	
	public ExportToShapefileCommand splitSize(Object szExpr) {
		long sz = ScriptUtils.parseByteLength(szExpr);
		m_params.splitSize(sz);
		return this;
	}
	
	public ExportToShapefileCommand reportInterval(long intvl) {
		m_progressInterval = intvl;
		return this;
	}
	
	public ExportToShapefileCommand force(boolean flag) {
		m_force = flag;
		return this;
	}

	@Override
	public Long execute() throws ExecutionException, InterruptedException, IOException {
		ExportDataSetAsShapefile cmd = ExportDataSetAsShapefile.create(m_dsId, m_shpPath, m_params);
		if ( m_progressInterval > 0 ) {
			cmd.setProgressInterval(m_progressInterval);
		}
		cmd.setForce(m_force);
		
		return cmd.start(m_marmot).get();
	}
	
	@Override
	public String toString() {
		return String.format("%s: ds='%s', shpFile='%s'",
							getClass().getSimpleName(), m_dsId, m_shpPath);
	}
}
