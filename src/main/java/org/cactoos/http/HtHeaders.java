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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.cactoos.Input;
import org.cactoos.Text;
import org.cactoos.list.Joined;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEnvelope;
import org.cactoos.text.LowerText;
import org.cactoos.text.SplitText;
import org.cactoos.text.TextOf;
import org.cactoos.text.TrimmedText;

/**
 * Headers of HTTP response.
 *
 * @since 0.1
 */
public final class HtHeaders extends MapEnvelope<String, List<String>> {

    /**
     * Ctor.
     * @param head Response head part
     */
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public HtHeaders(final Input head) {
        super(() -> {
            final Map<String, List<String>> map = new HashMap<>();
            final ListOf<Text> texts = new ListOf<>(
                new SplitText(new TextOf(head), "\r\n")
            );
            for (final Text line : texts.subList(1, texts.size())) {
                final String[] parts = line.asString().split(":", 2);
                map.merge(
                    new LowerText(
                        new TrimmedText(new TextOf(parts[0])), Locale.ENGLISH
                    ).asString(),
                    new ListOf<>(
                        new TrimmedText(new TextOf(parts[1])).asString()
                    ),
                    (first, second) -> new Joined<String>(first, second)
                );
            }
            return map;
        });
    }
}
