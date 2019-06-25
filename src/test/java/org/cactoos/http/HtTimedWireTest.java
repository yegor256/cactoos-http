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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.cactoos.io.InputOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.llorllale.cactoos.matchers.MatcherOf;
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

    private static final String UNUSED = "UNUSED";
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
                        new Get(home)
                    )
                ),
                new TextHasString("HTTP/1.1 200 ")
            )
        );
    }

    // @todo #87:30m For now I can't find proper OOP alternative for waiting.
    // Needs to create a new one. I think this can looks like this:
    // new Wait().seconds(long seconds). Needs to do this in the test
    // org.cactoos.http.HtTimedWireTest.failsAfterTimeoutCheckIfWait too
    // @checkstyle MagicNumberCheck (1 line)
    @Test(expected = TimeoutException.class, timeout = 1000)
    public void failsAfterTimeout() throws Exception {
        // @checkstyle MagicNumberCheck (1 line)
        final long timeout = 100;
        new HtTimedWire(
            input -> {
                TimeUnit.SECONDS.sleep(timeout + 1);
                return input;
            },
            timeout
        ).send(new InputOf(HtTimedWireTest.UNUSED));
    }

    // @checkstyle MagicNumberCheck (1 line)
    @Test(timeout = 1000)
    public void failsAfterTimeoutCheckIfWait() throws Exception {
        final long timeout = 100;
        final long current = System.currentTimeMillis();
        final long sleep = 10 * timeout;
        try {
            new HtTimedWire(
                input -> {
                    TimeUnit.SECONDS.sleep(sleep);
                    return input;
                },
                timeout
            ).send(
                new InputOf(HtTimedWireTest.UNUSED)
            );
            Assert.fail("Not failed on Timeout");
        } catch (final TimeoutException exception) {
            final long failed = System.currentTimeMillis();
            MatcherAssert.assertThat(
                failed,
                new MatcherOf<>(
                    input -> input - current < sleep
                )
            );
        }
    }
}
