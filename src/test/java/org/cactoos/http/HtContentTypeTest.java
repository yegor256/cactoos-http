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

import org.cactoos.io.InputOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;

/**
 * Test case for {@link HtContentType}.
 *
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class HtContentTypeTest {

    @Test
    public void takesContentTypeOutOfHttpResponse() {
        MatcherAssert.assertThat(
            new HtContentType(
                new HtHead(
                    new InputOf(
                        new Joined(
                            "\r\n",
                            "HTTP/1.1 200 OK",
                            "Content-type: text/plain",
                            "",
                            "Hello, dude!"
                        )
                    )
                )
            ).value(),
            new IsEqual<>(new ListOf<>("text/plain"))
        );
    }

    @Test
    public void takesDefaultContentType() {
        MatcherAssert.assertThat(
            new HtContentType(
                new HtHead(
                    new InputOf(
                        new Joined(
                            "\r\n",
                            "HTTP/1.1 200 OK",
                            "",
                            "Hello, dude!"
                        )
                    )
                )
            ).value(),
            new IsEqual<>(new ListOf<>("application/octet-stream"))
        );
    }
}
