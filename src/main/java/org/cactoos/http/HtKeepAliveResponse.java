/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import org.cactoos.Input;
import org.cactoos.Text;
import org.cactoos.io.InputOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Joined;

/**
 * The response which supports <em>Keep-Alive</em> header.
 * @since 0.1
 */
public final class HtKeepAliveResponse extends InputEnvelope {

    /**
     * The template of GET request which supports the <em>Keep-Alive</em>
     * header.
     */
    private static final Text TEMPLATE = new Joined(
        // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
        "\r\n",
        "%s",
        "Connection: Keep-Alive",
        "Keep-Alive: timeout=%s, max=%s"
    );

    /**
     * Ctor.
     * @param wre The wire
     * @param mtimeout The timeout for the connection usage in milliseconds
     * @param rmax The maximum quantity of the requests within the connection
     *  timeout
     * @param req The request
     * @checkstyle ParameterNumberCheck (2 lines)
     */
    public HtKeepAliveResponse(
        final Wire wre, final long mtimeout, final int rmax, final Input req
    ) {
        this(
            wre,
            new InputOf(
                new FormattedText(
                    HtKeepAliveResponse.TEMPLATE,
                    req,
                    mtimeout,
                    rmax
                )
            )
        );
    }

    /**
     * Ctor.
     * @param wre The wire
     * @param mtimeout The timeout for the connection usage in milliseconds
     * @param rmax The maximum quantity of the requests within the connection
     *  timeout
     * @param req The request
     * @checkstyle ParameterNumberCheck (2 lines)
     */
    public HtKeepAliveResponse(
        final Wire wre, final long mtimeout, final int rmax, final String req
    ) {
        this(
            wre,
            new InputOf(
                new FormattedText(
                    HtKeepAliveResponse.TEMPLATE,
                    req,
                    mtimeout,
                    rmax
                )
            )
        );
    }

    /**
     * Ctor.
     * @param wre The wire
     * @param req The already-formatted request
     */
    private HtKeepAliveResponse(final Wire wre, final Input req) {
        super(() -> wre.send(req).stream());
    }
}
