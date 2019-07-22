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
	protected CsvParameters m_options = CsvParameters.create();
	
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
	
	public CsvParametersParser delim(String delim) {
		m_options.delimiter(delim.charAt(0));
		return this;
	}
	
	public CsvParametersParser quote(String quote) {
		m_options.quote(quote.charAt(0));
		return this;
	}
	
	public CsvParametersParser escape(String escape) {
		m_options.escape(escape.charAt(0));
		return this;
	}
	
	public CsvParametersParser commentMarker(String marker) {
		m_options.commentMarker(marker);
		return this;
	}
	
	public CsvParametersParser charset(String charset) {
		m_options.charset(charset);
		return this;
	}
	
	public CsvParametersParser headerFirst(boolean flag) {
		m_options.headerFirst(flag);
		return this;
	}
	
	public CsvParametersParser header(String header) {
		m_options.header(header);
		return this;
	}
	
	public CsvParametersParser pointColumns(String pointCols) {
		m_options.pointColumns(pointCols);
		return this;
	}
	
	public CsvParametersParser srid(String srid) {
		m_options.srid(srid);
		return this;
	}
	
	public CsvParametersParser trimColumns(boolean flag) {
		m_options.trimColumns(flag);
		return this;
	}
	
	public CsvParametersParser nullValue(String str) {
		m_options.nullValue(str);
		return this;
	}
}
