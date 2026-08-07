/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http.io;

import java.io.InputStream;
import org.cactoos.Bytes;
import org.cactoos.Input;

/**
 * {@link Input} that skips until it find some defined bytes.
 * @since 0.1
 */
public final class SkipInput implements Input {

    /**
     * The input.
     */
    private final Input origin;

    /**
     * The delimiter.
     */
    private final Bytes delimiter;

    /**
     * Ctor.
     * @param origin The input
     * @param delimiter The bytes delimiter to skip until
     */
    public SkipInput(final Input origin, final Bytes delimiter) {
        this.origin = origin;
        this.delimiter = delimiter;
    }

    @Override
    public InputStream stream() throws Exception {
        final byte[] bytes = this.delimiter.asBytes();
        final BoundedByteBuffer buffer = new BoundedByteBuffer(
            bytes.length
        );
        final InputStream stream = this.origin.stream();
        boolean eof = false;
        while (!eof && !buffer.equalTo(bytes)) {
            final int read = stream.read();
            if (read < 0) {
                eof = true;
            } else {
                buffer.offer((byte) read);
            }
        }
        return stream;
    }
}
