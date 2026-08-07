/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import org.cactoos.io.InputOf;
import org.cactoos.text.Joined;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;

/**
 * Test case for {@link HtBody}.
 * @since 0.1
 */
final class HtBodyTest {

    @Test
    void takesBodyOutOfHttpResponse() {
        MatcherAssert.assertThat(
            new TextOf(
                new HtBody(
                    new InputOf(
                        new Joined(
                            // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
                            "\r\n",
                            "HTTP/1.1 200 OK",
                            "Content-type: text/plain",
                            "",
                            "Hello, dude!",
                            "How are you?"
                        )
                    )
                )
            ),
            // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
            new HasString("Hello, dude!\r\nHow are you?")
        );
    }
}
