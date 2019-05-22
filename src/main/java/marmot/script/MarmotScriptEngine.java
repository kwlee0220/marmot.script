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
import java.util.Scanner;
import java.util.Vector;

import groovy.lang.Binding;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyShell;
import marmot.MarmotRuntime;
import utils.io.IOUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class MarmotScriptEngine {
	private static final String PATH = "/marmot/script/command/GCommandScriptBootUp.script";
	private final MarmotRuntime m_marmot;
	private final Binding m_binding;
	private final String m_bootUpScript;
	
	public MarmotScriptEngine(MarmotRuntime marmot) throws URISyntaxException, IOException {
		m_marmot = marmot;
		
		m_binding = new Binding();
		m_binding.setProperty("marmot", m_marmot);
		
		try ( InputStream is = MarmotScriptEngine.class.getResourceAsStream(PATH) ) {
			m_bootUpScript = IOUtils.toString(is, StandardCharsets.UTF_8);
		}
	}
	
	public void evaluate(File script) throws IOException {
		evaluate(IOUtils.toString(script));
	}
	
	public void evaluate(String script) {
		String extendedScript = String.format("%s%n%nrun {%n%s%n}",
												m_bootUpScript, script);
		new GroovyShell(m_binding).evaluate(extendedScript);
	}
	
	public void evaluate(InputStream is) throws IOException {
		Vector<InputStream> streams = new Vector<>();

		String prefix = String.format("%s%n%nrun {%n", m_bootUpScript);
		streams.add(new ByteArrayInputStream(prefix.getBytes()));
		streams.add(is);
		streams.add(new ByteArrayInputStream("}".getBytes()));
		
		try ( InputStream tagged = new SequenceInputStream(streams.elements());
				Reader reader = new InputStreamReader(tagged) ) {
			new GroovyShell(m_binding).evaluate(reader, "GCommandScriptBootUp");
		}
	}
}
