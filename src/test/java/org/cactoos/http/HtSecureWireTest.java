/*
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
import java.io.InputStream;
import java.net.ServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import org.cactoos.Input;
import org.cactoos.io.InputOf;
import org.cactoos.text.FormattedText;
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
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class HtSecureWireTest {

    @Test
    public void worksFineThroughSsl() throws Exception {
        HtSecureWireTest.secure(new TkText("Hello, world!"), 0).exec(
            home -> MatcherAssert.assertThat(
                "Basic ssl request doesn't work for specified host",
                new TextOf(
                    new HtResponse(
                        new HtSecureWire(
                            home.getHost(), home.getPort()
                        ),
                        new HtSecureWireTest.Request(home.getHost())
                    )
                ).asString(),
                Matchers.containsString("HTTP/1.1 200")
            )
        );
    }

    @Test
    public void worksFineByUriThroughSsl() throws Exception {
        HtSecureWireTest.secure(new TkText(), 0).exec(
            home -> MatcherAssert.assertThat(
                "Doesn't work through ssl for specified uri",
                new TextOf(
                    new HtResponse(
                        new HtSecureWire(
                            home
                        ),
                        new HtSecureWireTest.Request(home.getHost())
                    )
                ).asString(),
                Matchers.containsString("HTTP/1.1 200 OK")
            )
        );
    }

    @Test
    public void createsSecureWireByAddress() throws Exception {
        MatcherAssert.assertThat(
            "Unable to create instance of HtSecureWire",
            new HtSecureWire("localhost"),
            Matchers.isA(Wire.class)
        );
    }

    /**
     * Creates an instance of secure Front.
     * @param take Take
     * @return FtRemote Front
     * @throws Exception If fails
     */
    private static FtRemote secure(final Take take, final int port)
        throws Exception {
        final ServerSocket skt = SSLServerSocketFactory.getDefault()
            .createServerSocket(port);
        return new FtRemote(new BkBasic(take), skt);
    }

    /**
     * Request input.
     */
    private static final class Request implements Input {

        /**
         * Host domain.
         */
        private final String host;

        /**
         * Ctor.
         * @param domain Host domain
         */
        Request(final String domain) {
            this.host = domain;
        }

        @Override
        public InputStream stream() throws IOException {
            final String delimiter = "\r\n";
            return new InputOf(
                new JoinedText(
                    delimiter,
                    "GET / HTTP/1.1",
                    new FormattedText(
                        "Host: %s",
                        this.host
                    ).asString(),
                    "Connection: close",
                    delimiter
                )
            ).stream();
        }
    }

}
