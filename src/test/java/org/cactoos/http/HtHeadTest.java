/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.util.Random;
import org.cactoos.Text;
import org.cactoos.bytes.BytesOf;
import org.cactoos.io.InputOf;
import org.cactoos.text.Joined;
import org.cactoos.text.Repeated;
import org.cactoos.text.Replaced;
import org.cactoos.text.TextOf;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.EndsWith;
import org.llorllale.cactoos.matchers.HasString;
import org.llorllale.cactoos.matchers.StartsWith;

/**
 * Test case for {@link HtHead}.
 * @since 0.1
 */
final class HtHeadTest {

    @Test
    void takesHeadOutOfHttpResponse() {
        new Assertion<>(
            "Header does not have 'text/plain'",
            new TextOf(
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
            ),
            new EndsWith("text/plain")
        ).affirm();
    }

    @Test
    void emptyHeadOfHttpResponse() {
        new Assertion<>(
            "Text does not have an empty string",
            new TextOf(
                new HtHead(
                    new InputOf(
                        new Joined(
                            // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
                            "\r\n",
                            "",
                            "",
                            "Body"
                        )
                    )
                )
            ),
            new HasString("")
        ).affirm();
    }

    @Test
    void largeText() throws Exception {
        final byte[] bytes = new byte[18_000];
        new Random().nextBytes(bytes);
        new Assertion<>(
            "Header does not have text/plain header",
            new TextOf(
                new HtHead(
                    new InputOf(
                        new Joined(
                            // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
                            "\r\n",
                            "HTTP/1.1 200 OK",
                            "Content-type: text/plain",
                            "",
                            new TextOf(new BytesOf(bytes)).asString()
                        )
                    )
                )
            ),
            new EndsWith("text/plain")
        ).affirm();
    }

    @Test
    void edgeOfTheBlockTearing() throws Exception {
        final int size = 16_384;
        final Text header = new Joined(
            // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
            "\r\n",
            "HTTP/1.1 200 OK",
            "Referer: http://en.wikipedia.org/wiki/Main_Page#\0",
            "Content-type: text/plain",
            ""
        );
        final Text block = new Replaced(
            header,
            "\0",
            new Repeated(
                "x",
                size - header.asString().length() + 1
            ).asString()
        );
        new Assertion<>(
            "make sure the constructed block is exact size",
            block.asString().length(),
            new IsEqual<>(
                size
            )
        ).affirm();
        new Assertion<>(
            String.format("Edge of the block tearing for size: %s", size),
            new TextOf(
                new HtHead(
                    new InputOf(
                        new Joined(
                            // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
                            "\r\n",
                            block.asString(),
                            "",
                            "body here"
                        )
                    )
                )
            ),
            Matchers.allOf(
                new StartsWith("HTTP"),
                // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
                new HasString("OK\r\nReferer"),
                new EndsWith("text/plain")
            )
        ).affirm();
    }
}
