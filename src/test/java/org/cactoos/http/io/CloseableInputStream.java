/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
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
     * @param origin The wrapped stream
     */
    public CloseableInputStream(final InputStream origin) {
        super();
        this.origin = origin;
    }

    /**
     * Check if stream is closed.
     * @return True if the stream was closed
     */
    public boolean wasClosed() {
        return this.closed;
    }

    @Override
    public int read() throws IOException {
        return this.origin.read();
    }

    @Override
    public int read(final byte[] buf, final int off, final int len)
        throws IOException {
        return this.origin.read(buf, off, len);
    }

    @Override
    public void close() throws IOException {
        this.closed = true;
        this.origin.close();
    }
}
