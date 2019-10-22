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
import org.cactoos.text.FormattedText;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.core.IsEqual;
import org.junit.Test;

/**
 * Test case for {@link HtCookies}.
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @since 0.1
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class HtCookiesTest {

    @Test
    public void takesCookiesOfHttpResponse() {
        MatcherAssert.assertThat(
            new HtCookies(
                new HtHead(
                    new InputOf(
                        new Joined(
                            "\r\n",
                            "HTTP/1.1 200 OK",
                            "Content-type: text/plain",
                            "Set-Cookie: path=/; domain=.google.com",
                            "",
                            "Hello, dude!",
                            "How are you?"
                        )
                    )
                )
            ),
            new IsMapContaining<>(
                new IsEqual<>("domain"),
                new IsEqual<>(new ListOf<>(".google.com"))
            )
        );
    }

    @Test
    public void takesMultipleCookies() {
        final String first = "first";
        final String second = "second";
        final HtHead head = new HtHead(
            new InputOf(
                new FormattedText(
                    new Joined(
                        "\r\n",
                        "HTTP/1.1 200 OK",
                        "Set-Cookie: path=/; session1=%s",
                        "Set-Cookie: path=/; session2=%s",
                        "",
                        "Hello!"
                    ), first, second
                )
            )
        );
        MatcherAssert.assertThat(
            new HtCookies(head),
            Matchers.allOf(
                new IsMapContaining<>(
                    new IsEqual<>("session1"),
                    new IsEqual<>(new ListOf<>(first))
                ),
                new IsMapContaining<>(
                    new IsEqual<>("session2"),
                    new IsEqual<>(new ListOf<>(second))
                )
            )
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void incorrectHttpResponseCookie() {
        MatcherAssert.assertThat(
            new HtCookies(
                new HtHead(
                    new InputOf(
                        new Joined(
                            "\r\n",
                            "HTTP/1.1 200 OK",
                            "Content-type: text/plain",
                            "Set-Cookie: path=/; 123; domain=.google.com",
                            "",
                            "Hello, dude!",
                            "How are you?"
                        )
                    )
                )
            ),
            new IsMapContaining<>(
                new IsEqual<>("domain"),
                new IsEqual<>(".google.com")
            )
        );
    }
}
