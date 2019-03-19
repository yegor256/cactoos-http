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

import java.util.Arrays;
import org.cactoos.Bytes;
import org.cactoos.io.BytesOf;
import org.cactoos.scalar.Equality;
import org.cactoos.scalar.UncheckedScalar;

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
     * The size limit.
     */
    private final int limit;

    /**
     * Current buffer index.
     */
    private int idx;

    /**
     * Is the buffer full?
     */
    private boolean full;


    /**
     * Ctor.
     *
     * @param limit The size limit.
     */
    BoundedByteBuffer(final int limit) {
        this.limit = limit;
        this.internal = new byte[this.limit];
        this.idx = 0;
        this.full = false;
    }

    /**
     * Add a byte to the buffer, potentially by removing the oldest one to
     * satisfy the size limit.
     *
     * @param add The byte to add
     */
    public void offer(final byte add) {
        this.internal[this.idx] = add;
        this.idx = (this.idx + 1) % this.limit;
        if (this.idx == 0){
            this.full = true;
        }
    }

    /**
     * Test if the buffer contains exactly the {@code bytes}.
     *
     * @param bytes The bytes to compare to.
     * @return The value {@code true} if the buffer contains exactly
     *  the {@code bytes}.
     */
    public boolean equalTo(final byte[] bytes) {
        boolean result;
        if (this.full){
            result = bytes.length == this.internal.length;
            for (int i=this.idx; i<this.internal.length; i++){
                if (!result)
                    break;
                result = bytes[i-this.idx] == this.internal[i];
            }
            for (int i=0; i<this.idx-1; i++){
                if (!result)
                    break;
                result = bytes[i+this.idx-1] == this.internal[i];
            }
        } else {
            result = bytes.length == this.idx;
            for (int i=0; i<this.idx-1; i++){
                if (!result)
                    break;
                result = bytes[i] == this.internal[i];
            }
        }
        return result;
    }
}
