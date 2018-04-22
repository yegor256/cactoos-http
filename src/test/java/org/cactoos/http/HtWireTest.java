/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cactoos.http;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import org.cactoos.BiFunc;
import org.cactoos.io.DeadInput;
import org.cactoos.io.DeadInputStream;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Test case for {@link HtWire}.
 * @author Krzysztof Krason (Krzysztof.Krason@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class HtWireTest {

    /**
     * Default port for HTTP.
     */
    private static final int HTTP_PORT = 80;

    /**
     * Default port for HTTPS.
     */
    private static final int HTTPS_PORT = 443;

    @Test
    public void guessesCorrectPortForHttp() throws Exception {
        this.checkPorts("http://localhost", HtWireTest.HTTP_PORT);
    }

    @Test
    public void guessesCorrectPortForHttps() throws Exception {
        this.checkPorts("https://localhost", HtWireTest.HTTPS_PORT);
    }

    /**
     * Verify correct port is returned for given URL.
     * @param url URL to check
     * @param port Port number
     * @throws Exception In case of error
     */
    @SuppressWarnings("unchecked")
    private void checkPorts(final String url, final int port)
        throws Exception {
        final BiFunc<String, Integer, Socket> function =
            Mockito.mock(BiFunc.class);
        final Socket socket = this.socket();
        Mockito.when(function.apply(Mockito.any(), Mockito.any()))
            .thenReturn(socket);
        new HtWire(URI.create(url), function)
            .send(new DeadInput());
        Mockito.verify(function).apply(Mockito.any(), Mockito.eq(port));
    }

    /**
     * Create a mock socket.
     * @return A mock socket
     * @throws IOException In case of error
     */
    private Socket socket() throws IOException {
        final Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getInputStream()).thenReturn(new DeadInputStream());
        return socket;
    }
}
