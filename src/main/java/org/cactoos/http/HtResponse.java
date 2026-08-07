/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.net.URI;
import org.cactoos.Input;
import org.cactoos.io.InputOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;

/**
 * Response.
 * @since 0.1
 * @todo #64:30min We need decorators for HtResponse that will automatically
 *  transform the inputstream based on criteria like the Content-Length header,
 *  the Transfer-Encoding header (when service returns the payload in chunks),
 *  etc.
 */
public final class HtResponse extends InputEnvelope {

    /**
     * Ctor.
     * @param uri Target URI
     * @since 0.1
     */
    public HtResponse(final String uri) {
        this(URI.create(uri));
    }

    /**
     * Ctor.
     * @param uri Target URI
     * @since 0.1
     */
    public HtResponse(final URI uri) {
        this(
            new HtWire(uri),
            new UncheckedText(
                new FormattedText(
                    // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
                    "GET %s HTTP/1.1\r\nHost:%s",
                    // @checkstyle AvoidInlineConditionalsCheck (1 line)
                    uri.getQuery() == null ? "/" : uri.getQuery(),
                    uri.getHost()
                )
            ).asString()
        );
    }

    /**
     * Ctor.
     * @param wre The wire
     * @param req The request
     */
    public HtResponse(final Wire wre, final String req) {
        this(wre, new InputOf(req));
    }

    /**
     * Ctor.
     * @param wre The wire
     * @param req The request
     */
    public HtResponse(final Wire wre, final Input req) {
        super(() -> wre.send(req).stream());
    }
}
