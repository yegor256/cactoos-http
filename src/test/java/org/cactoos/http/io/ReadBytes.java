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
     *
     * @param input The input
     */
    public ReadBytes(final InputStream input) {
        // @checkstyle MagicNumber (1 line)
        this(input, 16 << 10);
    }

    /**
     * Ctor.
     *
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
