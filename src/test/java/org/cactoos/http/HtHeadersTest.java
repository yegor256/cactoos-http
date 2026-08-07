/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.io.IOException;
import org.cactoos.io.InputOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsMapContaining;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link HtHeaders}.
 * @since 0.1
 */
final class HtHeadersTest {

    @Test
    void takesHeadersOutOfHttpResponse() throws IOException {
        MatcherAssert.assertThat(
            new HtHeaders(
                new HtHead(
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
            new IsMapContaining<>(
                new IsEqual<>("content-type"),
                new IsEqual<>(new ListOf<>("text/plain"))
            )
        );
    }
}
