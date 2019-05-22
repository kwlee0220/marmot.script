package marmot.script.plan.operator;

import marmot.plan.JdbcConnectOptions;
import marmot.proto.optor.OperatorProto;
import marmot.proto.optor.StoreIntoJdbcTableProto;
import utils.func.FOption;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class StoreIntoJdbcTableFactory extends GroovyOperatorFactory<StoreIntoJdbcTableFactory> {
	private StoreIntoJdbcTableProto.Builder m_builder;
	private FOption<String> m_valueExpr = FOption.empty();
	
	public StoreIntoJdbcTableFactory(String tblName, JdbcConnectOptions jdbcOpts) {
		m_builder = StoreIntoJdbcTableProto.newBuilder()
											.setTableName(tblName)
											.setJdbcOptions(jdbcOpts.toProto());
	}
	
	public StoreIntoJdbcTableFactory valueExpr(String expr) {
		m_valueExpr = FOption.of(expr);
		return this;
	}

	@Override
	public OperatorProto create() {
		m_valueExpr.ifPresent(m_builder::setValuesExpr);
		StoreIntoJdbcTableProto store = m_builder.build();
		
		return OperatorProto.newBuilder().setStoreIntoJdbcTable(store).build();
	}
}
