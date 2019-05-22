package marmot.script.plan.operator;

import java.util.Map;

import javax.annotation.Nullable;

import marmot.RecordScript;
import marmot.proto.optor.ExpandProto;
import marmot.proto.optor.OperatorProto;
import marmot.script.GroovyDslClass;
import marmot.script.ScriptUtils;
import utils.Utilities;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ExpandFactory extends GroovyDslClass implements OperatorFactory {
	private final ExpandProto.Builder m_builder;
	@Nullable private RecordScript m_expandExpr = null;
	
	public ExpandFactory(String colDecls) {
		Utilities.checkNotNullArgument(colDecls, "colDecls is null");
		
		m_builder = ExpandProto.newBuilder()
									.setColumnDecls(colDecls);
	}
	
	public ExpandFactory with(String expr) {
		Utilities.checkNotNullArgument(expr, "expand expr is null");
		
		m_expandExpr = RecordScript.of(expr);
		return this;
	}
	
	public ExpandFactory with(Map<String,Object> args, String expr) {
		Utilities.checkNotNullArgument(expr, "initializer is null");
		Utilities.checkNotNullArgument(args, "args is null");
		
		String initializer = ScriptUtils.getOrThrow(args, "initializer").toString();
		m_expandExpr = RecordScript.of(initializer, expr);
		return this;
	}

	@Override
	public OperatorProto create() {
		if ( m_expandExpr != null ) {
			m_builder.setColumnInitializer(m_expandExpr.toProto());
		}
		ExpandProto define = m_builder.build();
		
		return OperatorProto.newBuilder().setExpand(define).build();
	}
}
