package marmot.script;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;

import io.vavr.CheckedConsumer;
import marmot.MarmotRuntime;
import marmot.command.MarmotClientCommands;
import marmot.command.MarmotConnector;
import marmot.command.UsageHelp;
import marmot.remote.protobuf.PBMarmotClient;
import picocli.CommandLine;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class RemoteRunScriptMain implements CheckedConsumer<MarmotRuntime> {
	@Mixin private MarmotConnector m_connector;
	@Mixin private UsageHelp m_help;

	@Parameters(paramLabel="path", index="0", arity="0..1",
				description="plan file path to run")
	private String m_scriptFile;
	
	@Option(names={"-v"}, paramLabel="verbose", description={"display command progress"})
	private boolean m_verbose;

	public static final void main(String... args) throws Exception {
//		MarmotClientCommands.configureLog4j();
		MarmotClientCommands.disableLog4j();
		
		RemoteRunScriptMain cmd = new RemoteRunScriptMain();
		CommandLine commandLine = new CommandLine(cmd);
		
		try {
			commandLine.parse(args);
			
			if ( commandLine.isUsageHelpRequested() ) {
				commandLine.usage(System.out, Ansi.OFF);
			}
			else {
				// 원격 MarmotServer에 접속.
				PBMarmotClient marmot = cmd.m_connector.connect();
				cmd.accept(marmot);
			}
		}
		catch ( Exception e ) {
			System.err.printf("failed: %s%n%n", e);
			commandLine.usage(System.out, Ansi.OFF);
		}
	}

	@Override
	public void accept(MarmotRuntime marmot) throws Exception {
		MarmotScriptEngine scriptEngine = new MarmotScriptEngine(marmot);
		scriptEngine.setVerbose(m_verbose);
		
		if ( m_scriptFile != null ) {
			scriptEngine.evaluate(new File(m_scriptFile));
		}
		else {
			Reader reader = new InputStreamReader(System.in);
			scriptEngine.evaluate(reader);
		}
	}
}
