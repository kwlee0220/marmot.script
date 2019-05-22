package marmot.script.plan;

import java.util.Map;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

import groovy.lang.Closure;
import marmot.optor.geo.SquareGrid;
import marmot.script.GroovyDslClass;
import marmot.script.ScriptUtils;
import marmot.script.dslobj.Column;
import marmot.script.dslobj.DataSet;
import marmot.script.dslobj.JdbcTable;
import marmot.script.dslobj.TextFile;
import utils.Size2d;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
class DslTagParser extends GroovyDslClass {
	public final DataSet dataset(String dsId) {
		return new DataSet(dsId);
	}
	
	public final TextFile textFile(String path) {
		return new TextFile(path);
	}
	
	public final JdbcTable jdbcTable(String tblName, Closure decl) {
		return new JdbcTable(tblName, decl);
	}
	
	public final Column column(String name) {
		return new Column(name);
	}
	
	public Envelope envelope(Map<String,Double> coords) {
		return ScriptUtils.parseEnvelope(coords);
	}
	
	public Geometry wkt(String wktStr) throws ParseException {
		return ScriptUtils.parseWkt(wktStr);
	}
	
	public double distance(Object distObj) {
		return ScriptUtils.parseDistance(distObj);
	}
	
	public SquareGrid squareGrid(Map<String,Object> info) {
		Size2d cellSize = ScriptUtils.parseSize2d(info.get("cellSize"));
		Object boundsObj = info.get("bounds");
		if ( boundsObj instanceof Envelope )  {
			return new SquareGrid((Envelope)boundsObj, cellSize);
		}
		else if ( boundsObj instanceof DataSet )  {
			return new SquareGrid(((DataSet)boundsObj).getId(), cellSize);
		}
		else {
			throw new IllegalArgumentException("invalid SquareGrid.bounds");
		}
	}
	
	
	
//	public final RecordScript predicate(Map<String,Object> args, String expr) {
//		String initializer = GroovyDslClass.getOrThrow(args, "initializer").toString();
//		return RecordScript.of(expr, initializer);
//	}
}
