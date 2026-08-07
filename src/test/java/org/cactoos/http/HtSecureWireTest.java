/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.io.InputStream;
import javax.net.ssl.SSLServerSocketFactory;
import org.cactoos.Input;
import org.cactoos.io.InputOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Joined;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.takes.Take;
import org.takes.http.BkBasic;
import org.takes.http.FtRemote;
import org.takes.tk.TkText;

/**
 * Test case for {@link HtSecureWire}.
 * @since 0.1
 */
final class HtSecureWireTest {

    @Test
    void worksFineThroughSsl() throws Exception {
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
    void worksFineByUriThroughSsl() throws Exception {
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
    void createsSecureWireByAddress() throws Exception {
        MatcherAssert.assertThat(
            "Unable to create instance of HtSecureWire",
            new HtSecureWire("localhost"),
            Matchers.isA(Wire.class)
        );
    }

    /**
     * Creates an instance of secure Front.
     * @param take Take
     * @param port Port to bind to, or zero for a random one
     * @return FtRemote Front
     * @throws Exception If fails
     */
    private static FtRemote secure(final Take take, final int port)
        throws Exception {
        return new FtRemote(
            new BkBasic(take),
            SSLServerSocketFactory.getDefault().createServerSocket(port)
        );
    }

    /**
     * Request input.
     * @since 0.1
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
        public InputStream stream() throws Exception {
            // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
            final String delimiter = "\r\n";
            return new InputOf(
                new Joined(
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
