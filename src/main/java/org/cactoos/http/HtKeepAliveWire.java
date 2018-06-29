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
import java.time.Instant;
import org.cactoos.Input;

/**
 * Keep-alive {@link Wire}.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @author Vseslav Sekorin (vssekorin@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class HtKeepAliveWire implements Wire {

    /**
     * Origin wire.
     */
    private final Wire origin;

    /**
     * Timeout.
     */
    private final long timeout;

    /**
     * Max requests number.
     */
    private final int number;

    /**
     * Time of create.
     */
    private final long create;

    /**
     * Number of requests.
     */
    private int count;

    /**
     * Ctor.
     * @param wire Wire
     * @param time Timeout
     * @param nmb Max requests number
     */
    public HtKeepAliveWire(final Wire wire, final long time,
        final int nmb) {
        this.origin = wire;
        this.timeout = time;
        this.number = nmb;
        this.create = Instant.now().toEpochMilli();
        this.count = 0;
    }

    @Override
    public Input send(final Input input) throws IOException {
        ++this.count;
        final Input result;
        if (Instant.now().toEpochMilli() - this.create < this.timeout
            && this.count >= this.number) {
            result = new HtKeepAliveWire(
                this.origin,
                this.timeout,
                this.number
            ).send(input);
        } else {
            result = this.origin.send(input);
        }
        return result;
    }
}
