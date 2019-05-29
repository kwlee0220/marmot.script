package marmot.script;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;

import org.codehaus.groovy.control.CompilerConfiguration;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import marmot.MarmotRuntime;
import utils.io.IOUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class MarmotScriptEngine {
	private final MarmotRuntime m_marmot;
	private final CompilerConfiguration m_config;
	private final Binding m_binding;
	
	public MarmotScriptEngine(MarmotRuntime marmot) throws URISyntaxException, IOException {
		m_marmot = marmot;
		
		m_binding = new Binding();
		m_binding.setProperty("MARMOT", m_marmot);
		m_binding.setProperty("MARMOT_VERBOSE", false);
		
		m_config = new CompilerConfiguration();
		m_config.setScriptBaseClass(CommandScriptHandler.class.getName());
	}
	
	public MarmotScriptEngine setVerbose(boolean flag) {
		m_binding.setProperty("MARMOT_VERBOSE", flag);
		return this;
	}
	
	public void evaluate(File script) throws IOException {
		evaluate(IOUtils.toString(script));
	}
	
	public void evaluate(String script) {
		new GroovyShell(m_binding, m_config).evaluate(script);
	}
	
	public void evaluate(Reader reader) throws IOException {
		new GroovyShell(m_binding, m_config).evaluate(reader);
	}
}
