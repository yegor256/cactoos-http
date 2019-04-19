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
package org.cactoos.http.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link InputStream} that gets closed on EOF.
 *
 * @since 0.1
 */
public final class AutoClosedInputStream extends InputStream {

    /**
     * The original input.
     */
    private final InputStream origin;

    /**
     * Cotr.
     *
     * @param origin The origin input.
     */
    public AutoClosedInputStream(final InputStream origin) {
        super();
        this.origin = origin;
    }

    @Override
    public void close() throws IOException {
        this.origin.close();
    }

    @Override
    public int available() throws IOException {
        return this.origin.available();
    }

    @Override
    public int read(final byte[] bytes, final int off, final int len)
        throws IOException {
        return new AutoClosed(() -> this.origin.read(bytes, off, len)).value();
    }

    @Override
    public int read(final byte[] bytes) throws IOException {
        return new AutoClosed(() -> this.origin.read(bytes)).value();
    }

    @Override
    public int read() throws IOException {
        return new AutoClosed(() -> this.origin.read()).value();
    }

    /**
     * Primitive Scalar.
     */
    private interface IntScalar {
        /**
         * Convert it to the value.
         * @return The value
         * @throws IOException If fails
         */
        int value() throws IOException;
    }

    /**
     * Closes the stream if EOF is reached.
     */
    private final class AutoClosed implements IntScalar {

        /**
         * The read on the stream.
         */
        private final IntScalar origin;

        /**
         * Ctor.
         * @param origin The read of the stream.
         */
        AutoClosed(final IntScalar origin) {
            this.origin = origin;
        }

        @Override
        public int value() throws IOException {
            final int ret;
            ret = this.origin.value();
            if (ret < 0) {
                AutoClosedInputStream.this.close();
            }
            return ret;
        }
    }
}
