package marmot.script.plan.operator;

import marmot.proto.optor.OperatorProto;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public interface OperatorFactory {
	public OperatorProto create();
}
