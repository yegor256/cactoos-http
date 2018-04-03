/**
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

import java.io.IOException;
import java.io.InputStream;
import org.cactoos.Input;
import org.cactoos.io.InputOf;

/**
 * Response.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class HtResponse implements Input {

    /**
     * The wire.
     */
    private final HtWire wire;

    /**
     * HTTP request.
     */
    private final Input request;

    /**
     * Ctor.
     * @param wre The wire
     * @param req The request
     */
    public HtResponse(final HtWire wre, final String req) {
        this(wre, new InputOf(req));
    }

    /**
     * Ctor.
     * @param wre The wire
     * @param req The request
     */
    public HtResponse(final HtWire wre, final Input req) {
        this.wire = wre;
        this.request = req;
    }

    @Override
    public InputStream stream() throws IOException {
        return this.wire.send(this.request).stream();
    }
}
