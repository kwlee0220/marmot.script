package marmot.script.command;

import java.io.File;

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
									implements MarmotScriptCommand<Long> {
	private final MarmotRuntime m_marmot;
	private final String m_shpPath;
	private ShapefileParameters m_params = ShapefileParameters.create();
	private ImportParameters m_importParams = new ImportParameters();
	
	public ImportShapefileCommand(MarmotRuntime marmot, String shpPath) {
		m_marmot = marmot;
		m_shpPath = shpPath;
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
	
	public ImportShapefileCommand to(String dsId) {
		m_importParams.setDataSetId(dsId);
		return this;
	}
	
	public ImportShapefileCommand geometry(String str) {
		m_importParams.setGeometryColumnInfo(GeometryColumnInfo.fromString(str));
		return this;
	}
	
	public ImportShapefileCommand options(Closure script) {
		ScriptUtils.callClosure(script, this);
		return this;
	}
	
	public ImportShapefileCommand srid(String srid) {
		m_params.shpSrid(srid);
		return this;
	}
	
	public ImportShapefileCommand charset(String charset) {
		m_params.charset(charset);
		return this;
	}
	
	public ImportShapefileCommand force(boolean flag) {
		m_importParams.setForce(flag);
		return this;
	}

	@Override
	public Long execute() {
		return ImportShapefile.from(new File(m_shpPath), m_params, m_importParams)
								.run(m_marmot);
	}
	
	@Override
	public String toString() {
		return String.format("import_shapefile '%s' into '%s'",
							m_shpPath, m_importParams.getDataSetId());
	}
}
