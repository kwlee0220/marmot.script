package marmot.script.plan.operator;

import marmot.RecordScript;
import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.UpdateProto;
import utils.Utilities;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class UpdateFactory extends GroovyOperatorFactory<UpdateFactory> {
	private final RecordScript m_script;
	
	public UpdateFactory(RecordScript script) {
		Utilities.checkNotNullArgument(script, "update expr is null");
		
		m_script = script;
	}

	@Override
	public OperatorProto create() {
		UpdateProto update = UpdateProto.newBuilder()
										.setScript(m_script.toProto())
										.build();
		return OperatorProto.newBuilder().setUpdate(update).build();
	}
}
