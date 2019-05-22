package marmot.script.plan.operator;

import marmot.optor.geo.SquareGrid;
import marmot.proto.optor.LoadSquareGridFileProto;
import marmot.proto.optor.OperatorProto;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class LoadSquareGridFactory extends GroovyOperatorFactory<LoadSquareGridFactory> {
	private LoadSquareGridFileProto.Builder m_builder;
	
	public LoadSquareGridFactory(SquareGrid grid) {
		m_builder = LoadSquareGridFileProto.newBuilder()
											.setGrid(grid.toProto());
	}
	
	public LoadSquareGridFactory splitCount(int count) {
		m_builder.setSplitCount(count);
		
		return this;
	}
	
	@Override
	public OperatorProto create() {		
		LoadSquareGridFileProto load = m_builder.build();
		
		return OperatorProto.newBuilder().setLoadSquareGridfile(load).build();
	}
}
