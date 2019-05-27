package marmot.script.dslobj;

import java.util.List;

import com.vividsolutions.jts.geom.Envelope;

import groovy.lang.GroovyObjectSupport;
import io.vavr.Lazy;
import marmot.DataSet;
import marmot.DataSetType;
import marmot.GeometryColumnInfo;
import marmot.InsufficientThumbnailException;
import marmot.MarmotRuntime;
import marmot.Plan;
import marmot.RecordSchema;
import marmot.RecordSet;
import marmot.SpatialClusterInfo;
import marmot.ThumbnailNotFoundException;
import marmot.geo.catalog.IndexNotFoundException;
import marmot.geo.catalog.SpatialIndexInfo;
import marmot.geo.command.ClusterDataSetOptions;
import utils.func.FOption;

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
		
		m_ds = Lazy.val(() -> m_marmot.getDataSet(m_dsId), DataSet.class);
	}

	@Override
	public String getId() {
		return m_dsId;
	}

	@Override
	public MarmotRuntime getMarmotRuntime() {
		return m_marmot;
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
	public boolean isCompressed() {
		return m_ds.isCompressed();
	}

	@Override
	public FOption<SpatialIndexInfo> getDefaultSpatialIndexInfo() {
		return m_ds.getDefaultSpatialIndexInfo();
	}

	@Override
	public long length() {
		return m_ds.length();
	}

	@Override
	public RecordSet read() {
		return m_ds.read();
	}

	@Override
	public RecordSet queryRange(Envelope range, FOption<String> filterExpr) {
		return m_ds.queryRange(range, filterExpr);
	}

	@Override
	public long append(RecordSet rset) {
		return m_ds.append(rset);
	}

	@Override
	public long append(RecordSet rset, Plan plan) {
		return m_ds.append(rset, plan);
	}

	@Override
	public SpatialIndexInfo cluster(ClusterDataSetOptions opts) {
		return m_ds.cluster(opts);
	}

	@Override
	public void deleteSpatialCluster() {
		m_ds.deleteSpatialCluster();
	}

	@Override
	public List<SpatialClusterInfo> querySpatialClusterInfo(Envelope bounds) {
		return m_ds.querySpatialClusterInfo(bounds);
	}

	@Override
	public RecordSet readSpatialCluster(String quadKey) {
		return m_ds.readSpatialCluster(quadKey);
	}

	@Override
	public boolean hasThumbnail() {
		return m_ds.hasThumbnail();
	}

	@Override
	public RecordSet readThumbnail(Envelope bounds, int count)
			throws ThumbnailNotFoundException, InsufficientThumbnailException {
		return m_ds.readThumbnail(bounds, count);
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
	public float getThumbnailRatio() throws ThumbnailNotFoundException {
		return m_ds.getThumbnailRatio();
	}
	
	@Override
	public String toString() {
		return String.format("GDataSet(%s)", m_dsId);
	}
}
