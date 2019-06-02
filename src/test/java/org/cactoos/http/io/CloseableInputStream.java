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
 * Useful {@link InputStream} implementation for tests.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.1
 */
public final class CloseableInputStream extends InputStream {

    /**
     * The wrapped stream.
     */
    private final InputStream origin;

    /**
     * Closed or not.
     */
    private boolean closed;

    /**
     * Ctor.
     *
     * @param origin The wrapped stream.
     */
    public CloseableInputStream(final InputStream origin) {
        super();
        this.origin = origin;
    }

    /**
     * Check if stream is closed.
     *
     * @return True if the stream was closed.
     */
    public boolean wasClosed() {
        return this.closed;
    }

    @Override
    public int read() throws IOException {
        return this.origin.read();
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        this.origin.close();
    }
}
