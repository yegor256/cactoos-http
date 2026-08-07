/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * {@link InputStream} that gets closed on EOF.
 * @since 0.1
 */
public final class AutoClosedInputStream extends InputStream {

    /**
     * The original input.
     */
    private final InputStream origin;

    /**
     * Cotr.
     * @param origin The origin input
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
        return new AutoClosedInputStream.AutoClosed(
            () -> this.origin.read(bytes, off, len)
        ).value();
    }

    @Override
    public int read(final byte[] bytes) throws IOException {
        return new AutoClosedInputStream.AutoClosed(
            () -> this.origin.read(bytes)
        ).value();
    }

    @Override
    public int read() throws IOException {
        return new AutoClosedInputStream.AutoClosed(this.origin::read).value();
    }

    /**
     * Primitive Scalar.
     * @since 0.1
     */
    @FunctionalInterface
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
     * @since 0.1
     */
    private final class AutoClosed implements IntScalar {

        /**
         * The read on the stream.
         */
        private final IntScalar origin;

        /**
         * Ctor.
         * @param origin The read of the stream
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
