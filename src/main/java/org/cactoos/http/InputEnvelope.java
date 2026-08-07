/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.io.InputStream;
import org.cactoos.Input;

/**
 * The envelope for {@link Input}.
 * @since 0.1
 */
public abstract class InputEnvelope implements Input {

    /**
     * HTTP request.
     */
    private final Input origin;

    /**
     * Ctor.
     * @param origin The request
     */
    public InputEnvelope(final Input origin) {
        this.origin = origin;
    }

    @Override
    public final InputStream stream() throws Exception {
        return this.origin.stream();
    }
}
