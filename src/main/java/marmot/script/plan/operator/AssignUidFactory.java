package marmot.script.plan.operator;

import marmot.proto.optor.AssignUidProto;
import marmot.proto.optor.OperatorProto;
import marmot.script.GroovyDslClass;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class AssignUidFactory extends GroovyDslClass implements OperatorFactory {
	private final String m_geomCol;
	
	public AssignUidFactory(String geomCol) {
		m_geomCol = geomCol;
	}

	@Override
	public OperatorProto create() {
		AssignUidProto assign = AssignUidProto.newBuilder()
											.setUidColumn(m_geomCol)
											.build();
		
		return OperatorProto.newBuilder().setAssignUid(assign).build();
	}
}
