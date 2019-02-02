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

import java.util.Iterator;
import java.util.List;
import org.cactoos.Input;
import org.cactoos.Text;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.map.Grouped;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapEnvelope;
import org.cactoos.scalar.LengthOf;
import org.cactoos.text.SplitText;

/**
 * Cookies.
 *
 * @since 0.1
 * @todo #8:30min The implementation of method stream() will break on
 *  "flag-type" directives (`Secure`, `HttpOnly`). Fix HtHeaders so that
 *  these directives are handled correctly.
 */
public final class HtCookies extends MapEnvelope<String, List<String>> {

    /**
     * Ctor.
     * @param rsp Response
     */
    public HtCookies(final Input rsp) {
        super(() -> new Grouped<>(
            new Mapped<>(
                (Text entry) -> {
                    final Iterable<Text> parts = new SplitText(entry, "=");
                    if (new LengthOf(parts).intValue() != 2) {
                        throw new IllegalArgumentException(
                            "Incorrect HTTP Response cookie"
                        );
                    }
                    final Iterator<Text> iter = parts.iterator();
                    return new MapEntry<>(
                        iter.next().asString(),
                        iter.next().asString()
                    );
                },
                new Joined<>(
                    new Mapped<>(
                        e -> new SplitText(e, ";\\s+"),
                        new HtHeaders(rsp).get("set-cookie")
                    )
                )
            ),
            MapEntry::getKey,
            MapEntry::getValue
        ));
    }
}
