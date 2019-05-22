package marmot.script.plan;

import java.util.Map;

import marmot.proto.optor.GroupByKeyProto;
import marmot.script.GroovyDslClass;
import utils.func.FOption;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class GroupParser extends GroovyDslClass {
	private String m_keyCols;
	private FOption<String> m_tagCols = FOption.empty();
	private FOption<String> m_orderKeyCols = FOption.empty();
	private FOption<Integer> m_workerCount = FOption.empty();
	
	public GroupByKeyProto parse() {
		GroupByKeyProto.Builder builder = GroupByKeyProto.newBuilder()
													.setCompareColumns(m_keyCols);
		m_tagCols.ifPresent(builder::setTagColumns);
		m_orderKeyCols.ifPresent(builder::setOrderColumns);
		m_workerCount.ifPresent(cnt -> builder.setGroupWorkerCount(cnt));
		return builder.build();
	}
	
	public static GroupByKeyProto parse(Map<String,Object> info) {
		GroupParser parser = new GroupParser();

		info.computeIfPresent("keys", (k,v) -> parser.keys((String)v));
		info.computeIfPresent("tags", (k,v) -> parser.tags((String)v));
		info.computeIfPresent("orderBy", (k,v) -> parser.orderBy((String)v));
		info.computeIfPresent("workerCount", (k,v) -> parser.workerCount((Integer)v));
		
		return parser.parse();
	}
	
	public GroupParser keys(String keyCols) {
		m_keyCols = keyCols;
		return this;
	}
	
	public String keys() {
		return m_keyCols;
	}
	
	public GroupParser tags(String tagCols) {
		m_tagCols = FOption.of(tagCols);
		return this;
	}
	
	public FOption<String> tags() {
		return m_tagCols;
	}
	
	public GroupParser orderBy(String orderCols) {
		m_orderKeyCols = FOption.of(orderCols);
		return this;
	}
	
	public FOption<String> orderBy() {
		return m_orderKeyCols;
	}
	
	public GroupParser workerCount(int count) {
		m_workerCount = FOption.of(count);
		return this;
	}
	
	public FOption<Integer> workerCount() {
		return m_workerCount;
	}
}
