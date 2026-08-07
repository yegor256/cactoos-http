/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.util.concurrent.atomic.AtomicInteger;
import org.cactoos.Text;
import org.cactoos.io.InputOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.HasContent;
import org.llorllale.cactoos.matchers.IsApplicable;

/**
 * Test case for {@link HtRetryWire}.
 * @since 0.1
 */
final class HtRetryWireTest {

    @Test
    void retriesMultipleTimesButNotMaxAttempts() {
        final int times = 3;
        final int max = times + 2;
        MatcherAssert.assertThat(
            t -> {
                final AtomicInteger tries = new AtomicInteger(0);
                new HtRetryWire(
                    input -> {
                        if (tries.incrementAndGet() < t) {
                            throw new IllegalArgumentException("retry");
                        }
                        return new InputOf("ignored");
                    },
                    max
                ).send(new InputOf("ignored"));
                return tries.get();
            },
            new IsApplicable<>(times, times)
        );
    }

    @Test
    void eventuallySucceeds() throws Exception {
        final Text txt = new TextOf("out");
        final int max = 3;
        final AtomicInteger tries = new AtomicInteger(0);
        MatcherAssert.assertThat(
            new HtRetryWire(
                input -> {
                    if (tries.incrementAndGet() < max) {
                        throw new IllegalArgumentException("retry");
                    }
                    return new InputOf(txt);
                },
                max
            ).send(new InputOf("ignored")),
            new HasContent(txt.asString())
        );
    }

    @Test
    void failsAfterMaxRetries() {
        final String msg = "retry";
        final int max = 3;
        final AtomicInteger tries = new AtomicInteger(0);
        MatcherAssert.assertThat(
            new ListOf<>(
                Assertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> new HtRetryWire(
                        input -> {
                            if (tries.incrementAndGet() <= max) {
                                throw new IllegalArgumentException(msg);
                            }
                            return new InputOf("ignored");
                        },
                        max
                    ).send(new InputOf("ignored"))
                ).getMessage(),
                String.valueOf(tries.get())
            ),
            new IsEqual<>(new ListOf<>(msg, String.valueOf(max)))
        );
    }
}
