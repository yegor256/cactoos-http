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

import org.cactoos.Input;
import org.cactoos.scalar.NumberEnvelope;
import org.cactoos.text.TextOf;

/**
 * Status of HTTP response.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 * @todo #1:30min The implementation here is not effective. It converts
 *  the entire head part of the request to a string and then only
 *  takes the first line out of it. We should deal with a stream
 *  instead and just read its first line.
 */
public final class HtStatus extends NumberEnvelope {

    /**
     * Serialization marker.
     */
    private static final long serialVersionUID = -5892731788828504127L;

    /**
     * Ctor.
     * @param head Response head part
     */
    public HtStatus(final Input head) {
        super(() -> Double.parseDouble(
            // @checkstyle MagicNumber (1 line)
            new TextOf(head).asString().split("\n\r")[0].split(" ", 3)[1]
        ));
    }

}
