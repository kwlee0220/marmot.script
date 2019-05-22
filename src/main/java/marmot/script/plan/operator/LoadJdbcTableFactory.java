package marmot.script.plan.operator;

import marmot.plan.JdbcConnectOptions;
import marmot.plan.LoadJdbcTableOptions;
import marmot.proto.optor.LoadJdbcTableProto;
import marmot.proto.optor.OperatorProto;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class LoadJdbcTableFactory extends GroovyOperatorFactory<LoadJdbcTableFactory> {
	private LoadJdbcTableProto.Builder m_builder;
	private LoadJdbcTableOptions m_options = LoadJdbcTableOptions.create();
	
	public LoadJdbcTableFactory(String tblName, JdbcConnectOptions jdbcOpts) {
		m_builder = LoadJdbcTableProto.newBuilder()
										.setTableName(tblName)
										.setJdbcOptions(jdbcOpts.toProto());
	}
	
	public LoadJdbcTableFactory selectExpr(String expr) {
		m_options.selectExpr(expr);
		return this;
	}
	
	public LoadJdbcTableFactory mapperCount(int count) {
		m_options.mapperCount(count);
		return this;
	}

	@Override
	public OperatorProto create() {
		m_builder.setOptions(m_options.toProto());
		LoadJdbcTableProto load = m_builder.build();
		
		return OperatorProto.newBuilder().setLoadJdbcTable(load).build();
	}
}
