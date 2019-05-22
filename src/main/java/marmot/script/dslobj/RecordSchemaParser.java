package marmot.script.dslobj;

import marmot.RecordSchema;
import marmot.script.GroovyDslClass;
import marmot.type.DataType;
import marmot.type.DataTypes;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class RecordSchemaParser extends GroovyDslClass {
	private RecordSchema.Builder m_builder = RecordSchema.builder();
	
	@Override
	public Object getProperty(String name) {
		switch ( name ) {
			case "polygon":
			case "point":
			case "multi_polygon":
			case "line_string":
			case "multi_point":
			case "multi_line_string":
			case "geometry":
			case "geom_collection":
			case "envelope":
				return name;
		}
		
		return super.getProperty(name);
	}
	
	public Object invokeMethod(String name, Object args) {
		Object typeObj = getArgs(args, 0);
		DataType type = DataTypes.fromName(typeObj.toString());
		m_builder.addColumn(name, type);
		
		return null;
	}
	
	public RecordSchema parse() {
		return m_builder.build();
	}
}
