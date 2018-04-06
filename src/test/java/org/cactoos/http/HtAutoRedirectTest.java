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
import java.net.ServerSocket;
import java.net.URI;
import org.cactoos.text.JoinedText;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.http.BkBasic;
import org.takes.http.FtRemote;
import org.takes.rs.RsText;
import org.takes.rs.RsWithHeader;
import org.takes.rs.RsWithStatus;
import org.takes.tk.TkText;

/**
 * Test case for {@link HtAutoRedirect}.
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class HtAutoRedirectTest {

    @Test
    public void redirectsRequestAutomatically() throws Exception {
        HtAutoRedirectTest.remote().exec(
            home -> MatcherAssert.assertThat(
                new TextOf(
                    new HtAutoRedirect(
                        new HtResponse(
                            new HtWire(home),
                            HtAutoRedirectTest.requestInput(home)
                        )
                    )
                ).asString(),
                Matchers.containsString("HTTP/1.1 200 ")
            )
        );
    }

    @Test
    public void noRedirectionOnStatusOk() throws Exception {
        new FtRemote(new TkText("Hello, world!")).exec(
            home -> MatcherAssert.assertThat(
                new TextOf(
                    new HtAutoRedirect(
                        new HtResponse(
                            new HtWire(home),
                            HtAutoRedirectTest.requestInput(home)
                        )
                    )
                ).asString(),
                Matchers.containsString("HTTP/1.1 200 OK")
            )
        );
    }

    @Test
    public void returnsErrorResponseForNoLocation() throws Exception {
        new FtRemote(
            new Take() {
                @Override
                public Response act(final Request req) {
                    // @checkstyle MagicNumber (1 line)
                    return new RsWithStatus(new RsText(), 300);
                }
            }).exec(
                home -> MatcherAssert.assertThat(
                    new TextOf(
                        new HtAutoRedirect(
                            new HtResponse(
                                new HtWire(home),
                                HtAutoRedirectTest.requestInput(home)
                            )
                        )
                    ).asString(),
                    Matchers.containsString("HTTP/1.1 300 ")
                )
        );
    }

    /**
     * Request input.
     * @param home Uri
     * @return String Request as string
     * @throws IOException If fails
     */
    private static String requestInput(final URI home) throws IOException {
        return new JoinedText(
            "\r\n",
            "GET / HTTP/1.1",
            String.format("Host:%s", home.getHost())
        ).asString();
    }

    /**
     * Creates front instance that returns redirect response for
     * the first request attempt.
     * @return FtRemote Front
     * @throws IOException If fails
     */
    private static FtRemote remote() throws IOException {
        final ServerSocket skt = HtAutoRedirectTest.randomSocket();
        return new FtRemote(
            new BkBasic(
                new HtAutoRedirectTest.TkRedirect(
                    String.format(
                        "Location: http://localhost:%s/test",
                        skt.getLocalPort()
                    )
                )
            ),
            skt
        );
    }

    /**
     * Generates socket on a random port.
     * @return ServerSocket Socket
     * @throws IOException If fails
     */
    private static ServerSocket randomSocket() throws IOException {
        final ServerSocket skt = new ServerSocket(0);
        skt.setReuseAddress(true);
        return skt;
    }

    /**
     * Take that redirects response on a first request attempt.
     */
    private static final class TkRedirect implements Take {

        /**
         * Location url.
         */
        private final String header;

        /**
         * Number of attempts.
         */
        private Integer attempts;

        /**
         * Ctor.
         * @param destination Destination url
         */
        TkRedirect(final String destination) {
            this.header = destination;
            this.attempts = 0;
        }

        @Override
        public Response act(final Request req) {
            final Response response;
            if (this.attempts == 0) {
                response = new RsWithHeader(
                    // @checkstyle MagicNumber (1 line)
                    new RsWithStatus(new RsText(), 301),
                    this.header
                );
                this.attempts += 1;
            } else {
                response = new RsText("Hello World!");
            }
            return response;
        }
    }
}
