package marmot.script.dslobj;

import static marmot.script.ScriptUtils.getBooleanOption;
import static marmot.script.ScriptUtils.getOption;

import java.util.Map;

import groovy.lang.GroovyObjectSupport;
import marmot.dataset.GeometryColumnInfo;
import marmot.optor.StoreDataSetOptions;
import marmot.script.ScriptUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class StoreDataSetOptionsParser extends GroovyObjectSupport {
	protected StoreDataSetOptions m_options = StoreDataSetOptions.DEFAULT;
	
	public static StoreDataSetOptions parse(Map<String,Object> args) {
		StoreDataSetOptions opts = StoreDataSetOptions.DEFAULT;
		
		opts = getBooleanOption(args, "force").transform(opts, StoreDataSetOptions::force);
		opts = getOption(args, "geometry")
				.cast(String.class)
				.map(GeometryColumnInfo::fromString)
				.transform(opts, StoreDataSetOptions::geometryColumnInfo);
		
		return opts;
	}
	
	public StoreDataSetOptionsParser force(boolean flag) {
		m_options = m_options.append(flag);
		return this;
	}
	
	public StoreDataSetOptionsParser geometry(String str) {
		m_options = m_options.geometryColumnInfo(GeometryColumnInfo.fromString(str));
		return this;
	}
	
	public StoreDataSetOptionsParser blockSize(Object obj) {
		m_options = m_options.blockSize(ScriptUtils.parseByteLength(obj));
		return this;
	}
	
	public StoreDataSetOptionsParser compressionCodecName(String codecName) {
		m_options = m_options.compressionCodecName(codecName);
		return this;
	}
	
	public StoreDataSetOptionsParser geometry(Object obj) {
		if ( obj instanceof String ) {
			m_options = m_options.geometryColumnInfo(GeometryColumnInfo.fromString((String)obj));
		}
		else if ( obj instanceof GeometryColumnInfo ) {
			m_options = m_options.geometryColumnInfo((GeometryColumnInfo)obj);
		}
		else {
			throw new IllegalArgumentException("incorrect GeometryColumnInfo: " + obj);
		}
		
		return this;
	}
}
