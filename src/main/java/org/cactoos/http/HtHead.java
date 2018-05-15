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

package org.cactoos.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import org.cactoos.Input;
import org.cactoos.io.DeadInputStream;
import org.cactoos.io.InputStreamOf;

/**
 * Head of HTTP response.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
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
     * @param rsp Response
     */
    public HtHead(final Input rsp) {
        this.response = rsp;
    }

    @Override
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public InputStream stream() throws IOException {
        final InputStream stream = this.response.stream();
        final byte[] buf = new byte[HtHead.LENGTH];
        InputStream head = new DeadInputStream();
        while (true) {
            final int len = stream.read(buf);
            if (len < 0) {
                break;
            }
            final int tail = HtHead.findEnd(buf, len);
            final byte[] temp = new byte[tail];
            System.arraycopy(buf, 0, temp, 0, tail);
            head = new SequenceInputStream(head, new InputStreamOf(temp));
            if (tail != len) {
                break;
            }
        }
        return head;
    }

    /**
     * Find header end.
     * @param buf Buffer where to search
     * @param len Size of the buffer
     * @return End of the header
     */
    private static int findEnd(final byte[] buf, final int len) {
        final byte[] end = {'\r', '\n', '\r', '\n'};
        int tail = end.length - 1;
        while (tail < len) {
            boolean found = true;
            for (int num = 0; num < end.length; ++num) {
                if (end[num] != buf[tail - end.length + 1 + num]) {
                    found = false;
                    break;
                }
            }
            if (found) {
                tail = tail - end.length + 1;
                break;
            }
            ++tail;
        }
        return tail;
    }
}
