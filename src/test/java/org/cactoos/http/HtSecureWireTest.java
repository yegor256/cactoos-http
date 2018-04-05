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

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.cactoos.Scalar;
import org.cactoos.text.JoinedText;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Take;
import org.takes.http.BkBasic;
import org.takes.http.FtRemote;
import org.takes.tk.TkText;

/**
 * Test case for {@link HtSecureWire}.
 *
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class HtSecureWireTest {

    @Test
    public void worksFineWithSsl() throws Exception {
        HtSecureWireTest.secure(new TkText("Hello, world!")).exec(
            home -> MatcherAssert.assertThat(
                new TextOf(
                    new HtResponse(
                        new HtSecureWire(
                            HtSecureWireTest.sslSocket(home.getPort())
                        ),
                        new JoinedText(
                            "\r\n",
                            "GET / HTTP/1.1",
                            String.format("Host:%s", home.getHost())
                        ).asString()
                    )
                ).asString(),
                Matchers.containsString("HTTP/1.1 200 OK")
            )
        );
    }

    /**
     * Creates an instance of secure Front.
     * @param take Take
     * @return FtRemote Front
     * @throws Exception If fails
     */
    private static FtRemote secure(final Take take) throws Exception {
        final SSLServerSocketFactory factory = HtSecureWireTest.context()
            .getServerSocketFactory();
        final SSLServerSocket skt =
            (SSLServerSocket) factory.createServerSocket(0);
        skt.setEnabledCipherSuites(skt.getSupportedCipherSuites());
        return new FtRemote(new BkBasic(take), skt);
    }

    /**
     * Cretes an instance of ssl context.
     * @return SSLContext Context
     * @throws Exception If fails
     */
    private static SSLContext context() throws Exception {
        final SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        ctx.init(null, null, null);
        return ctx;
    }

    /**
     * Creates ssl socket as a scalar.
     * @param port Port
     * @return Scalar Ssl socket
     */
    private static Scalar<SSLSocket> sslSocket(final int port) {
        return () -> {
            final SSLSocketFactory factory = HtSecureWireTest.context()
                .getSocketFactory();
            final SSLSocket skt = (SSLSocket) factory.createSocket(
                "localhost", port
            );
            skt.setEnabledCipherSuites(skt.getSupportedCipherSuites());
            return skt;
        };
    }
}
