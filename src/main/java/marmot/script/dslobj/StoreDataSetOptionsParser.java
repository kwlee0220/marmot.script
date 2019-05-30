package marmot.script.dslobj;

import static marmot.script.ScriptUtils.getBooleanOption;
import static marmot.script.ScriptUtils.getOption;

import java.util.Map;

import groovy.lang.GroovyObjectSupport;
import marmot.GeometryColumnInfo;
import marmot.StoreDataSetOptions;
import marmot.script.ScriptUtils;
import utils.func.FOption;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class StoreDataSetOptionsParser extends GroovyObjectSupport {
	protected StoreDataSetOptions m_options = StoreDataSetOptions.create();
	
	public static StoreDataSetOptions parse(Map<String,Object> args) {
		StoreDataSetOptions opts = StoreDataSetOptions.create();
		
		getBooleanOption(args, "force").ifPresent(opts::force);
		
		FOption<Object> gcInfo = getOption(args, "geometry");
		gcInfo.ifPresent(info -> {
			if ( info instanceof String ) {
				opts.geometryColumnInfo(GeometryColumnInfo.fromString((String)info));
			}
			else if ( info instanceof GeometryColumnInfo ) {
				opts.geometryColumnInfo((GeometryColumnInfo)info);
			}
			else {
				throw new IllegalArgumentException("incorrect GeometryColumnInfo: " + info);
			}
		});
		
		return opts;
	}
	
	public StoreDataSetOptionsParser force(boolean flag) {
		m_options.append(flag);
		return this;
	}
	
	public StoreDataSetOptionsParser geometry(String str) {
		m_options.geometryColumnInfo(GeometryColumnInfo.fromString(str));
		return this;
	}
	
	public StoreDataSetOptionsParser blockSize(Object obj) {
		m_options.blockSize(ScriptUtils.parseByteLength(obj));
		return this;
	}
	
	public StoreDataSetOptionsParser compression(boolean flag) {
		m_options.compression(flag);
		return this;
	}
	
	public StoreDataSetOptionsParser geometry(Object obj) {
		if ( obj instanceof String ) {
			m_options.geometryColumnInfo(GeometryColumnInfo.fromString((String)obj));
		}
		else if ( obj instanceof GeometryColumnInfo ) {
			m_options.geometryColumnInfo((GeometryColumnInfo)obj);
		}
		else {
			throw new IllegalArgumentException("incorrect GeometryColumnInfo: " + obj);
		}
		
		return this;
	}
}
