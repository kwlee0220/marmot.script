package marmot.script.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FileUtils;

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
public class ExportToCsvFileCommand extends ScriptParsingObject implements ScriptCommand<Long> {
	private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
	
	private final String m_dsId;
	private final String m_csvPath;
	private CsvParametersParser m_parser;
	
	public ExportToCsvFileCommand(MarmotRuntime marmot, String dsId, String csvPath,
									Map<String,Object> args, Closure<?> optDecl) {
		super(marmot);
		
		m_dsId = dsId;
		m_csvPath = csvPath;
		
		m_parser = CsvParametersParser.from(args);
		if ( optDecl != null ) {
			ScriptUtils.callClosure(optDecl, m_parser);
		}
	}

	@Override
	public Long execute() throws ExecutionException, InterruptedException, IOException {
		CsvParameters params = m_parser.getParsed();
		
		ExportAsCsv cmd = new ExportAsCsv(m_dsId, params);
		if ( m_parser.progressInterval() > 0 ) {
			cmd.reportInterval(m_parser.progressInterval());
		}
		
		FOption<String> output = FOption.ofNullable(m_csvPath);
		if ( m_parser.force() ) {
			output.map(File::new)
					.filter(File::exists)
					.ifPresentOrThrow(FileUtils::forceDelete);
		}
		Charset charset = params.charset().getOrElse(DEFAULT_CHARSET);
		BufferedWriter writer = ExternIoUtils.toWriter(output, charset);
		return new ExportAsCsv(m_dsId, params).run(m_marmot, writer);
	}
	
	@Override
	public String toString() {
		return String.format("%s: ds='%s', shpFile='%s'",
							getClass().getSimpleName(), m_dsId, m_csvPath);
	}
}