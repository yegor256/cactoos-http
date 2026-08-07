/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import org.cactoos.io.InputOf;
import org.cactoos.text.Joined;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link HtStatus}.
 * @since 0.1
 */
final class HtStatusTest {

    @Test
    void takesStatusOutOfHttpResponse() throws IOException {
        MatcherAssert.assertThat(
            new HtStatus(
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
            ).intValue(),
            Matchers.equalTo(HttpURLConnection.HTTP_OK)
        );
    }
}
