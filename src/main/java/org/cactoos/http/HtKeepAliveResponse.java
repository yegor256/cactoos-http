/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cactoos.http;

import java.net.URI;
import java.util.Optional;
import org.cactoos.Input;
import org.cactoos.Text;
import org.cactoos.io.InputOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.JoinedText;

/**
 * The response which supports <em>Keep-Alive</em> header.
 *
 * @since 0.1
 */
public final class HtKeepAliveResponse extends HtResponseEnvelope {

    /**
     * The template of GET request which supports the <em>Keep-Alive</em>
     * header.
     */
    private static final Text TEMPLATE = new JoinedText(
        "\r\n",
        "GET %s HTTP/1.1",
        "Host:%s",
        "Connection: Keep-Alive",
        "Keep-Alive: timeout=%s, max=%s"
    );

    /**
     * Ctor.
     * @param uri Target URI
     * @param mtimeout The timeout for the connection usage in milliseconds
     * @param rmax The maximum quantity of the requests within the connection
     *  timeout
     * @todo #20:30min Replace the HtWire by HtKeepAliveWire which should
     *  support the <em>Keep-Alive</em> header. It should has at least 1 `ctor`
     *  with 3 parameters:
     *  - `wire` which represents the HTTP connection;
     *  - `timeout` in milliseconds;
     *  - `rmax` the max quantity of within the timeout.
     *  In case the quantity of requests is greater than rmax but takes less
     *  time than `timeout`, the connection should be reset by the server.
     *  Remove the suppresing of PMD errors due to unused private fields.
     */
    public HtKeepAliveResponse(
        final URI uri, final long mtimeout, final int rmax
    ) {
        this(
            new HtWire(uri),
            new FormattedText(
                HtKeepAliveResponse.TEMPLATE,
                Optional.ofNullable(uri.getQuery()).orElse("/"),
                uri.getHost(),
                mtimeout,
                rmax
            ),
            mtimeout,
            rmax
        );
    }

    /**
     * Ctor.
     * @param wire The wire
     * @param req The request
     * @param mtimeout The timeout for the connection usage in milliseconds
     * @param rmax The maximum quantity of the requests within the connection
     *  timeout
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    public HtKeepAliveResponse(
        final Wire wire, final Text req, final long mtimeout, final int rmax
    ) {
        this(wire, new InputOf(req), mtimeout, rmax);
    }

    /**
     * Ctor.
     * @param wire The wire
     * @param req The request
     * @param mtimeout The timeout for the connection usage in milliseconds
     * @param rmax The maximum quantity of the requests within the connection
     *  timeout
     * @checkstyle ParameterNumberCheck (5 lines)
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    public HtKeepAliveResponse(
        final Wire wire, final Input req, final long mtimeout, final int rmax
    ) {
        super(new HtResponse(wire, req));
    }

}
