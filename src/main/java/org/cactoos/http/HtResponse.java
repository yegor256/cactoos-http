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
import org.cactoos.Input;
import org.cactoos.io.InputOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.UncheckedText;

/**
 * Response.
 *
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
