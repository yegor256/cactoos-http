/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import org.cactoos.Func;
import org.cactoos.Input;
import org.cactoos.func.Retry;

/**
 * {@link Wire} that will try a few times before throwing an exception.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.1
 */
public final class HtRetryWire implements Wire {

    /**
     * Original wire.
     */
    private final Wire origin;

    /**
     * Exit condition.
     */
    private final Func<Integer, Boolean> func;

    /**
     * Ctor.
     * @param wire Original wire
     * @param attempts Maximum number of attempts
     */
    public HtRetryWire(final Wire wire, final int attempts) {
        this(wire, attempt -> attempt >= attempts);
    }

    /**
     * Ctor.
     * @param wire Original wire
     * @param exit Exit condition, returns TRUE if there is no reason to try
     */
    public HtRetryWire(final Wire wire, final Func<Integer, Boolean> exit) {
        this.origin = wire;
        this.func = exit;
    }

    @Override
    public Input send(final Input input) throws Exception {
        return new Retry<>(
            this.origin::send,
            this.func
        ).apply(input);
    }
}
