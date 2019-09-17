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
public final class HtKeepAliveResponse extends InputEnvelope {

    /**
     * The template of GET request which supports the <em>Keep-Alive</em>
     * header.
     */
    private static final Text TEMPLATE = new JoinedText(
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
     * @checkstyle ParameterNumberCheck (58 lines)
     */
    public HtKeepAliveResponse(
        final Wire wre, final long mtimeout, final int rmax, final String req
    ) {
        super(
            new HtResponse(
                wre,
                new InputOf(
                    new FormattedText(
                        HtKeepAliveResponse.TEMPLATE,
                        req,
                        mtimeout,
                        rmax
                    )
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
     * @checkstyle ParameterNumberCheck (84 lines)
     */
    public HtKeepAliveResponse(
        final Wire wre, final long mtimeout, final int rmax, final Input req
    ) {
        super(() -> wre.send(
            new InputOf(
                new FormattedText(
                    HtKeepAliveResponse.TEMPLATE,
                    req,
                    mtimeout,
                    rmax
                )
            )
        ).stream());
    }
}
