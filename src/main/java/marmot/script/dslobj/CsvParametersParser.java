package marmot.script.dslobj;

import static marmot.script.ScriptUtils.getBooleanOption;
import static marmot.script.ScriptUtils.getStringOption;

import java.util.Map;

import groovy.lang.GroovyObjectSupport;
import marmot.externio.csv.CsvParameters;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class CsvParametersParser extends GroovyObjectSupport {
	protected CsvParameters m_params = CsvParameters.create();
	private long m_progressInterval = -1;
	private boolean m_force = false;
	
	public static CsvParametersParser from(Map<String,Object> args) {
		CsvParametersParser parser = new CsvParametersParser();
		
		getStringOption(args, "delim").ifPresent(parser::delim);
		getStringOption(args, "quote").ifPresent(parser::quote);
		getStringOption(args, "escape").ifPresent(parser::escape);
		getStringOption(args, "commentMarker").ifPresent(parser::commentMarker);
		getStringOption(args, "charset").ifPresent(parser::charset);
		getStringOption(args, "header").ifPresent(parser::header);
		getBooleanOption(args, "headerFirst").ifPresent(parser::headerFirst);
		getStringOption(args, "pointColumns").ifPresent(parser::pointColumns);
		getStringOption(args, "srid").ifPresent(parser::srid);
		getBooleanOption(args, "trimColumns").ifPresent(parser::trimColumns);
		getStringOption(args, "nullValue").ifPresent(parser::nullValue);
		
		return parser;
	}
	
	public CsvParameters getParsed() {
		return m_params;
	}
	
	@Override
	public Object getProperty(String name) {
		switch ( name ) {
			case "headerFirst":
				return headerFirst(true);
			case "trimColumns":
				return trimColumns(true);
		}
		
		return super.getProperty(name);
	}
	
	@Override
	public void setProperty(String name, Object value) {
		switch ( name ) {
			case "headerFirst":
				headerFirst((Boolean)value);
				break;
			case "trimColumns":
				trimColumns((Boolean)value);
				break;
		}
		
		super.setProperty(name, value);
	}
	
	public long progressInterval() {
		return m_progressInterval;
	}
	
	public CsvParametersParser reportInterval(long intvl) {
		m_progressInterval = intvl;
		return this;
	}
	
	public boolean force() {
		return m_force;
	}
	
	public CsvParametersParser force(boolean flag) {
		m_force = flag;
		return this;
	}
	
	public CsvParametersParser delim(String delim) {
		m_params.delimiter(delim.charAt(0));
		return this;
	}
	
	public CsvParametersParser quote(String quote) {
		m_params.quote(quote.charAt(0));
		return this;
	}
	
	public CsvParametersParser escape(String escape) {
		m_params.escape(escape.charAt(0));
		return this;
	}
	
	public CsvParametersParser commentMarker(String marker) {
		m_params.commentMarker(marker);
		return this;
	}
	
	public CsvParametersParser charset(String charset) {
		m_params.charset(charset);
		return this;
	}
	
	public CsvParametersParser headerFirst(boolean flag) {
		m_params.headerFirst(flag);
		return this;
	}
	
	public CsvParametersParser header(String header) {
		m_params.header(header);
		return this;
	}
	
	public CsvParametersParser pointColumns(String pointCols) {
		m_params.pointColumns(pointCols);
		return this;
	}
	
	public CsvParametersParser srid(String srid) {
		m_params.srid(srid);
		return this;
	}
	
	public CsvParametersParser trimColumns(boolean flag) {
		m_params.trimColumns(flag);
		return this;
	}
	
	public CsvParametersParser nullValue(String str) {
		m_params.nullValue(str);
		return this;
	}
}
