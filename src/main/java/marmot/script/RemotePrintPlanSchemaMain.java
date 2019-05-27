package marmot.script;

import java.io.File;

import io.vavr.CheckedConsumer;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.RecordSchema;
import marmot.command.MarmotClientCommands;
import marmot.command.MarmotConnector;
import marmot.command.UsageHelp;
import marmot.remote.protobuf.PBMarmotClient;
import marmot.script.plan.PlanScriptParser;
import picocli.CommandLine;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class RemotePrintPlanSchemaMain implements CheckedConsumer<MarmotRuntime> {
	@Mixin private MarmotConnector m_connector;
	@Mixin private UsageHelp m_help;

	@Parameters(paramLabel="path", index="0", arity="0..1",
				description="plan file path to run")
	private String m_planFile;

	public static final void main(String... args) throws Exception {
		MarmotClientCommands.disableLog4j();
		
		RemotePrintPlanSchemaMain cmd = new RemotePrintPlanSchemaMain();
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
