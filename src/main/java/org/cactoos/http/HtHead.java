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

package org.cactoos.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Arrays;
import org.cactoos.Input;
import org.cactoos.http.io.StateMachine;
import org.cactoos.io.DeadInputStream;
import org.cactoos.io.InputStreamOf;

/**
 * Head of HTTP response.
 *
 * @since 0.1
 * @todo #42:30min The implementation of method stream() too complex.
 *  Looks like state machine is too low-level abstraction for such task,
 *  and one more level of abstraction should be arrive.
 *  We should remove all checkstyle and PMD exceptions here.
 *  Be careful with previous buffer.
 */
public final class HtHead implements Input {

    /**
     * Buffer length.
     */
    private static final int LENGTH = 16384;

    /**
     * Response.
     */
    private final Input response;

    /**
     * Ctor.
     *
     * @param rsp Response
     */
    public HtHead(final Input rsp) {
        this.response = rsp;
    }

    // @checkstyle NestedIfDepthCheck (500 lines)
    // @checkstyle ExecutableStatementCountCheck (500 lines)
    @Override
    @SuppressWarnings(
        {"PMD.AvoidInstantiatingObjectsInLoops", "PMD.NullAssignment"}
    )
    public InputStream stream() throws IOException {
        final byte[] pattern = {'\r', '\n', '\r', '\n'};
        final InputStream stream = this.response.stream();
        final byte[] buf = new byte[HtHead.LENGTH];
        InputStream head = new DeadInputStream();
        StateMachine machine = new StateMachine(pattern);
        byte[] old = null;
        while (true) {
            final int len = stream.read(buf);
            if (len <= 0) {
                break;
            }
            machine = machine.process(buf, len);
            if (machine.finding()) {
                if (machine.found()) {
                    final int index = machine.getIndex();
                    if (index >= 0) {
                        if (old != null) {
                            head = new SequenceInputStream(
                                head,
                                new InputStreamOf(old)
                            );
                        }
                        if (index != 0) {
                            head = new SequenceInputStream(
                                head,
                                new InputStreamOf(Arrays.copyOf(buf, index))
                            );
                        }
                    } else {
                        head = new SequenceInputStream(
                            head,
                            new InputStreamOf(
                                Arrays.copyOf(old, old.length + index)
                            )
                        );
                    }
                    break;
                } else {
                    old = Arrays.copyOf(buf, len);
                }
            } else {
                if (old != null) {
                    head = new SequenceInputStream(
                        head,
                        new InputStreamOf(old)
                    );
                    old = null;
                }
                head = new SequenceInputStream(
                    head,
                    new InputStreamOf(Arrays.copyOf(buf, len))
                );
            }
        }
        return head;
    }
}
