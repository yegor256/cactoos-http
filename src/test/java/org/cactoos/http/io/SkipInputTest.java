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

package org.cactoos.http.io;

import org.cactoos.io.BytesOf;
import org.cactoos.io.InputOf;
import org.cactoos.matchers.TextHasString;
import org.cactoos.text.JoinedText;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

/**
 * Test case for {@link SkipInput}.
 *
 * @author Victor Noel (victor.noel@crazydwarves.org)
 * @version $Id$
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class SkipInputTest {

    @Test
    public void skipsSomeBytes() throws Exception {
        final String prefix = "Hello dude!";
        final String suffix = "How are you?";
        final String delimiter = "\r";
        MatcherAssert.assertThat(
            new TextOf(
                new SkipInput(
                    new InputOf(
                        new JoinedText(
                            delimiter,
                            prefix,
                            suffix
                        )
                    ),
                    new BytesOf(delimiter)
                )
            ),
            new TextHasString(suffix)
        );
    }

    @Test
    public void skipsEverythingWhenNoDelimiter() throws Exception {
        MatcherAssert.assertThat(
            new TextOf(
                new SkipInput(
                    new InputOf("Hello, dude! How are you?"),
                    new BytesOf("\n")
                )
            ),
            new TextHasString("")
        );
    }

    @Test
    public void skipsEverythingWhenEndingWithDelimiter() throws Exception {
        final String delimiter = "\r\n";
        MatcherAssert.assertThat(
            new TextOf(
                new SkipInput(
                    new InputOf(
                        new JoinedText(
                            "",
                            "Hello dude! How are you?",
                            delimiter
                        )
                    ),
                    new BytesOf(delimiter)
                )
            ),
            new TextHasString("")
        );
    }
}
