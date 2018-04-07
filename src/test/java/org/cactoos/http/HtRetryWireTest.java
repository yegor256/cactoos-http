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

import java.util.concurrent.atomic.AtomicInteger;
import org.cactoos.Text;
import org.cactoos.io.InputOf;
import org.cactoos.matchers.FuncApplies;
import org.cactoos.matchers.InputHasContent;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test case for {@link HtRetryWire}.
 *
 * @author Victor Noel (victor.noel@crazydwarves.org)
 * @version $Id$
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class HtRetryWireTest {

    /**
     * A rule for expecting exceptions.
     */
    @Rule
    public final ExpectedException expected = ExpectedException.none();

    @Test
    public void retriesMultipleTimesButNotMaxAttempts() {
        final int times = 3;
        final int max = times + 2;
        MatcherAssert.assertThat(
            t -> {
                final AtomicInteger tries = new AtomicInteger(0);
                new HtRetryWire(
                    input -> {
                        if (tries.incrementAndGet() < t) {
                            throw new IllegalArgumentException("retry");
                        }
                        return new InputOf("ignored");
                    },
                    max
                ).send(new InputOf("ignored"));
                return tries.get();
            },
            new FuncApplies<>(times, times)
        );
    }

    @Test
    public void eventuallySucceeds() throws Exception {
        final Text txt = new TextOf("out");
        final int max = 3;
        final AtomicInteger tries = new AtomicInteger(0);
        MatcherAssert.assertThat(
            new HtRetryWire(
                input -> {
                    if (tries.incrementAndGet() < max) {
                        throw new IllegalArgumentException("retry");
                    }
                    return new InputOf(txt);
                },
                max
            ).send(new InputOf("ignored")),
            new InputHasContent(txt.asString())
        );
    }

    @Test
    public void failsAfterMaxRetries() throws Exception {
        final String msg = "retry";
        final int max = 3;
        final AtomicInteger tries = new AtomicInteger(0);
        try {
            this.expected.expect(IllegalArgumentException.class);
            this.expected.expectMessage(msg);
            new HtRetryWire(
                input -> {
                    if (tries.incrementAndGet() <= max) {
                        throw new IllegalArgumentException(msg);
                    }
                    return new InputOf("ignored");
                },
                max
            ).send(new InputOf("ignored"));
        } finally {
            MatcherAssert.assertThat(tries.get(), new IsEqual<>(max));
        }
    }
}
