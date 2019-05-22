package marmot.script.plan.operator;

import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.ProjectProto;
import marmot.script.GroovyDslClass;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ProjectFactory extends GroovyDslClass implements OperatorFactory {
	private ProjectProto m_project;

	public ProjectFactory(String colExpr) {
		m_project = ProjectProto.newBuilder()
								.setColumnExpr(colExpr)
								.build();
	}

	@Override
	public OperatorProto create() {
		return OperatorProto.newBuilder().setProject(m_project).build();
	}
}
