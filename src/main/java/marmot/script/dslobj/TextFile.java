package marmot.script.dslobj;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class TextFile {
	private String m_path;
	
	public TextFile(String path) {
		m_path = path;
	}
	
	public String getPath() {
		return m_path;
	}
	
	@Override
	public String toString() {
		return String.format("textfile(%s)", m_path);
	}
}
