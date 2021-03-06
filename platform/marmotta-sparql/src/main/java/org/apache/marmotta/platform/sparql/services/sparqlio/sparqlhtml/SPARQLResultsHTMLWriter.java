/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.marmotta.platform.sparql.services.sparqlio.sparqlhtml;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.marmotta.platform.core.api.templating.TemplatingService;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryResultHandlerException;
import org.openrdf.query.TupleQueryResultHandlerException;
import org.openrdf.query.resultio.QueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultFormat;
import org.openrdf.query.resultio.TupleQueryResultWriter;
import org.openrdf.rio.RioSetting;
import org.openrdf.rio.WriterConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SPARQL results to HTML writer usong Freemarker
 * 
 * @author Sergio Fernández
 */
public class SPARQLResultsHTMLWriter implements TupleQueryResultWriter {

	private static final Logger log = LoggerFactory.getLogger(SPARQLResultsHTMLWriter.class);
    
    private static final String START_TEMPLATE = "sparql_select_start.ftl";

	private static final String RESULT_TEMPLATE = "sparql_select_result.ftl";

	private static final String END_TEMPLATE = "sparql_select_end.ftl";
	
    private OutputStream out;
    
    private List<String> vars;
    
    private TemplatingService templatingService;
    
    private WriterConfig config;
    
    public SPARQLResultsHTMLWriter(OutputStream out, TemplatingService templatingService) {
        this.out = out;
        this.templatingService = templatingService;
    }

	@Override
	public TupleQueryResultFormat getTupleQueryResultFormat() {
		return new TupleQueryResultFormat("SPARQL/HTML", "text/html", Charset.forName("UTF-8"), "html");
	}

	@Override
	public void startQueryResult(List<String> vars) throws TupleQueryResultHandlerException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("vars", vars);
        this.vars = vars;
        try {
            templatingService.process(SPARQLResultsHTMLWriter.class, START_TEMPLATE, data, new OutputStreamWriter(out));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TupleQueryResultHandlerException(e);
        }
	}
	
	@Override
	public void handleSolution(BindingSet binding) throws TupleQueryResultHandlerException {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("vars", vars);
        Map<String, String> result = new HashMap<String, String>();
        for (String var: vars) {
        	if (binding.hasBinding(var)) {
        		result.put(var, binding.getBinding(var).getValue().stringValue());
        	} else {
        		result.put(var, "");
        	}
        }
        data.put("result", result);
        try {
            templatingService.process(SPARQLResultsHTMLWriter.class, RESULT_TEMPLATE, data, new OutputStreamWriter(out));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TupleQueryResultHandlerException(e);
        }
	}	
	
	@Override
	public void endQueryResult() throws TupleQueryResultHandlerException {
		Map<String, Object> data = new HashMap<String, Object>();
        try {            
            templatingService.process(SPARQLResultsHTMLWriter.class, END_TEMPLATE, data, new OutputStreamWriter(out));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TupleQueryResultHandlerException(e);
        }
	}

	@Override
	public void handleBoolean(boolean arg0) throws QueryResultHandlerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleLinks(List<String> arg0)
			throws QueryResultHandlerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public QueryResultFormat getQueryResultFormat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void handleNamespace(String prefix, String uri)
			throws QueryResultHandlerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startDocument() throws QueryResultHandlerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleStylesheet(String stylesheetUrl)
			throws QueryResultHandlerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startHeader() throws QueryResultHandlerException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endHeader() throws QueryResultHandlerException {
		// TODO Auto-generated method stub
		
	}

    /**
     * @return A collection of {@link RioSetting}s that are supported by this
     *         RDFWriter.
     * @since 2.7.0
     */
	@Override
	public Collection<RioSetting<?>> getSupportedSettings() {
		return new ArrayList<RioSetting<?>>();
	}

    /**
     * Retrieves the current writer configuration as a single object.
     * 
     * @return a writer configuration object representing the current
     *         configuration of the writer.
     * @since 2.7.0
     */
	@Override
	public WriterConfig getWriterConfig() {
		return config;
	}

    /**
     * Sets all supplied writer configuration options.
     * 
     * @param config
     *        a writer configuration object.
     * @since 2.7.0
     */
	@Override
	public void setWriterConfig(WriterConfig config) {
		this.config = config;
	}

}
