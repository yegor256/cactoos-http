/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link HtCookies}.
 * @since 0.1
 */
final class HtCookiesTest {

    @Test
    void takesCookiesOfHttpResponse() {
        MatcherAssert.assertThat(
            new HtCookies(
                new HtHead(
                    new InputOf(
                        new Joined(
                            // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
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
    void takesMultipleCookies() {
        final String first = "first";
        final String second = "second";
        MatcherAssert.assertThat(
            new HtCookies(
                new HtHead(
                    new InputOf(
                        new FormattedText(
                            new Joined(
                                // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
                                "\r\n",
                                "HTTP/1.1 200 OK",
                                "Set-Cookie: path=/; session1=%s",
                                "Set-Cookie: path=/; session2=%s",
                                "",
                                "Hello!"
                            ), first, second
                        )
                    )
                )
            ),
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

    @Test
    void skipsCookieSegmentWithoutEqualsSign() {
        MatcherAssert.assertThat(
            new HtCookies(
                new HtHead(
                    new InputOf(
                        new Joined(
                            // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
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
                new IsEqual<>(new ListOf<>(".google.com"))
            )
        );
    }

    @Test
    void ignoresFlagTypeDirectives() {
        MatcherAssert.assertThat(
            new HtCookies(
                new HtHead(
                    new InputOf(
                        new Joined(
                            // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
                            "\r\n",
                            "HTTP/1.1 200 OK",
                            "Content-type: text/plain",
                            "Set-Cookie: name=value; Secure; HttpOnly; domain=.google.com",
                            "",
                            "Hello"
                        )
                    )
                )
            ),
            Matchers.allOf(
                new IsMapContaining<>(
                    new IsEqual<>("name"),
                    new IsEqual<>(new ListOf<>("value"))
                ),
                new IsMapContaining<>(
                    new IsEqual<>("domain"),
                    new IsEqual<>(new ListOf<>(".google.com"))
                )
            )
        );
    }
}
