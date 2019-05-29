package marmot.script.command;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public interface ScriptCommand<T> {
	public T execute() throws Exception;
}
