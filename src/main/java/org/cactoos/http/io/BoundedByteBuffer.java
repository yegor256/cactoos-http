/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http.io;

/**
 * A very simple circular buffer of bytes.
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
     * @param limit The size limit
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
     * @param bytes The bytes to compare to
     * @return The value {@code true} if the buffer contains exactly
     *  the {@code bytes}
     * @checkstyle ReturnCountCheck (14 lines)
     */
    @SuppressWarnings("PMD.OnlyOneReturn")
    public boolean equalTo(final byte[] bytes) {
        if (this.size() != bytes.length) {
            return false;
        }
        int index = this.start;
        for (final byte current : bytes) {
            if (current != this.internal[index]) {
                return false;
            }
            index = (index + 1) % this.internal.length;
        }
        return true;
    }

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

    private boolean empty() {
        return this.end == this.start && !this.full;
    }
}
