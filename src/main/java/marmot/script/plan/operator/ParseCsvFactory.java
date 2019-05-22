package marmot.script.plan.operator;

import marmot.optor.ParseCsvOptions;
import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.ParseCsvProto;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ParseCsvFactory extends GroovyOperatorFactory<ParseCsvFactory> {
	private ParseCsvProto.Builder m_builder;
	private ParseCsvOptions m_options = ParseCsvOptions.create();
	
	public ParseCsvFactory(String colName) {
		m_builder = ParseCsvProto.newBuilder().setCsvColumn(colName);
	}
	
	@Override
	public Object getProperty(String name) {
		switch ( name ) {
			case "trimColumn":
				return trimColumn(true);
			case "throwParseError":
				return throwParseError(true);
		}
		
		return super.getProperty(name);
	}
	
	@Override
	public void setProperty(String name, Object value) {
		switch ( name ) {
			case "trimColumn":
				trimColumn((Boolean)value);
				return;
			case "throwParseError":
				throwParseError((Boolean)value);
				return;
		}
		
		super.setProperty(name, value);
	}
	
	public ParseCsvFactory delim(String delim) {
		m_options.delimiter(delim.charAt(0));
		return this;
	}
	
	public ParseCsvFactory quote(String quote) {
		m_options.quote(quote.charAt(0));
		return this;
	}
	
	public ParseCsvFactory escape(String esc) {
		m_options.escape(esc.charAt(0));
		return this;
	}
	
	public ParseCsvFactory trimColumn(boolean flag) {
		m_options.trimColumns(flag);
		return this;
	}
	
	public ParseCsvFactory throwParseError(boolean flag) {
		m_options.throwParseError(flag);
		return this;
	}
	
	public ParseCsvFactory nullValue(String value) {
		m_options.nullValue(value);
		return this;
	}
	
	public ParseCsvFactory header(String header) {
		m_options.header(header);
		return this;
	}

	@Override
	public OperatorProto create() {
		m_builder.setOptions(m_options.toProto());
		ParseCsvProto parse = m_builder.build();
		
		return OperatorProto.newBuilder().setParseCsv(parse).build();
	}
}
