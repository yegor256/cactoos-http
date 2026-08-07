/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.net.URI;
import java.net.URISyntaxException;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasString;

/**
 * Test case for {@link Get}.
 * @since 0.1
 */
final class GetTest {

    @Test
    void buildsRequestCorrectly() throws URISyntaxException {
        new Assertion<>(
            "Must build GET request correctly from URI",
            new TextOf(
                new Get(
                    new URI(
                        "http://host/path/to/resource"
                    )
                )
            ),
            // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
            new HasString("GET /path/to/resource HTTP/1.1\r\nHost: host")
        ).affirm();
    }
}
