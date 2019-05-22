package marmot.script.plan;

import marmot.script.GroovyDslClass;
import marmot.support.DataUtils;
import utils.Size2d;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class Size2dParser extends GroovyDslClass {
	private double m_width;
	private double m_height;
	
	public Size2dParser width(Object obj) {
		m_width = DataUtils.asDouble(obj);
		return this;
	}
	
	public Size2dParser height(Object obj) {
		m_height = DataUtils.asDouble(obj);
		return this;
	}
	
	public Size2d parse() {
		return new Size2d(m_width, m_height);
	}
}
