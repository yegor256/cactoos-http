/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.http.FtRemote;
import org.takes.tk.TkText;

/**
 * Test case for {@link HtResponse}.
 * @since 0.1
 */
final class HtResponseTest {

    @Test
    void worksFine() throws Exception {
        new FtRemote(new TkText("Hello, world!")).exec(
            home -> MatcherAssert.assertThat(
                new TextOf(
                    new HtResponse(
                        new HtWire(home),
                        new Get(home)
                    )
                ),
                new HasString("HTTP/1.1 200 OK")
            )
        );
    }

    @Test
    void worksFineByUri() throws Exception {
        new FtRemote(new TkText("Hello, dude!")).exec(
            home -> MatcherAssert.assertThat(
                new TextOf(
                    new HtResponse(home)
                ).asString(),
                Matchers.containsString("HTTP/1.1 200 OK")
            )
        );
    }
}
