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
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.cactoos.Input;
import org.cactoos.io.StickyInput;
import org.cactoos.scalar.IoCheckedScalar;

/**
 * Automatically redirects request if response status code is 30x.
 *
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class HtAutoRedirect implements Input {

    /**
     * Response.
     */
    private final Input response;

    /**
     * Ctor.
     * @param rsp Response
     */
    public HtAutoRedirect(final Input rsp) {
        this.response = new StickyInput(rsp);
    }

    @Override
    public InputStream stream() throws IOException {
        InputStream stream = this.response.stream();
        final String header = "location";
        final int status = new HtStatus(this.response).intValue();
        // @checkstyle MagicNumber (1 line)
        if (status >= 300 && status <= 308) {
            final Map<String, List<String>> headers = new HtHeaders(
                new HtHead(this.response)
            );
            if (headers.containsKey(header)) {
                final URL url = new URL(headers.get(header).get(0));
                stream = new HtResponse(
                    new IoCheckedScalar<>(url::toURI).value()
                ).stream();
            }
        }
        return stream;
    }
}
