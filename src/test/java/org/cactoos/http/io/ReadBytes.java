/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http.io;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.cactoos.Bytes;
import org.cactoos.Input;
import org.cactoos.Scalar;
import org.cactoos.scalar.Sticky;

/**
 * Reads available data from {@link Input} as {@link Bytes} only once
 * and without closing it.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.1
 */
public final class ReadBytes implements Bytes {

    /**
     * Bytes from the input stream.
     */
    private final Scalar<byte[]> scalar;

    /**
     * Ctor.
     * @param input The input
     */
    public ReadBytes(final InputStream input) {
        this(input, 16 << 10);
    }

    /**
     * Ctor.
     * @param input The input
     * @param max Max length of the buffer for reading
     */
    public ReadBytes(final InputStream input, final int max) {
        this.scalar = new Sticky<>(
            () -> {
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final byte[] buf = new byte[max];
                while (input.read(buf) >= 0) {
                    baos.write(buf);
                }
                return baos.toByteArray();
            }
        );
    }

    @Override
    public byte[] asBytes() throws Exception {
        return this.scalar.value();
    }
}
