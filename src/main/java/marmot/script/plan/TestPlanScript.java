package marmot.script.plan;

import java.io.File;

import marmot.Plan;
import marmot.command.MarmotClientCommands;
import marmot.remote.protobuf.PBMarmotClient;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class TestPlanScript {
	public static final void main(String... args) throws Exception {
		PBMarmotClient marmot = MarmotClientCommands.connect();
		
		PlanScriptParser parser = new PlanScriptParser(marmot);
		Plan plan = parser.parse("sample_plan", new File("sample_plan.mps"));
		
		System.out.println(plan.toJson(false));
	}
}
