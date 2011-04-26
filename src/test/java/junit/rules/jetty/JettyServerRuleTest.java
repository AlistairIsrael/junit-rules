/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Aug 28, 2009
 */
package junit.rules.jetty;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Rule;
import org.junit.Test;
import org.mortbay.jetty.handler.AbstractHandler;

/**
 * JUnit test case for {@link JettyServerRule}
 *
 * @author Alistair A. Israel
 */
public final class JettyServerRuleTest {

    // CHECKSTYLE:OFF
    @Rule
    public final JettyServerRule jettyServer = new JettyServerRule();
    // CHECKSTYLE:ON

    /**
     * @throws Exception
     *         on exception
     */
    @Test
    public void testHttpServerInterceptor() throws Exception {
        jettyServer.setHandler(new AbstractHandler() {

            @Override
            public void handle(final String target, final HttpServletRequest request,
                    final HttpServletResponse response, final int dispatch) throws IOException,
                    ServletException {
                final PrintWriter out = response.getWriter();
                out.println("<?xml version=\"1.0\"?>");
                out.println("<resource id=\"1234\" name=\"test\" />");
                response.setStatus(HttpServletResponse.SC_OK);
                response.flushBuffer();
            }
        });

        final HttpURLConnection connection = jettyServer.get("/1234.xml");
        final BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        assertEquals("<?xml version=\"1.0\"?>", in.readLine());
        assertEquals("<resource id=\"1234\" name=\"test\" />", in.readLine());
        assertEquals(HTTP_OK, connection.getResponseCode());
    }
    
    
    @Test
    public void testHttpServerInterceptorPostMethod() throws Exception {
        jettyServer.setHandler(new AbstractHandler() {

            @Override
            public void handle(final String target, final HttpServletRequest request,
                    final HttpServletResponse response, final int dispatch) throws IOException,
                    ServletException {
            	BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
            	final PrintWriter out = response.getWriter();
                out.println(reader.readLine());
                response.setStatus(HttpServletResponse.SC_OK);
                response.flushBuffer();
            }
        });
        final HttpURLConnection connection = jettyServer.post("/");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        out.write("Hello World");
        out.flush();
        final BufferedReader in =
                new BufferedReader(new InputStreamReader(connection.getInputStream()));
        assertEquals("Hello World", in.readLine());
        assertEquals(HTTP_OK, connection.getResponseCode());
    }
    
    @Test
    public void testHttpServerInterceptorPutMethod() throws Exception {
        jettyServer.setHandler(new AbstractHandler() {
        	
            @Override
            public void handle(final String target, final HttpServletRequest request,
                    final HttpServletResponse response, final int dispatch) throws IOException,
                    ServletException {
            	BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
                final PrintWriter out = response.getWriter();
                out.println(reader.readLine());
                response.setStatus(HttpServletResponse.SC_OK);
                response.flushBuffer();
            }
        });
        final HttpURLConnection connection = jettyServer.put("/");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        out.write("Hello Again");
        out.flush();
        final BufferedReader in =
                new BufferedReader(new InputStreamReader(connection.getInputStream()));
        assertEquals("Hello Again", in.readLine());
        assertEquals(HTTP_OK, connection.getResponseCode());
    }
    
    @Test
    public void testHttpServerInterceptorDeleteMethod() throws Exception {
        final boolean[] deleteIssued = new boolean[]{false};
        jettyServer.setHandler(new AbstractHandler() {
        
        	@Override
        	public void handle(final String target, final HttpServletRequest request,
                    final HttpServletResponse response, final int dispatch) throws IOException,
                    ServletException {
            	deleteIssued[0] = true;
                response.setStatus(HttpServletResponse.SC_OK);
                response.flushBuffer();
            }
        });
        final HttpURLConnection connection = jettyServer.delete("/");
        assertEquals(HTTP_OK, connection.getResponseCode());
        assertTrue(deleteIssued[0]);
    }
}
