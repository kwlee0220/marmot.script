package marmot.script.plan;

import marmot.MarmotRuntime;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class GPlanBuilder extends PlanDslHandler {
	public GPlanBuilder(MarmotRuntime marmot, String name) {
		super(marmot, name);
	}

	@Override
	public Object run() {
		throw new AssertionError();
	}
}
