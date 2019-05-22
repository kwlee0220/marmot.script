package marmot.script.plan.operator;

import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.ShardProto;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ShardFactory extends GroovyOperatorFactory<ShardFactory> {
	private final int m_count;
	
	public ShardFactory(int count) {
		m_count = count;
	}

	@Override
	public OperatorProto create() {
		ShardProto shard = ShardProto.newBuilder()
									.setPartCount(m_count)
									.build();
		
		return OperatorProto.newBuilder().setShard(shard).build();
	}
}
