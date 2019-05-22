package marmot.script.plan.operator;

import java.util.Map;

import groovy.lang.Closure;
import marmot.optor.StoreAsCsvOptions;
import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.StoreAsCsvProto;
import marmot.script.GroovyDslClass;
import marmot.script.ScriptUtils;
import utils.Utilities;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class StoreAsCsvFactory extends GroovyDslClass implements OperatorFactory {
	private final String m_path;
	private final StoreAsCsvOptions m_options;
	
	public StoreAsCsvFactory(String path) {
		m_path = path;
		m_options = StoreAsCsvOptions.create();
	}
	
	public StoreAsCsvFactory options(Closure opts) {
		ScriptUtils.callClosure(opts, this);
		return this;
	}
	
	public StoreAsCsvFactory options(Map options) {
		Utilities.ifNotNull((String)options.get("delim"), delim -> m_options.delimiter(delim.charAt(0)));
		Utilities.ifNotNull((String)options.get("quote"), quote -> m_options.quote(quote.charAt(0)));
		Utilities.ifNotNull((String)options.get("escape"), esc -> m_options.escape(esc.charAt(0)));
		Utilities.ifNotNull((String)options.get("charset"), cs -> m_options.charset(cs));
		Utilities.ifNotNull((Boolean)options.get("headerFirst"), cs -> m_options.headerFirst(cs));
		
		return this;
	}
	
	public StoreAsCsvFactory delim(String delim) {
		m_options.delimiter(delim.charAt(0));
		return this;
	}
	
	public StoreAsCsvFactory quote(String quote) {
		m_options.quote(quote.charAt(0));
		return this;
	}
	
	public StoreAsCsvFactory escape(String esc) {
		m_options.escape(esc.charAt(0));
		return this;
	}
	
	public StoreAsCsvFactory charset(String charsetName) {
		m_options.charset(charsetName);
		return this;
	}
	
	public StoreAsCsvFactory headerFirst(boolean flag) {
		m_options.headerFirst(flag);
		return this;
	}

	@Override
	public OperatorProto create() {
		StoreAsCsvProto store = StoreAsCsvProto.newBuilder()
												.setPath(m_path)
												.setOptions(m_options.toProto())
												.build();
		return OperatorProto.newBuilder().setStoreAsCsv(store).build();
	}
}
