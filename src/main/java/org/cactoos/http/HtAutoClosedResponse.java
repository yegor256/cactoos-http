/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.io.InputStream;
import org.cactoos.Input;
import org.cactoos.http.io.AutoClosedInputStream;

/**
 * Response that gets closed on EOF.
 * @since 0.1
 */
public final class HtAutoClosedResponse implements Input {

    /**
     * The origin response.
     */
    private final Input origin;

    /**
     * Ctor.
     * @param rsp The origin response
     */
    public HtAutoClosedResponse(final Input rsp) {
        this.origin = rsp;
    }

    @Override
    public InputStream stream() throws Exception {
        return new AutoClosedInputStream(this.origin.stream());
    }
}
