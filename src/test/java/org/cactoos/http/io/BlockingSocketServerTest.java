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
package org.cactoos.http.io;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import org.cactoos.text.TextOf;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.MatcherOf;
import org.llorllale.cactoos.matchers.Throws;

/**
 * Test case for {@link BlockingSocketServer}.
 *
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle JavadocVariableCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class BlockingSocketServerTest {

    // @checkstyle MagicNumberCheck (1 line)
    @Test(timeout = 1000)
    public void providesAPort() {
        try (BlockingSocketServer server = new BlockingSocketServer()) {
            new Assertion<>(
                "must have a positive port",
                server.port(),
                new MatcherOf<>(
                    port -> port > 0,
                    new TextOf("0")
                )
            ).affirm();
        }
    }

    // @checkstyle MagicNumberCheck (1 line)
    @Test(timeout = 1000)
    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    public void providesAnAddress() {
        try (BlockingSocketServer server = new BlockingSocketServer()) {
            new Assertion<>(
                "must have an address",
                server.address().getHostAddress(),
                new IsEqual<>("0.0.0.0")
            ).affirm();
        }
    }

    // @checkstyle MagicNumberCheck (1 line)
    @Test(timeout = 1000)
    public void blocksOnNewConnections() {
        try (BlockingSocketServer server = new BlockingSocketServer()) {
            new Assertion<>(
                "must timeout because of blocked connection",
                () -> {
                    try (Socket sck = new Socket()) {
                        sck.connect(
                            new InetSocketAddress(
                                server.address(),
                                server.port()
                            ),
                            // @checkstyle MagicNumberCheck (1 line)
                            10
                        );
                    }
                    return true;
                },
                new Throws<>(
                    "connect timed out",
                    SocketTimeoutException.class
                )
            ).affirm();
        }
    }
}
