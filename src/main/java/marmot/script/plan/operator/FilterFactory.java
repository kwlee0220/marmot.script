package marmot.script.plan.operator;

import marmot.RecordScript;
import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.ScriptFilterProto;
import marmot.script.GroovyDslClass;
import utils.Utilities;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class FilterFactory extends GroovyDslClass implements OperatorFactory {
	private final RecordScript m_pred;
	
	public FilterFactory(RecordScript pred) {
		Utilities.checkNotNullArgument(pred, "predicate is null");
		
		m_pred = pred;
	}
	
//	public FilterFactory initializer(String expr) {
//		Utilities.checkNotNullArgument(expr, "initializer is null");
//		
//		m_script.setInitializer(expr);
//		return this;
//	}

	@Override
	public OperatorProto create() {
		ScriptFilterProto filter = ScriptFilterProto.newBuilder()
											.setPredicate(m_pred.toProto())
											.build();
		return OperatorProto.newBuilder().setFilterScript(filter).build();
	}
}
