package marmot.script.plan.operator;

import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.SortProto;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class SortFactory extends GroovyOperatorFactory<SortFactory> {
	private final String m_sortColsExpr;
	
	public SortFactory(String sortColsExpr) {
		m_sortColsExpr = sortColsExpr;
	}

	@Override
	public OperatorProto create() {
		SortProto pick = SortProto.newBuilder()
									.setSortColumns(m_sortColsExpr)
									.build();
		return OperatorProto.newBuilder().setSort(pick).build();
	}
}
