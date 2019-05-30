package marmot.script.plan;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;

import org.codehaus.groovy.control.CompilerConfiguration;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.PlanBuilder;
import utils.io.IOUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class PlanScriptParser {
	private final MarmotRuntime m_marmot;
	private final Binding m_binding;
	private final CompilerConfiguration m_config;
	
	public PlanScriptParser(MarmotRuntime marmot) throws URISyntaxException, IOException {
		m_marmot = marmot;
		m_binding = new Binding();
		m_binding.setProperty("MARMOT", m_marmot);
		
		m_config = new CompilerConfiguration();
		m_config.setScriptBaseClass(PlanDslHandler.class.getName());
	}
	
	public Plan parse(String name, File dslScript) throws IOException {
		return parse(name, IOUtils.toString(dslScript));
	}
	
	public Plan parse(String name, String script) {
		m_binding.setProperty("MARMOT_PLAN_NAME", name);
		return ((PlanBuilder)new GroovyShell(m_binding, m_config).evaluate(script)).build();
	}
	
	public Plan parse(String name, Reader reader) throws IOException {
		m_binding.setProperty("MARMOT_PLAN_NAME", name);
		return ((PlanBuilder)new GroovyShell(m_binding, m_config).evaluate(reader)).build();
	}
	
	public Plan parse(String name, InputStream is) throws IOException {
		try ( Reader reader = new InputStreamReader(is) ) {
			return parse(name, reader);
		}
	}
}
