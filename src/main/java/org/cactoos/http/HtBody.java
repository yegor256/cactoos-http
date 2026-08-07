/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.io.InputStream;
import org.cactoos.Input;
import org.cactoos.bytes.BytesOf;
import org.cactoos.http.io.SkipInput;

/**
 * Head of HTTP response.
 * @since 0.1
 */
public final class HtBody implements Input {

    /**
     * Response.
     */
    private final Input response;

    /**
     * Ctor.
     * @param rsp Response
     */
    public HtBody(final Input rsp) {
        this.response = rsp;
    }

    @Override
    public InputStream stream() throws Exception {
        // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
        return new SkipInput(this.response, new BytesOf("\r\n\r\n")).stream();
    }
}
