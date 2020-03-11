package marmot.script.dslobj;

import com.vividsolutions.jts.geom.Envelope;

import groovy.lang.GroovyObjectSupport;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.RecordSchema;
import marmot.RecordSet;
import marmot.dataset.DataSet;
import marmot.dataset.DataSetType;
import marmot.dataset.GeometryColumnInfo;
import marmot.geo.catalog.IndexNotFoundException;
import marmot.geo.catalog.SpatialIndexInfo;
import marmot.geo.command.CreateSpatialIndexOptions;
import marmot.geo.query.RangeQueryEstimate;
import utils.func.FOption;
import utils.func.Lazy;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class GDataSet extends GroovyObjectSupport implements DataSet {
	private final MarmotRuntime m_marmot;
	private final String m_dsId;
	private final DataSet m_ds;
	
	public GDataSet(MarmotRuntime marmot, String dsId) {
		m_marmot = marmot;
		m_dsId = dsId;
		
		m_ds = Lazy.wrap(() -> m_marmot.getDataSet(m_dsId), DataSet.class);
	}

	@Override
	public String getId() {
		return m_dsId;
	}

	@Override
	public RecordSchema getRecordSchema() {
		return m_ds.getRecordSchema();
	}

	@Override
	public DataSetType getType() {
		return m_ds.getType();
	}
	
	@Override
	public String getDirName() {
		return m_ds.getDirName();
	}

	@Override
	public boolean hasGeometryColumn() {
		return m_ds.hasGeometryColumn();
	}

	@Override
	public GeometryColumnInfo getGeometryColumnInfo() {
		return m_ds.getGeometryColumnInfo();
	}

	@Override
	public Envelope getBounds() {
		return m_ds.getBounds();
	}

	@Override
	public long getRecordCount() {
		return m_ds.getRecordCount();
	}

	@Override
	public String getHdfsPath() {
		return m_ds.getHdfsPath();
	}

	@Override
	public long getBlockSize() {
		return m_ds.getBlockSize();
	}

	@Override
	public FOption<String> getCompressionCodecName() {
		return m_ds.getCompressionCodecName();
	}

	@Override
	public FOption<SpatialIndexInfo> getSpatialIndexInfo() {
		return m_ds.getSpatialIndexInfo();
	}

	@Override
	public long length() {
		return m_ds.length();
	}

	@Override
	public DataSet updateGeometryColumnInfo(FOption<GeometryColumnInfo> gcInfo) {
		return m_ds.updateGeometryColumnInfo(gcInfo);
	}

	@Override
	public RecordSet read() {
		return new GRecordSet(m_ds.read());
	}

	@Override
	public RangeQueryEstimate estimateRangeQuery(Envelope range) {
		return m_ds.estimateRangeQuery(range);
	}

	@Override
	public RecordSet queryRange(Envelope range, int nsamples) {
		return new GRecordSet(m_ds.queryRange(range, nsamples));
	}

	@Override
	public long append(RecordSet rset) {
		return m_ds.append(rset);
	}

	@Override
	public long append(RecordSet rset, String partId) {
		return m_ds.append(rset, partId);
	}

	@Override
	public SpatialIndexInfo createSpatialIndex(CreateSpatialIndexOptions opts) {
		return m_ds.createSpatialIndex(opts);
	}

	@Override
	public void deleteSpatialIndex() {
		m_ds.deleteSpatialIndex();
	}

	@Override
	public RecordSet readSpatialCluster(String quadKey) {
		return new GRecordSet(m_ds.readSpatialCluster(quadKey));
	}

	@Override
	public void createThumbnail(int sampleCount) throws IndexNotFoundException {
		m_ds.createThumbnail(sampleCount);
	}

	@Override
	public boolean deleteThumbnail() {
		return m_ds.deleteThumbnail();
	}

	@Override
	public FOption<Float> getThumbnailRatio() {
		return m_ds.getThumbnailRatio();
	}
	
	@Override
	public String toString() {
		return String.format("GDataSet(%s)", m_dsId);
	}
}
