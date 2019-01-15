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

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;
import org.cactoos.Input;
import org.cactoos.io.InputStreamOf;

/**
 * Head of HTTP response.
 *
 * @since 0.1
 */
public final class HtHead implements Input {

    /**
     * Header separator.
     */
    private static final String DELIMITER = "\r\n\r\n";

    /**
     * Charset that is used to read headers.
     */
    private static final Charset CHARSET = Charset.defaultCharset();

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
    public InputStream stream() throws Exception {
        try (final Scanner scanner = new Scanner(
            this.response.stream(),
            HtHead.CHARSET.name()
        )) {
            scanner.useDelimiter(HtHead.DELIMITER);
            return new InputStreamOf(scanner.next());
        }
    }
}

