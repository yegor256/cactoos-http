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

import java.security.SecureRandom;
import org.cactoos.io.InputOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link HtRetryWire}.
 *
 * @author Victor Noel (victor.noel@crazydwarves.org)
 * @version $Id$
 * @since 0.1
 * @todo #11:30min the test is weak and should be completed by a test where an
 *  actual socket fails to connect
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class HtRetryWireTest {

    @Test
    public void runsHtRetryWireMultipleTimes() throws Exception {
        final String txt = "out";
        MatcherAssert.assertThat(
            new TextOf(
                new HtRetryWire(
                    input -> {
                        // @checkstyle MagicNumberCheck (1 line)
                        if (new SecureRandom().nextDouble() > 0.3d) {
                            throw new IllegalArgumentException("May happen");
                        }
                        return new InputOf(txt);
                    },
                    Integer.MAX_VALUE
                ).send(new InputOf("in"))
            ).asString(),
            Matchers.equalTo(txt)
        );
    }
}
