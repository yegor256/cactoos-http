/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.http.FtRemote;
import org.takes.tk.TkText;

/**
 * Test case for {@link HtKeepAliveResponse}.
 * @since 0.1
 */
final class HtKeepAliveResponseTest {

    @Test
    void worksFineByStringReq() throws Exception {
        new FtRemote(new TkText("Hello, dude!")).exec(
            home -> new Assertion<>(
                "The HTTP response contains 200 status code",
                new TextOf(
                    new HtKeepAliveResponse(new HtWire(home), 5000, 5, "req1")
                ),
                new HasString("HTTP/1.1 200 OK")
            ).affirm()
        );
    }

    @Test
    void worksFineByInputReq() throws Exception {
        new FtRemote(new TkText("Hello, world!")).exec(
            home -> new Assertion<>(
                "The HTTP response contains 200 status code",
                new TextOf(
                    new HtKeepAliveResponse(
                        new HtWire(home),
                        5000,
                        5,
                        new Get(home)
                    )
                ),
                new HasString("HTTP/1.1 200 OK")
            ).affirm()
        );
    }
}
