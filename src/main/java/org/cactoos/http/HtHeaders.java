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
import java.util.Locale;
import java.util.Map;
import org.cactoos.Input;
import org.cactoos.map.MapEnvelope;
import org.cactoos.text.TextOf;

/**
 * Headers of HTTP response.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class HtHeaders extends MapEnvelope<String, String> {

    /**
     * Ctor.
     * @param head Response head part
     */
    public HtHeaders(final Input head) {
        super(() -> {
            final String[] lines = new TextOf(head).asString().split("\n\r");
            final Map<String, String> map = new HashMap<>(lines.length - 1);
            for (int idx = 1; idx < lines.length; ++idx) {
                final String[] parts = lines[idx].split(":", 2);
                map.put(
                    parts[0].trim().toLowerCase(Locale.ENGLISH),
                    parts[1].trim()
                );
            }
            return map;
        });
    }

}
