package marmot.script.dslobj;

import java.util.Map;

import marmot.ColumnNotFoundException;
import marmot.Record;
import marmot.RecordSchema;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class GRecord implements Record {
	private final Record m_record;
	
	public GRecord(Record rec) {
		m_record = rec;
	}
	
	public Object getAt(String name) {
		return get(name);
	}

	@Override
	public RecordSchema getRecordSchema() {
		return m_record.getRecordSchema();
	}

	@Override
	public Object get(int index) {
		return m_record.get(index);
	}

	@Override
	public Object get(String name) throws ColumnNotFoundException {
		return m_record.get(name);
	}

	@Override
	public Object[] getAll() {
		return m_record.getAll();
	}

	@Override
	public Record set(String name, Object value) throws ColumnNotFoundException {
		return m_record.set(name, value);
	}

	@Override
	public Record set(int idx, Object value) {
		return m_record.set(idx, value);
	}

	@Override
	public Record set(Record src) {
		return m_record.set(src);
	}

	@Override
	public Record set(Map<String, Object> values) {
		return m_record.set(values);
	}

	@Override
	public Record setAll(Iterable<?> values) {
		return m_record.setAll(values);
	}

	@Override
	public void clear() {
		m_record.clear();
	}

	@Override
	public Record duplicate() {
		return new GRecord(m_record.duplicate());
	}

}
