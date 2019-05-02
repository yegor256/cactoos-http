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

/**
 * A very simple circular buffer of bytes.
 *
 * @since 0.1
 */
public final class BoundedByteBuffer {

    /**
     * The buffer.
     */
    private final byte[] internal;

    /**
     * Beginning of buffer.
     */
    private int start;

    /**
     * End of buffer.
     */
    private int end;

    /**
     * Buffer is full and will rotate.
     */
    private boolean full;

    /**
     * Ctor.
     *
     * @param limit The size limit.
     */
    BoundedByteBuffer(final int limit) {
        this.internal = new byte[limit];
        this.end = 0;
        this.start = 0;
        this.full = false;
    }

    /**
     * Add a byte to the buffer, potentially by removing the oldest one to
     * satisfy the size limit.
     *
     * @param add The byte to add
     */
    public void offer(final byte add) {
        if (this.full) {
            this.start = (this.start + 1) % this.internal.length;
        }
        this.internal[this.end] = add;
        this.end = (this.end + 1) % this.internal.length;
        if (this.start == this.end) {
            this.full = true;
        }
    }

    /**
     * Test if the buffer contains exactly the {@code bytes}.
     *
     * @param bytes The bytes to compare to.
     * @return The value {@code true} if the buffer contains exactly
     *  the {@code bytes}.
     * @checkstyle ReturnCountCheck (14 lines)
     */
    @SuppressWarnings("PMD.OnlyOneReturn")
    public boolean equalTo(final byte[] bytes) {
        if (this.size() != bytes.length) {
            return false;
        }
        int index = this.start;
        for (final byte current : bytes) {
            if ((int) current != (int) this.internal[index]) {
                return false;
            }
            index = (index + 1) % this.internal.length;
        }
        return true;
    }

    /**
     * Size of this buffer.
     * @return This buffer's size.
     */
    private int size() {
        final int size;
        if (this.full) {
            size = this.internal.length;
        } else if (this.empty()) {
            size = 0;
        } else {
            size = this.end;
        }
        return size;
    }

    /**
     * Is this buffer empty?
     * @return True if empty, otherwise false.
     */
    private boolean empty() {
        return this.end == this.start && !this.full;
    }
}
