package marmot.script.plan.operator;

import marmot.optor.geo.SquareGrid;
import marmot.proto.optor.AssignSquareGridCellProto;
import marmot.proto.optor.OperatorProto;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class AssignSquareGridFactory extends GroovyOperatorFactory<AssignSquareGridFactory> {
	private AssignSquareGridCellProto.Builder m_builder;
	
	public AssignSquareGridFactory(SquareGrid grid) {
		m_builder = AssignSquareGridCellProto.newBuilder()
											.setGrid(grid.toProto())
											.setIgnoreOutside(true);
	}
	
	public Object getProperty(String name) {
		switch ( name ) {
			case "ignoreOutside":
				ignoreOutside(true);
				return this;
		}
		
		return super.getProperty(name);
	}
	
	public AssignSquareGridFactory to(String geomCol) {
		m_builder.setGeometryColumn(geomCol);
		
		return this;
	}
	
	public AssignSquareGridFactory ignoreOutside(boolean flag) {
		m_builder.setIgnoreOutside(flag);
		
		return this;
	}
	
	@Override
	public OperatorProto create() {		
		AssignSquareGridCellProto assign = m_builder.build();
		
		return OperatorProto.newBuilder().setAssignSquareGridCell(assign).build();
	}
}
