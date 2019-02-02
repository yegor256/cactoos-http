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

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeoutException;
import org.cactoos.io.InputOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.JoinedText;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.llorllale.cactoos.matchers.TextHasString;
import org.takes.http.FtRemote;
import org.takes.tk.TkText;

/**
 * Test case for {@link HtTimedWire}.
 *
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle JavadocVariableCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class HtTimedWireTest {

    private ServerSocket server;

    @Before
    public void openServerWithOneSlot() throws Exception {
        this.server = new ServerSocket(0, 1);
    }

    @After
    public void closeServer() throws Exception {
        this.server.close();
    }

    @Test
    public void worksFine() throws Exception {
        // @checkstyle MagicNumberCheck (1 line)
        final long timeout = 1000;
        new FtRemote(new TkText("Hello, world!")).exec(
            home -> MatcherAssert.assertThat(
                new TextOf(
                    new HtResponse(
                        new HtTimedWire(new HtWire(home), timeout),
                        new InputOf(
                            new JoinedText(
                                new TextOf("\r\n"),
                                new TextOf("GET / HTTP/1.1"),
                                new FormattedText("Host:%s", home.getHost())
                            )
                        )
                    )
                ),
                new TextHasString("HTTP/1.1 200 ")
            )
        );
    }

    // @checkstyle MagicNumberCheck (1 line)
    @Test(expected = TimeoutException.class, timeout = 1000)
    public void failsAfterTimeout() throws Exception {
        // @checkstyle MagicNumberCheck (1 line)
        final long timeout = 100;
        try (Socket blocker = new Socket()) {
            blocker.connect(this.server.getLocalSocketAddress());
            new HtTimedWire(
                new HtWire(
                    this.server.getInetAddress().getHostAddress(),
                    this.server.getLocalPort()
                ),
                timeout
            ).send(new InputOf("unused"));
        }
    }
}
