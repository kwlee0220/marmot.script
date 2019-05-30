package marmot.script.command;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import groovy.lang.Closure;
import marmot.MarmotRuntime;
import marmot.externio.ExternIoUtils;
import marmot.externio.csv.CsvParameters;
import marmot.externio.csv.ExportAsCsv;
import marmot.script.ScriptParsingObject;
import marmot.script.ScriptUtils;
import marmot.script.dslobj.CsvParametersParser;
import utils.func.FOption;

/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class ExportDataSetAsCsvCommand extends ScriptParsingObject
											implements ScriptCommand<Long> {
	private final String m_dsId;
	private final String m_csvPath;
	private CsvParameters m_params = CsvParameters.create();
	private long m_progressInterval = -1;
	private boolean m_force = false;
	
	public ExportDataSetAsCsvCommand(MarmotRuntime marmot, String dsId, String csvPath,
									Map<String,Object> args, Closure<?> optDecl) {
		super(marmot);
		
		m_dsId = dsId;
		m_csvPath = csvPath;
		
		getLongOption(args, "progressInterval").ifPresent(cnt -> m_progressInterval = cnt);
		getBooleanOption(args, "force").ifPresent(f -> m_force = f);
		
		CsvParametersParser parser = CsvParametersParser.from(args);
		if ( optDecl != null ) {
			ScriptUtils.callClosure(optDecl, parser);
		}
	}
	
	public ExportDataSetAsCsvCommand reportInterval(long intvl) {
		m_progressInterval = intvl;
		return this;
	}
	
	public ExportDataSetAsCsvCommand force(boolean flag) {
		m_force = flag;
		return this;
	}

	@Override
	public Long execute() throws ExecutionException, InterruptedException, IOException {
		ExportAsCsv cmd = new ExportAsCsv(m_dsId, m_params);
		if ( m_progressInterval > 0 ) {
			cmd.reportInterval(m_progressInterval);
		}
		
		FOption<String> output = FOption.ofNullable(m_csvPath);
		BufferedWriter writer = ExternIoUtils.toWriter(output, m_params.charset().get());
		return new ExportAsCsv(m_dsId, m_params).run(m_marmot, writer);
	}
	
	@Override
	public String toString() {
		return String.format("%s: ds='%s', shpFile='%s'",
							getClass().getSimpleName(), m_dsId, m_csvPath);
	}
}
