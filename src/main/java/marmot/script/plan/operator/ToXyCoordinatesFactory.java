package marmot.script.plan.operator;

import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.ToXYCoordinatesProto;
import marmot.script.GroovyDslClass;
import marmot.script.dslobj.Keyword;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ToXyCoordinatesFactory extends GroovyDslClass implements OperatorFactory {
	private final String m_geomCol;
	private final ToXYCoordinatesProto.Builder m_builder;
	
	public ToXyCoordinatesFactory(String geomCol) {
		m_geomCol = geomCol;
		m_builder = ToXYCoordinatesProto.newBuilder()
										.setGeomColumn(m_geomCol);
	}
	
	public ToXyCoordinatesFactory to(Object[] args) {
		m_builder.setXColumn((String)args[0]);
		m_builder.setYColumn((String)args[1]);
		return this;
	}
	
	public ToXyCoordinatesFactory x_col(String colName) {
		m_builder.setXColumn(colName);
		return this;
	}
	
	public ToXyCoordinatesFactory y_col(String colName) {
		m_builder.setYColumn(colName);
		return this;
	}
	
	public ToXyCoordinatesFactory ignore(Keyword dummy) {
		m_builder.setKeepGeomColumn(false);
		return this;
	}
	
	public ToXyCoordinatesFactory keep(Keyword dummy) {
		m_builder.setKeepGeomColumn(true);
		return this;
	}

	@Override
	public OperatorProto create() {
		ToXYCoordinatesProto trans = m_builder.build();
		
		return OperatorProto.newBuilder().setToXYCoordinates(trans).build();
	}
}
