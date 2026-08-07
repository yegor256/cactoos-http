/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import org.cactoos.Input;
import org.cactoos.func.Timed;

/**
 * {@link Wire} that will terminate the connection if it's taking too long.
 * @since 0.1
 */
public final class HtTimedWire implements Wire {

    /**
     * Original wire.
     */
    private final Wire origin;

    /**
     * Milliseconds.
     */
    private final long milliseconds;

    /**
     * Ctor.
     * @param wire Original wire
     * @param milliseconds Milliseconds until the connection is terminated
     */
    public HtTimedWire(final Wire wire, final long milliseconds) {
        this.origin = wire;
        this.milliseconds = milliseconds;
    }

    @Override
    public Input send(final Input input) throws Exception {
        return new Timed<>(
            this.origin::send,
            this.milliseconds
        ).apply(input);
    }
}
