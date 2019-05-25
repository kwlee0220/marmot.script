package marmot.script.plan;

import com.vividsolutions.jts.geom.Envelope;

import marmot.optor.geo.SquareGrid;
import marmot.script.GroovyDslClass;
import marmot.script.dslobj.GDataSet;
import utils.Size2d;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class SquareGridParser extends GroovyDslClass {
	private Envelope m_envl;
	private String m_boundsDsId;
	private Size2d m_cellSize;
	
	public SquareGridParser bounds(Envelope bounds) {
		m_envl = bounds;
		
		return this;
	}
	
	public SquareGridParser bounds(GDataSet dsRef) {
		m_boundsDsId = dsRef.getId();
		
		return this;
	}
	
	public SquareGridParser cellSize(String str) {
		m_cellSize = Size2d.fromString(str);
		
		return this;
	}
	
	public SquareGrid parse() {
		if ( m_envl != null ) {
			return new SquareGrid(m_envl, m_cellSize);
		}
		else if ( m_boundsDsId != null ) {
			return new SquareGrid(m_boundsDsId, m_cellSize);
		}
		else {
			throw new IllegalArgumentException("SquareGrid is missing");
		}
	}
}
