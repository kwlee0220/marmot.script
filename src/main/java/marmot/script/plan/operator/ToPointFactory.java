package marmot.script.plan.operator;

import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.ToGeometryPointProto;
import marmot.script.GroovyDslClass;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ToPointFactory extends GroovyDslClass implements OperatorFactory {
	private final String m_geomCol;
	private final ToGeometryPointProto.Builder m_builder;
	
	public ToPointFactory(String geomCol) {
		m_geomCol = geomCol;
		m_builder = ToGeometryPointProto.newBuilder()
										.setOutColumn(m_geomCol);
	}
	
	public ToPointFactory from(Object[] args) {
		m_builder.setXColumn((String)args[0]);
		m_builder.setYColumn((String)args[1]);
		return this;
	}
	
	public ToPointFactory x_col(String colName) {
		m_builder.setXColumn(colName);
		return this;
	}
	
	public ToPointFactory y_col(String colName) {
		m_builder.setYColumn(colName);
		return this;
	}

	@Override
	public OperatorProto create() {
		ToGeometryPointProto trans = m_builder.build();
		
		return OperatorProto.newBuilder().setToPoint(trans).build();
	}
}
