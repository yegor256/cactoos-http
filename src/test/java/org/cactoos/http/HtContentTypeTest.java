/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import org.cactoos.io.InputOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link HtContentType}.
 * @since 0.1
 */
final class HtContentTypeTest {

    @Test
    void takesContentTypeOutOfHttpResponse() {
        MatcherAssert.assertThat(
            new HtContentType(
                new HtHead(
                    new InputOf(
                        new Joined(
                            // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
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
    void takesDefaultContentType() {
        MatcherAssert.assertThat(
            new HtContentType(
                new HtHead(
                    new InputOf(
                        new Joined(
                            // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
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
