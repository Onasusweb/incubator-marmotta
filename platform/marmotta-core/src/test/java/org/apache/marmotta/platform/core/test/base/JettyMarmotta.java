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
package org.apache.marmotta.platform.core.test.base;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

import org.apache.marmotta.platform.core.servlet.MarmottaResourceFilter;
import org.apache.marmotta.platform.core.test.base.jetty.TestApplication;
import org.apache.marmotta.platform.core.test.base.jetty.TestInjectorFactory;
import org.apache.marmotta.platform.core.util.CDIContext;
import org.apache.marmotta.platform.core.webservices.CoreApplication;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.FilterMapping;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import javax.servlet.DispatcherType;

/**
 * An extended version of the EmbeddedMarmotta which also starts a jetty servlet 
 * container. The context name is passed in the constructor; port could be passed,
 * a random available port will be use otherwise. The JettyMarmotta can optionally 
 * take a set of web service classes as argument; if this argument is present, only 
 * the given web services will be instantiated; otherwise, all configured web services 
 * will be instantiated (as in a normal webapp installation).
 * 
 * @author Sebastian Schaffert
 * @author Sergio Fernández
 */
public class JettyMarmotta extends AbstractMarmotta {

    private Server jetty;
    
    private int port;

	private String context;
	
    public JettyMarmotta(String context) {
        this(context, getRandomPort());
    }

    public JettyMarmotta(String context, int port) {
        this(context, port, (Set<Class<?>>) null);
    }
    
    public JettyMarmotta(String context, Class<?> webservice) {
        this(context, getRandomPort(), webservice);
    }

    public JettyMarmotta(String context, int port, Class<?> webservice) {
        this(context,port, Collections.<Class<?>>singleton(webservice));
    }
    
    public JettyMarmotta(String context, Class<?>... webservices) {
        this(context, getRandomPort(), webservices);
    }

    public JettyMarmotta(String context, int port, Class<?>... webservices) {
        this(context,port, new HashSet<Class<?>>(Arrays.asList(webservices)));
    }
    
    public JettyMarmotta(String context, Set<Class<?>> webservices) {
    	this(context, getRandomPort(), webservices);
    }

    public JettyMarmotta(String context, int port, Set<Class<?>> webservices) {
        super();

        this.port = port;
        this.context = (context != null ? context : "/");
        
        // create a new jetty & run it on port 8080
        jetty = new Server(this.port);


        TestInjectorFactory.setManager(container.getBeanManager());

        ServletContextHandler ctx = new ServletContextHandler(jetty, this.context);

        // now we have a context, start up the first phase of the LMF initialisation
        startupService.startupConfiguration(home.getAbsolutePath(), override, ctx.getServletContext());

        // register the RestEasy CDI injector factory
        ctx.setAttribute("resteasy.injector.factory", TestInjectorFactory.class.getCanonicalName());

        // register filters
        FilterHolder resourceFilter = new FilterHolder(CDIContext.getInstance(MarmottaResourceFilter.class));
        resourceFilter.setInitParameter("kiwi.resourceCaching", "true");
        ctx.addFilter(resourceFilter, "/*", EnumSet.of(DispatcherType.REQUEST));

        // register RestEasy so we can run web services

        // if a single web service is given, only register that webservice, otherwise startup the default configuration
        //FilterHolder restEasyFilter = new FilterHolder(org.jboss.resteasy.plugins.server.servlet.FilterDispatcher.class);
        ServletHolder restEasyFilter  = new ServletHolder(org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher.class);
        restEasyFilter.setInitParameter("resteasy.injector.factory", TestInjectorFactory.class.getCanonicalName());

        if(webservices != null) {
            TestApplication.setTestedWebServices(webservices);
            //restEasyFilter.setInitParameter("resteasy.resources", webservice.getName());
            restEasyFilter.setInitParameter("javax.ws.rs.Application", TestApplication.class.getCanonicalName());
        } else {
            restEasyFilter.setInitParameter("javax.ws.rs.Application", CoreApplication.class.getCanonicalName());
        }

        //ctx.addFilter(restEasyFilter,"/*", Handler.ALL);
        ctx.addServlet(restEasyFilter, "/*");

        try {
            jetty.start(); 
            String url = "http://localhost:" + this.port + this.context + "/";
            startupService.startupHost(url, url);
        } catch (Exception e) {
            log.error("could not start up embedded jetty server", e);
        }
    }

    @Override
    public void shutdown() {
        try {
            jetty.stop();
        } catch (Exception e) {
            log.error("could not shutdown embedded jetty server" ,e);
        }
        super.shutdown();
    }
    
    public int getPort() {
		return port;
	}

	public String getContext() {
		return context;
	}
	
	public static int getRandomPort() {
		Random ran = new Random();
	    int port = 0;
	    do {
	    	port = ran.nextInt(2000) + 8000;
	    } while (!isPortAvailable(port));

	    return port;
	}
	
	public static boolean isPortAvailable(final int port) {
	    ServerSocket ss = null;
	    try {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        return true;
	    } catch (final IOException e) {
	    	
	    } finally {
	        if (ss != null) {
	            try {
					ss.close();
				} catch (IOException e) {
					
				}
	        }
	    }
	    return false;
	}

}
