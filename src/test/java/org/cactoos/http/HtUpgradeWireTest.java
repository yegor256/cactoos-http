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
import org.cactoos.Input;
import org.cactoos.Scalar;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Ignore;
import org.junit.Test;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.http.FtRemote;
import org.takes.rs.RsWithStatus;
import org.takes.tk.TkText;

/**
 * Test for {@link HtUpgradeWire}. Must test if the {@link Wire} returned
 * after an 101 status code is an {@link HtSecureWire}.
 * @author Paulo Lobo (pauloeduardolobo@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class HtUpgradeWireTest {

    /**
     * Tests if the wire of the response of an 101 code is an
     * {@link HtSecureWire}.
     * @throws Exception If Something goes wrong.
     */
    @Test
    @Ignore("HtUpgradeWire implementation is not ready yet.")
    public void testUpgrade() throws Exception {
        new FtRemote(new TkAlways101Mock(new TkText("Hello, world!"))).exec(
            home -> MatcherAssert.assertThat(
                "Could not upgrade wire",
                new ResponseWrap(
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
    public void testHtUpgrade() throws Exception {
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
     */
    private class ResponseWrap implements Input, Scalar<Wire> {

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
         * @param wire Original wire.
         * @param req Request string.
         */
        ResponseWrap(final Wire wire, final String req) {
            this.htwire = wire;
            this.response = new HtResponse(wire, req);
        }

        @Override
        public InputStream stream() throws IOException {
            return this.response.stream();
        }

        @Override
        public Wire value() {
            return this.htwire;
        }
    }

    /**
     * Mock which always returns response with 101 code.
     */
    private class TkAlways101Mock implements Take {

        /**
         * Origin {@link Take}.
         */
        private final Take origin;

        /**
         * Ctor.
         * @param origin Origin take.
         */
        TkAlways101Mock(final Take origin) {
            this.origin = origin;
        }

        @Override
        public Response act(final Request req) throws IOException {
            return new RsWithStatus(
                this.origin.act(req),
                // @checkstyle MagicNumber (1 line)
                101,
                "Switching Protocols"
            );
        }
    }
}
