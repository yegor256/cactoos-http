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
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import org.cactoos.Input;
import org.cactoos.Scalar;
import org.cactoos.io.InputOf;
import org.cactoos.text.JoinedText;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Integration case for {@link HtSecureWire}.
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class HtSecureWireITCase {

    @Test
    public void fetchesPageUsingSsl() throws IOException {
        final String host = new HtSecureWireITCase.Host().toString();
        MatcherAssert.assertThat(
            new TextOf(
                new HtResponse(
                    new HtSecureWire(host),
                    new HtSecureWireITCase.RemoteInput(host)
                )
            ).asString(),
            Matchers.containsString("HTTP/1.1 200 ")
        );
    }

    @Test
    public void fetchesPageByUriUsingSsl() throws Exception {
        final URI uri = new HtSecureWireITCase.Host().value();
        MatcherAssert.assertThat(
            new TextOf(
                new HtResponse(
                    new HtSecureWire(uri),
                    new HtSecureWireITCase.RemoteInput(uri.getHost())
                )
            ).asString(),
            Matchers.containsString("HTTP/1.1 200 OK")
        );
    }

    /**
     * Request input towards remote host.
     */
    private static final class RemoteInput implements Input {

        /**
         * Host domain.
         */
        private final String host;

        /**
         * Ctor.
         * @param domain Host domain
         */
        RemoteInput(final String domain) {
            this.host = domain;
        }

        @Override
        public InputStream stream() throws IOException {
            final String delimiter = "\r\n";
            return new InputOf(
                new JoinedText(
                    delimiter,
                    "GET / HTTP/1.1",
                    String.format("Host: %s", this.host),
                    "Connection: close",
                    delimiter
                )
            ).stream();
        }
    }

    /**
     * Host.
     */
    private static final class Host implements Scalar<URI> {

        /**
         * Host domain.
         */
        private final String domain;

        /**
         * Ctor.
         */
        Host() {
            this("www.yegor256.com");
        }

        /**
         * Ctor.
         * @param host Host
         */
        Host(final String host) {
            this.domain = host;
        }

        @Override
        public URI value() throws Exception {
            return new URL(
                String.format("https://%s:443", this.domain)
            ).toURI();
        }

        @Override
        public String toString() {
            return this.domain;
        }
    }
}
