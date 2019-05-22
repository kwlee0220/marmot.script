package marmot.script.plan.operator;

import java.util.Map;

import javax.annotation.Nullable;

import marmot.RecordScript;
import marmot.proto.optor.DefineColumnProto;
import marmot.proto.optor.OperatorProto;
import marmot.script.GroovyDslClass;
import marmot.script.ScriptUtils;
import utils.Utilities;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class DefineColumnFactory extends GroovyDslClass implements OperatorFactory {
	private final DefineColumnProto.Builder m_builder;
	@Nullable private RecordScript m_initValue = null;
	
	public DefineColumnFactory(String colDecl) {
		Utilities.checkNotNullArgument(colDecl, "colDecl is null");
		
		m_builder = DefineColumnProto.newBuilder()
									.setColumnDecl(colDecl);
	}
	
	public DefineColumnFactory set(String expr) {
		Utilities.checkNotNullArgument(expr, "initial value expr is null");
		
		m_initValue = RecordScript.of(expr);
		return this;
	}
	
	public DefineColumnFactory set(Map<String,Object> args, String expr) {
		Utilities.checkNotNullArgument(expr, "initializer is null");
		Utilities.checkNotNullArgument(args, "args is null");
		
		String initializer = ScriptUtils.getOrThrow(args, "initializer").toString();
		m_initValue = RecordScript.of(initializer, expr);
		return this;
	}

	@Override
	public OperatorProto create() {
		if ( m_initValue != null ) {
			m_builder.setColumnInitializer(m_initValue.toProto());
		}
		DefineColumnProto define = m_builder.build();
		
		return OperatorProto.newBuilder().setDefineColumn(define).build();
	}
}
