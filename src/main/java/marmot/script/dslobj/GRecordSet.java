package marmot.script.dslobj;

import marmot.Record;
import marmot.RecordSchema;
import marmot.RecordSet;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class GRecordSet implements RecordSet {
	private final RecordSet m_rset;
	
	public GRecordSet(RecordSet rset) {
		m_rset = rset;
	}

	@Override
	public RecordSchema getRecordSchema() {
		return m_rset.getRecordSchema();
	}

	@Override
	public void close() {
		m_rset.close();
	}

	@Override
	public boolean next(Record output) {
		return m_rset.next(output);
	}

	@Override
	public Record nextCopy() {
		Record rec = m_rset.nextCopy();
		return rec != null ? new GRecord(rec) : null;
	}
}
