package marmot.script;

import java.io.File;

import marmot.command.MarmotClientCommands;
import marmot.remote.protobuf.PBMarmotClient;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class Test {
	public static final void main(String... args) throws Exception {
		PBMarmotClient marmot = MarmotClientCommands.connect();
		
		MarmotScriptEngine engine = new MarmotScriptEngine(marmot);
		engine.setVerbose(true);
		
		File file = new File("sample.mcs");
		engine.evaluate(file);
	}
}
