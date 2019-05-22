package marmot.script.plan;

import java.io.File;

import marmot.Plan;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class TestPlanScript {
	public static final void main(String... args) throws Exception {
		PlanScriptParser parser = new PlanScriptParser();
		Plan plan = parser.parse("sample_plan", new File("sample_plan.mps"));
		
		System.out.println(plan.toJson());
	}
}
