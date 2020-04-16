package marmot.script;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;

import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.RecordSchema;
import marmot.command.MarmotClientCommand;
import marmot.command.MarmotClientCommands;
import marmot.command.PicocliCommands.SubCommand;
import marmot.script.RemoteScriptMain.Run;
import marmot.script.plan.PlanScriptParser;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
@Command(name="mc_script",
		parameterListHeading = "Parameters:%n",
		optionListHeading = "Options:%n",
		description="script-related commands",
		subcommands = {
			Run.class,
		})
public class RemoteScriptMain extends MarmotClientCommand {
	public static final void main(String... args) throws Exception {
		MarmotClientCommands.configureLog4j();

		RemoteScriptMain cmd = new RemoteScriptMain();
		CommandLine.run(cmd, System.out, System.err, Help.Ansi.OFF, args);
	}

	@Command(name="run", description="run mamort script")
	public static class Run extends SubCommand<MarmotRuntime> {
		@Parameters(paramLabel="path", index="0", arity="0..1", description="plan file path to run")
		private String m_scriptFile;
		
		@Option(names={"-v"}, paramLabel="verbose", description={"display command progress"})
		private boolean m_verbose;

		@Override
		public void run(MarmotRuntime marmot) throws Exception {
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

	@Command(name="run", description="run mamort script")
	public static class Schema extends SubCommand<MarmotRuntime> {
		@Parameters(paramLabel="path", index="0", arity="0..1", description="plan file path to run")
		private String m_planFile;

		@Override
		public void run(MarmotRuntime marmot) throws Exception {
			PlanScriptParser parser = new PlanScriptParser(marmot);
			
			Plan plan = (m_planFile != null)
						? parser.parse("sample_plan", new File(m_planFile))
						: parser.parse("sample_plan", System.in);
			RecordSchema schema = marmot.getOutputRecordSchema(plan);
			
			System.out.println("SCHEMA:");
			schema.getColumns()
					.stream()
					.forEach(c -> System.out.println("\t" + c));
		}
	}
}
