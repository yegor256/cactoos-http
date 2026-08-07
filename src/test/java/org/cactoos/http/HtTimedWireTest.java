/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.time.Duration;
import java.util.concurrent.TimeoutException;
import org.cactoos.io.InputOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasString;
import org.takes.http.FtRemote;
import org.takes.tk.TkText;

/**
 * Test case for {@link HtTimedWire}.
 * @since 0.1
 */
final class HtTimedWireTest {

    @Test
    void worksFine() throws Exception {
        final long timeout = 1000;
        new FtRemote(new TkText("Hello, world!")).exec(
            home -> MatcherAssert.assertThat(
                new TextOf(
                    new HtResponse(
                        new HtTimedWire(new HtWire(home), timeout),
                        new Get(home)
                    )
                ),
                new HasString("HTTP/1.1 200 ")
            )
        );
    }

    /**
     * Non-routable IP address artificially creates a timeout error.
     * See: https://stackoverflow.com/a/904609/3456163
     */
    @Test
    @SuppressWarnings("PMD.AvoidUsingHardCodedIP")
    void failsAfterTimeout() {
        final long timeout = 100;
        Assertions.assertTimeoutPreemptively(
            Duration.ofMillis(1000),
            () -> Assertions.assertThrows(
                TimeoutException.class,
                () -> new HtTimedWire(
                    new HtWire(
                        "10.255.255.1",
                        80
                    ),
                    timeout
                ).send(new InputOf("unused"))
            )
        );
    }
}
