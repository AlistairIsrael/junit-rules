/**
 * junit-rules: JUnit Rules Library
 *
 * Copyright (c) 2009-2011 by Alistair A. Israel.
 * This software is made available under the terms of the MIT License.
 *
 * Created Sep 1, 2009
 */
package junit.rules.httpserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URI;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * <p>
 * Simplifies the effort to write an {@link HttpHandler}.
 * <p>
 * <p>
 * With contributions by <a href="https://github.com/beobal">Sam Tunnicliffe</a>.
 * </p>
 *
 * @author Alistair A. Israel
 */
public class SimpleHttpHandler implements HttpHandler {

    /**
     * {@value #GET}
     */
    private static final String GET = "GET";

    /**
     * {@value #POST}
     */
    private static final String POST = "POST";

    /**
     * {@value #PUT}
     */
    private static final String PUT = "PUT";

    /**
     * {@value #DELETE}
     */
    private static final String DELETE = "DELETE";

    /**
     * HTTP OK ({@value #HTTP_OK})
     *
     * @see HttpURLConnection#HTTP_OK
     */
    public static final int HTTP_OK = HttpURLConnection.HTTP_OK;

    private HttpExchange httpExchange;

    private ByteArrayOutputStream out;

    private PrintWriter pw;

    private int responseCodeSent;

    /**
     * {@inheritDoc}
     *
     * @see com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange)
     */
    @Override
    public final void handle(final HttpExchange exchange) throws IOException {
        this.httpExchange = exchange;
        this.out = new ByteArrayOutputStream();
        this.pw = new PrintWriter(out);
        responseCodeSent = -1;
        final String requestMethod = exchange.getRequestMethod();
        if (GET.equalsIgnoreCase(requestMethod)) {
            onGet();
        } else if (POST.equalsIgnoreCase(requestMethod)) {
            onPost();
        } else if (PUT.equalsIgnoreCase(requestMethod)) {
            onPut();
        } else if (DELETE.equalsIgnoreCase(requestMethod)) {
            onDelete();
        }
        if (responseCodeSent == -1) {
            sendResponse(HTTP_OK);
        }
    }

    /**
     * @return the httpExchange
     */
    public final HttpExchange getHttpExchange() {
        return httpExchange;
    }

    /**
     * Get the request URI
     *
     * @return the request URI
     * @see com.sun.net.httpserver.HttpExchange#getRequestURI()
     */
    protected final URI getRequestURI() {
        return httpExchange.getRequestURI();
    }

    /**
     * Returns a stream from which the request body can be read. Multiple calls to this method will return the same
     * stream. It is recommended that applications should consume (read) all of the data from this stream before closing
     * it. If a stream is closed before all data has been read, then the close() call will read and discard remaining
     * data (up to an implementation specific number of bytes).
     *
     * @return the stream from which the request body can be read.
     * @see com.sun.net.httpserver.HttpExchange#getRequestBody()
     */
    protected final InputStream getRequestBody() {
        return httpExchange.getRequestBody();
    }

    /**
     * Returns an immutable Map containing the HTTP headers that were included with this request. The keys in this Map
     * will be the header names, while the values will be a List of Strings containing each value that was included
     * (either for a header that was listed several times, or one that accepts a comma-delimited list of values on a
     * single line). In either of these cases, the values for the header name will be presented in the order that they
     * were included in the request.
     *
     * @return a read-only Map which can be used to access request headers
     * @see com.sun.net.httpserver.HttpExchange#getRequestHeaders()
     */
    protected final Headers getRequestHeaders() {
        return httpExchange.getRequestHeaders();
    }

    /**
     * Get the request method
     *
     * @return the request method
     * @see com.sun.net.httpserver.HttpExchange#getRequestMethod()
     */
    protected final String getRequestMethod() {
        return httpExchange.getRequestMethod();
    }

    /**
     * Returns a {@link PrintWriter} to the response buffer used to calculate the byte length of the actual HTTP
     * response to be sent later.
     *
     * @return a {@link PrintWriter}
     */
    protected final PrintWriter getResponseWriter() {
        return this.pw;
    }

    /**
     * Returns a mutable Map into which the HTTP response headers can be stored and which will be transmitted as part of
     * this response. The keys in the Map will be the header names, while the values must be a List of Strings
     * containing each value that should be included multiple times (in the order that they should be included).
     *
     * @return a writable Map which can be used to set response headers.
     * @see com.sun.net.httpserver.HttpExchange#getResponseHeaders()
     */
    protected final Headers getResponseHeaders() {
        return httpExchange.getResponseHeaders();
    }

    /**
     * @throws IOException
     *         on exception
     */
    protected void onGet() throws IOException {
    }

    /**
     * @throws IOException
     *         on exception
     */
    protected void onPost() throws IOException {
    }

    /**
     * @throws IOException
     *         on exception
     */
    protected void onPut() throws IOException {
    }

    /**
     * @throws IOException
     *         on exception
     */
    protected void onDelete() throws IOException {
    }

    /**
     * @param responseCode
     *        the HTTP response code to send
     * @throws IOException
     *         on exception
     */
    protected final void sendResponse(final int responseCode) throws IOException {
        pw.flush();
        httpExchange.sendResponseHeaders(responseCode, out.size());
        final OutputStream responseBody = httpExchange.getResponseBody();
        out.writeTo(responseBody);
        responseBody.flush();
        httpExchange.close();
        responseCodeSent = responseCode;
    }
}
