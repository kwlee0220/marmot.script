package marmot.script.dslobj;

import static marmot.script.ScriptUtils.getStringOption;

import java.util.Map;

import groovy.lang.GroovyObjectSupport;
import marmot.externio.shp.ShapefileParameters;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ShapefileParametersParser extends GroovyObjectSupport {
	protected ShapefileParameters m_options = ShapefileParameters.create();
	
	public static ShapefileParametersParser from(Map<String,Object> args) {
		ShapefileParametersParser parser = new ShapefileParametersParser();
		
		getStringOption(args, "charset").ifPresent(parser::charset);
		getStringOption(args, "srid").ifPresent(parser::srid);
		
		return parser;
	}
	
	public ShapefileParametersParser charset(String charset) {
		m_options.charset(charset);
		return this;
	}
	
	public ShapefileParametersParser srid(String srid) {
		m_options.srid(srid);
		return this;
	}
}
