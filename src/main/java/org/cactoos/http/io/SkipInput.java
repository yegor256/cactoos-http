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

package org.cactoos.http.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import org.cactoos.Bytes;
import org.cactoos.Input;

/**
 * {@link Input} that skips until it find some defined bytes.
 *
 * @author Victor Noel (victor.noel@crazydwarves.org)
 * @version $Id$
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
     *
     * @param origin The input.
     * @param delimiter The bytes delimiter to skip until.
     */
    public SkipInput(final Input origin, final Bytes delimiter) {
        this.origin = origin;
        this.delimiter = delimiter;
    }

    @Override
    public InputStream stream() throws IOException {
        final byte[] bytes = this.delimiter.asBytes();
        final BoundedByteBuffer buffer = new BoundedByteBuffer(
            bytes.length
        );
        final InputStream stream = this.origin.stream();
        boolean eof = false;
        while (!eof && !buffer.isEqualsTo(bytes)) {
            final int read = stream.read();
            if (read < 0) {
                eof = true;
            } else {
                buffer.offer((byte) read);
            }
        }
        return stream;
    }

    /**
     * A very simple circular buffer of bytes.
     *
     * @author Victor Noel (victor.noel@crazydwarves.org)
     * @version $Id$
     * @since 0.1
     * @todo #3:30min this internal class should be transformed to a full
     *  fledged public class with a clear interface, either in the cactoos or
     *  the cactoos-http project and the implementation is not efficient in
     *  memory usage because of the boxing of bytes into an object. This should
     *  be replaced by a real circular byte buffer implementation with
     *  primitive bytes.
     */
    static final class BoundedByteBuffer {

        /**
         * The buffer.
         */
        private final Deque<Byte> internal;

        /**
         * The size limit.
         */
        private final int limit;

        /**
         * Ctor.
         *
         * @param limit The size limit.
         */
        BoundedByteBuffer(final int limit) {
            this.limit = limit;
            this.internal = new ArrayDeque<>(limit);
        }

        /**
         * Add a byte to the buffer, potentially by removing the oldest one to
         * satisfy the size limit.
         *
         * @param add The byte to add
         */
        public void offer(final byte add) {
            if (this.internal.size() == this.limit) {
                this.internal.removeFirst();
            }
            this.internal.addLast(add);
        }

        /**
         * Test if the buffer contains exactly the {@code bytes}.
         *
         * @param bytes The bytes to compare to.
         * @return The value {@code true} if the buffer contains exactly
         *  the {@code bytes}.
         */
        public boolean isEqualsTo(final byte[] bytes) {
            boolean equal = bytes.length == this.internal.size();
            final Iterator<Byte> iter = this.internal.iterator();
            int idx = 0;
            while (iter.hasNext() && equal) {
                equal = iter.next() == bytes[idx];
                idx = idx + 1;
            }
            return equal;
        }
    }
}
