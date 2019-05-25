package marmot.script;

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
import utils.io.IOUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class MarmotScriptEngine {
	private static final String RUNNER_PATH = "/marmot/script/command/GCommandScriptBootUp.script";
	private static final String PARSER_PATH = "/marmot/script/command/GCommandScriptParser.script";
	private final MarmotRuntime m_marmot;
	private final Binding m_binding;
	private final String m_bootUpScript;
	private final String m_parserScript;
	
	public MarmotScriptEngine(MarmotRuntime marmot) throws URISyntaxException, IOException {
		m_marmot = marmot;
		
		m_binding = new Binding();
		m_binding.setProperty("marmot", m_marmot);
		m_binding.setProperty("MARMOT_VERBOSE", false);
		
		try ( InputStream is = MarmotScriptEngine.class.getResourceAsStream(RUNNER_PATH) ) {
			m_bootUpScript = IOUtils.toString(is, StandardCharsets.UTF_8);
		}
		try ( InputStream is = MarmotScriptEngine.class.getResourceAsStream(PARSER_PATH) ) {
			m_parserScript = IOUtils.toString(is, StandardCharsets.UTF_8);
		}
	}
	
	public MarmotScriptEngine setVerbose(boolean flag) {
		m_binding.setProperty("MARMOT_VERBOSE", flag);
		return this;
	}
	
	public void evaluate(File script) throws IOException {
		evaluate(IOUtils.toString(script));
	}
	
	public void evaluate(String script) {
		String extendedScript = String.format("%s%n%nscript {%n%s%n}",
												m_bootUpScript, script);
		new GroovyShell(m_binding).evaluate(extendedScript);
	}
	
	public void evaluate(InputStream is) throws IOException {
		Vector<InputStream> streams = new Vector<>();

		String prefix = String.format("%s%n%nscript {%n", m_bootUpScript);
		streams.add(new ByteArrayInputStream(prefix.getBytes()));
		streams.add(is);
		streams.add(new ByteArrayInputStream("}".getBytes()));
		
		try ( InputStream tagged = new SequenceInputStream(streams.elements());
				Reader reader = new InputStreamReader(tagged) ) {
			new GroovyShell(m_binding).evaluate(reader, "GCommandScriptBootUp");
		}
	}
	
	public void validate(File script) throws IOException {
		validate(IOUtils.toString(script));
	}
	
	public void validate(String script) {
		String extendedScript = String.format("%s%n%nscript {%n%s%n}", m_parserScript, script);
		new GroovyShell(m_binding).evaluate(extendedScript);
	}
	
	public void validate(InputStream is) throws IOException {
		Vector<InputStream> streams = new Vector<>();
		String prefix = String.format("%s%n%nscript {%n", m_parserScript);
		streams.add(new ByteArrayInputStream(prefix.getBytes()));
		streams.add(is);
		streams.add(new ByteArrayInputStream("}".getBytes()));
		
		try ( InputStream tagged = new SequenceInputStream(streams.elements());
				Reader reader = new InputStreamReader(tagged) ) {
			new GroovyShell(m_binding).evaluate(reader, "GCommandScriptParser");
		}
	}
}
