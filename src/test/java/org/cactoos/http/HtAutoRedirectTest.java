/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import org.cactoos.io.InputOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Joined;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.http.FtRemote;
import org.takes.tk.TkText;

/**
 * Test case for {@link HtAutoRedirect}.
 * @since 0.1
 */
final class HtAutoRedirectTest {

    @Test
    void redirectsRequestAutomatically() throws Exception {
        new FtRemote(new TkText("redirected ok")).exec(
            home -> MatcherAssert.assertThat(
                "Does not redirects automatically",
                new TextOf(
                    new HtAutoRedirect(
                        new InputOf(
                            new Joined(
                                // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
                                new TextOf("\r\n"),
                                new TextOf("HTTP/1.1 301"),
                                new FormattedText(
                                    "Location: %s", home
                                )
                            )
                        )
                    )
                ),
                new HasString("HTTP/1.1 200 ")
            )
        );
    }

    @Test
    void noRedirectionOnStatusOk() throws Exception {
        final String response = "HTTP/1.1 200 OK";
        MatcherAssert.assertThat(
            "Doesn't return status code OK",
            new TextOf(
                new HtAutoRedirect(
                    new InputOf(
                        new TextOf(response)
                    )
                )
            ),
            new HasString(response)
        );
    }

    @Test
    void returnsRedirectResponseForNoLocation() throws Exception {
        final String response = "HTTP/1.1 300";
        MatcherAssert.assertThat(
            "Doesn't return redirection response code",
            new TextOf(
                new HtAutoRedirect(
                    new InputOf(
                        new TextOf(response)
                    )
                )
            ),
            new HasString(response)
        );
    }
}
