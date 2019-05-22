package marmot.script.plan.operator;

import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.PickTopKProto;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class PickTopKFactory extends GroovyOperatorFactory<PickTopKFactory> {
	private final int m_count;
	private String m_orderColsExpr;
	
	public PickTopKFactory(int count) {
		m_count = count;
	}
	
	public PickTopKFactory orderBy(String orderColsExpr) {
		m_orderColsExpr = orderColsExpr;
		return this;
	}

	@Override
	public OperatorProto create() {
		PickTopKProto pick = PickTopKProto.newBuilder()
											.setSortKeyColumns(m_orderColsExpr)
											.setTopK(m_count)
											.build();
		return OperatorProto.newBuilder().setPickTopK(pick).build();
	}
}
