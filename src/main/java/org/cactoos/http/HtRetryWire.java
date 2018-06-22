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

import java.io.IOException;
import org.cactoos.Func;
import org.cactoos.Input;
import org.cactoos.func.IoCheckedFunc;
import org.cactoos.func.RetryFunc;

/**
 * {@link Wire} that will try a few times before throwing an exception.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.1
 */
public final class HtRetryWire implements Wire {

    /**
     * Original wire.
     */
    private final Wire origin;

    /**
     * Exit condition.
     */
    private final Func<Integer, Boolean> func;

    /**
     * Ctor.
     * @param wire Original wire
     * @param attempts Maximum number of attempts
     */
    public HtRetryWire(final Wire wire, final int attempts) {
        this(wire, attempt -> attempt >= attempts);
    }

    /**
     * Ctor.
     * @param wire Original wire
     * @param exit Exit condition, returns TRUE if there is no reason to try
     */
    public HtRetryWire(final Wire wire, final Func<Integer, Boolean> exit) {
        this.origin = wire;
        this.func = exit;
    }

    @Override
    public Input send(final Input input) throws IOException {
        return new IoCheckedFunc<>(
            new RetryFunc<>(
                this.origin::send,
                this.func
            )
         ).apply(input);
    }
}
