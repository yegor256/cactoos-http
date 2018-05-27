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

/**
 * Simple immutable finite-state machine for finding a specific pattern
 * in a sequence of buffers.
 *
 * <p>There is thread-safety guarantee.
 *
 * @author Alexander Menshikov (sharplermc@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class StateMachine {

    /**
     * The pattern for lookup.
     */
    private final byte[] pattern;

    /**
     * Current state.
     */
    private final int state;

    /**
     * Position in the current or previous buffer where the pattern is
     * starting.
     */
    private final int index;

    /**
     * Ctor.
     *
     * @param pattern Pattern for lookup.
     */
    public StateMachine(final byte[] pattern) {
        this(pattern.clone(), 0, 0);
    }

    /**
     * Ctor.
     *
     * @param pattern Pattern for lookup.
     * @param state Next state.
     * @param index Next index.
     */
    @SuppressWarnings("PMD.ArrayIsStoredDirectly")
    private StateMachine(final byte[] pattern, final int state,
        final int index) {
        this.pattern = pattern;
        this.state = state;
        this.index = index;
    }

    /**
     * Processing new buffer.
     *
     * @param buf New buffer.
     * @param len Length.
     * @return Machine with next state.
     * @checkstyle NestedIfDepthCheck (500 lines)
     * @checkstyle HiddenFieldCheck (500 lines)
     */
    public StateMachine process(final byte[] buf, final int len) {
        int state = this.state;
        int index = this.index;
        if (state < this.pattern.length) {
            for (int pos = 0; pos < buf.length && pos < len; ++pos) {
                if (this.pattern[state] == buf[pos]) {
                    ++state;
                    if (state == this.pattern.length) {
                        index = pos + 1 - this.pattern.length;
                        break;
                    }
                } else {
                    state = 0;
                }
            }
        }
        final StateMachine ans;
        if (this.state == state && this.index == index) {
            ans = this;
        } else {
            ans = new StateMachine(this.pattern, state, index);
        }
        return ans;
    }

    /**
     * Check to at least part of the pattern was found.
     *
     * @return Return {@code True} if at least part of the pattern was found.
     */
    public boolean finding() {
        return this.state > 0;
    }

    /**
     * Check to the pattern was found.
     *
     * @return Return {@code true} if the pattern was found.
     */
    public boolean found() {
        return this.state == this.pattern.length;
    }

    /**
     * Return index in the current or previous buffer where the pattern is
     * starting.
     *
     * @return If positive then return position in the current buffer where the
     *  pattern is starting. If negative then return position from the end of
     *  the previous buffer.
     */
    public int getIndex() {
        if (!this.found()) {
            throw new IllegalStateException("Should be in terminal state.");
        }
        return this.index;
    }
}
