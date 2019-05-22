package marmot.script.plan.operator;

import marmot.proto.optor.DistinctProto;
import marmot.proto.optor.OperatorProto;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class DistinctFactory extends GroovyOperatorFactory<DistinctFactory> {
	private final String m_keyCols;
	private int m_workerCount = -1;
	
	public DistinctFactory(String keyCols) {
		m_keyCols = keyCols;
	}
	
	public DistinctFactory workerCount(int count) {
		m_workerCount = count;
		
		return this;
	}

	@Override
	public OperatorProto create() {
		if ( m_workerCount <= 0 ) {
			throw new IllegalArgumentException("workerCount is not specified");
		}
		
		DistinctProto distinct = DistinctProto.newBuilder()
											.setKeyColumns(m_keyCols)
											.setWorkerCount(m_workerCount)
											.build();
		return OperatorProto.newBuilder().setDistinct(distinct).build();
	}
}
