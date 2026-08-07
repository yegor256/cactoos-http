/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http.io;

import org.cactoos.bytes.BytesOf;
import org.cactoos.io.InputOf;
import org.cactoos.text.Joined;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;

/**
 * Test case for {@link SkipInput}.
 * @since 0.1
 */
final class SkipInputTest {

    @Test
    void skipsSomeBytes() throws Exception {
        final String suffix = "How are you?";
        // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
        final String delimiter = "\r";
        MatcherAssert.assertThat(
            new TextOf(
                new SkipInput(
                    new InputOf(
                        new Joined(
                            delimiter,
                            "Hello dude!",
                            suffix
                        )
                    ),
                    new BytesOf(delimiter)
                )
            ),
            new HasString(suffix)
        );
    }

    @Test
    void skipsEverythingWhenNoDelimiter() throws Exception {
        MatcherAssert.assertThat(
            new TextOf(
                new SkipInput(
                    new InputOf("Hello, dude! How are you?"),
                    // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
                    new BytesOf("\n")
                )
            ),
            new HasString("")
        );
    }

    @Test
    void skipsEverythingWhenEndingWithDelimiter() throws Exception {
        // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
        final String delimiter = "\r\n";
        MatcherAssert.assertThat(
            new TextOf(
                new SkipInput(
                    new InputOf(
                        new Joined(
                            "",
                            "Hello dude! How are you?",
                            delimiter
                        )
                    ),
                    new BytesOf(delimiter)
                )
            ),
            new HasString("")
        );
    }
}
