/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.io.InputStream;
import org.cactoos.Input;
import org.cactoos.Scalar;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.http.FtRemote;
import org.takes.rs.RsWithStatus;
import org.takes.tk.TkText;

/**
 * Test for {@link HtUpgradeWire}. Must test if the {@link Wire} returned
 * after an 101 status code is an {@link HtSecureWire}.
 * @since 0.1
 */
final class HtUpgradeWireTest {

    /**
     * Tests if the wire of the response of an 101 code is an
     * {@link HtSecureWire}.
     * @throws Exception If Something goes wrong.
     */
    @Test
    @Disabled("HtUpgradeWire implementation is not ready yet.")
    void upgradesWireOnSwitchingProtocolsCode() throws Exception {
        new FtRemote(
            new HtUpgradeWireTest.TkAlways101Mock(new TkText("Hello, world!"))
        ).exec(
            home -> MatcherAssert.assertThat(
                "Could not upgrade wire",
                new HtUpgradeWireTest.ResponseWrap(
                    new HtUpgradeWire(
                        new HtWire(
                            home.getHost(), home.getPort()
                        )
                    ),
                    home.getHost()
                ).value(),
                new IsInstanceOf(HtSecureWire.class)
            )
        );
    }

    /**
     * Test of {@link HtUpgradeWire} just to suit coverage standards.
     * @throws Exception If something goes wrong.
     */
    @Test
    void upgradesWireSuccessfully() throws Exception {
        new FtRemote(new TkText("Upgraded wire")).exec(
            home -> MatcherAssert.assertThat(
                "Upgrade wire not found",
                new TextOf(
                    new HtResponse(
                        new HtUpgradeWire(
                            new HtWire(
                                home.getHost(),
                                home.getPort()
                            )
                        ),
                        home.getHost()
                    )
                ).asString(),
                Matchers.containsString("HTTP/1.1 200")
            )
        );
    }

    /**
     * Wrap for response which allows access to its wire.
     * @since 0.1
     */
    private final class ResponseWrap implements Input, Scalar<Wire> {

        /**
         * Origin response.
         */
        private final HtResponse response;

        /**
         * Original wire.
         */
        private final Wire htwire;

        /**
         * Ctor.
         * @param wire Original wire
         * @param req Request string
         */
        ResponseWrap(final Wire wire, final String req) {
            this.htwire = wire;
            this.response = new HtResponse(wire, req);
        }

        @Override
        public InputStream stream() throws Exception {
            return this.response.stream();
        }

        @Override
        public Wire value() {
            return this.htwire;
        }
    }

    /**
     * Mock which always returns response with 101 code.
     * @since 0.1
     */
    private final class TkAlways101Mock implements Take {

        /**
         * Origin {@link Take}.
         */
        private final Take origin;

        /**
         * Ctor.
         * @param origin Origin take
         */
        TkAlways101Mock(final Take origin) {
            this.origin = origin;
        }

        @Override
        public Response act(final Request req) throws Exception {
            return new RsWithStatus(
                this.origin.act(req),
                101,
                "Switching Protocols"
            );
        }
    }
}
