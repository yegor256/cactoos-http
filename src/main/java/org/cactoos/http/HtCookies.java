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

import java.util.HashMap;
import java.util.Map;
import org.cactoos.Input;
import org.cactoos.map.MapEnvelope;

/**
 * Cookies.
 *
 * @author Vseslav Sekorin (vssekorin@gmail.com)
 * @version $Id$
 * @since 0.1
 * @todo #1:30min The implementation of method stream() can handle only
 * one Set-Cookie in a response. Fix HtHeaders so that a single key
 * may be mapped to one or more values (it is legal to receive more than one
 * Set-Cookie in a response).
 * @todo #2:30min The implementation of method stream() will break on
 * "flag-type" directives (`Secure`, `HttpOnly`). Fix HtHeaders so that
 * these directives are handled correctly.
 */
public final class HtCookies extends MapEnvelope<String, String> {

    /**
     * Ctor.
     * @param rsp Response
     */
    public HtCookies(final Input rsp) {
        super(() -> {
            final Map<String, String> result = new HashMap<>();
            final String cookie = new HtHeaders(rsp).get("set-cookie");
            for (final String item : cookie.split(";\\s+")) {
                final String[] entry = item.split("=", 2);
                if (entry.length == 2) {
                    result.put(entry[0], entry[1]);
                } else {
                    throw new IllegalArgumentException(
                        "Incorrect HTTP Response cookie"
                    );
                }
            }
            return result;
        });
    }
}
