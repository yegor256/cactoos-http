/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.util.List;
import java.util.Locale;
import org.cactoos.Input;
import org.cactoos.Text;
import org.cactoos.iterable.Mapped;
import org.cactoos.iterable.Skipped;
import org.cactoos.map.Grouped;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapEnvelope;
import org.cactoos.text.Lowered;
import org.cactoos.text.Split;
import org.cactoos.text.TextOf;
import org.cactoos.text.Trimmed;

/**
 * Headers of HTTP response.
 * @since 0.1
 */
public final class HtHeaders extends MapEnvelope<String, List<String>> {

    /**
     * Ctor.
     * @param head Response head part
     */
    public HtHeaders(final Input head) {
        super(new Grouped<>(
            new Mapped<>(
                (Text line) -> {
                    final String[] parts = line.asString().split(":", 2);
                    return new MapEntry<>(
                        new Lowered(
                            new Trimmed(new TextOf(parts[0])), Locale.ENGLISH
                        ).asString(),
                        new Trimmed(new TextOf(parts[1])).asString()
                    );
                },
                new Skipped<>(
                    1,
                    // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
                    new Split(new TextOf(head), "\r\n")
                )
            ),
            MapEntry::getKey,
            MapEntry::getValue
        ));
    }
}
