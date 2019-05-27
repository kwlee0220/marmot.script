package marmot.script.plan;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.script.MarmotScriptEngine;
import utils.io.IOUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class PlanScriptParser {
	private static final String PATH = "/marmot/script/plan/GPlanScriptParser.script";
	
	private final MarmotRuntime m_marmot;
	private final Binding m_binding;
	private final String m_bootUpScript;
	
	public PlanScriptParser(MarmotRuntime marmot) throws URISyntaxException, IOException {
		m_marmot = marmot;
		m_binding = new Binding();
		m_binding.setProperty("marmot", m_marmot);
		
		try ( InputStream is = MarmotScriptEngine.class.getResourceAsStream(PATH) ) {
			m_bootUpScript = IOUtils.toString(is, StandardCharsets.UTF_8);
		}
	}
	
	public Plan parse(String name, File dslScript) throws IOException {
		return parse(name, IOUtils.toString(dslScript));
	}
	
	public Plan parse(String name, String dslScript) {
		String script = String.format("%s%n%nplan('%s'){%n%s%n}", m_bootUpScript, name, dslScript);
		return (Plan)new GroovyShell(m_binding).evaluate(script, "GPlanScriptParser");
	}
	
	public Plan parse(String name, InputStream is) throws IOException {
		Vector<InputStream> streams = new Vector<>();

		String prefix = String.format("%s%n%nplan('%s'){%n", m_bootUpScript, name);
		streams.add(new ByteArrayInputStream(prefix.getBytes()));
		streams.add(is);
		streams.add(new ByteArrayInputStream("}".getBytes()));
		
		try ( InputStream tagged = new SequenceInputStream(streams.elements());
				Reader reader = new InputStreamReader(tagged) ) {
			return (Plan)new GroovyShell(m_binding).evaluate(reader, "GPlanScriptParser");
		}
	}
}
