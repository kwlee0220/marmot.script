package marmot.script.plan;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import groovy.lang.GroovyShell;
import marmot.Plan;
import utils.io.IOUtils;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class PlanScriptParser {
	private static final String PATH = "/marmot/script/plan/GPlanScriptParser.script";
	private final String m_bootUpScript;
	
	public PlanScriptParser() throws URISyntaxException, IOException {
		URI uri = PlanScriptParser.class.getResource(PATH).toURI();
		m_bootUpScript = IOUtils.toString(new File(uri));
	}
	
	public Plan parse(String name, File dslScript) throws IOException {
		return parse(name, IOUtils.toString(dslScript));
	}
	
	public Plan parse(String name, String dslScript) {
		String script = String.format("%s%n%nplan('%s'){%n%s%n}", m_bootUpScript, name, dslScript);
		return (Plan)new GroovyShell().evaluate(script);
	}
	
	public void parse(InputStream is) throws IOException {
		Vector<InputStream> streams = new Vector<>();

		String prefix = String.format("s%n%nplan('%s'){%n", m_bootUpScript);
		streams.add(new ByteArrayInputStream(prefix.getBytes()));
		streams.add(is);
		streams.add(new ByteArrayInputStream("}".getBytes()));
		
		try ( InputStream tagged = new SequenceInputStream(streams.elements());
				Reader reader = new InputStreamReader(tagged) ) {
			new GroovyShell().evaluate(reader, "GPlanScriptParser");
		}
	}
}
