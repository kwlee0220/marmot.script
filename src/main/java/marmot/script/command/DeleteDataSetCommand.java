package marmot.script.command;

import marmot.MarmotRuntime;
import marmot.script.GroovyDslClass;
import utils.stream.FStream;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class DeleteDataSetCommand extends GroovyDslClass
									implements ScriptCommand<Void> {
	private final MarmotRuntime m_marmot;
	private String[] m_dsIds;
	
	public DeleteDataSetCommand(MarmotRuntime marmot, String... ids) {
		m_marmot = marmot;
		m_dsIds = ids;
	}

	@Override
	public Void execute() {
		for ( String id: m_dsIds ) {
			m_marmot.deleteDataSet(id);
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		String idsStr = FStream.of(m_dsIds).map(id ->  String.format("'%s'", id)).join(",");
		return String.format("delete dataset '%s'", idsStr);
	}
}
