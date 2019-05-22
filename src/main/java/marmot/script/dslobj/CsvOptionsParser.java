package marmot.script.dslobj;

import marmot.externio.csv.CsvParameters;
import marmot.script.GroovyDslClass;
import marmot.support.DataUtils;
import utils.UnitUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class CsvOptionsParser extends GroovyDslClass {
	protected CsvParameters m_options = CsvParameters.create();
	
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
	
	public CsvOptionsParser delim(String delim) {
		m_options.delimiter(delim.charAt(0));
		return this;
	}
	
	public CsvOptionsParser quote(String quote) {
		m_options.quote(quote.charAt(0));
		return this;
	}
	
	public CsvOptionsParser escape(String escape) {
		m_options.escape(escape.charAt(0));
		return this;
	}
	
	public CsvOptionsParser commentMarker(String marker) {
		m_options.commentMarker(marker);
		return this;
	}
	
	public CsvOptionsParser charset(String charset) {
		m_options.charset(charset);
		return this;
	}
	
	public CsvOptionsParser headerFirst(boolean flag) {
		m_options.headerFirst(flag);
		return this;
	}
	
	public CsvOptionsParser header(Keyword first) {
		return headerFirst(true);
	}
	
	public CsvOptionsParser header(String header) {
		m_options.header(header);
		return this;
	}
	
	public CsvOptionsParser pointColumns(String pointCols) {
		m_options.pointColumns(pointCols);
		return this;
	}
	
	public CsvOptionsParser csvSrid(String srid) {
		m_options.srid(srid);
		return this;
	}
	
	public CsvOptionsParser trimColumns(boolean flag) {
		m_options.trimColumns(flag);
		return this;
	}
	
	public CsvOptionsParser trim(Keyword column) {
		return trimColumns(true);
	}
	
	public CsvOptionsParser nullValue(String str) {
		m_options.nullValue(str);
		return this;
	}
	
	public CsvOptionsParser maxColumnLength(Object obj) {
		int length;
		if ( obj instanceof String ) {
			length = (int)UnitUtils.parseByteSize((String)obj);
		}
		else if ( obj instanceof Number ) {
			length = DataUtils.asInt(obj);
		}
		else {
			throw new IllegalArgumentException("invalid length object: " + obj);
		}
		m_options.maxColumnLength(length);
		return this;
	}
}
