/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.util.Iterator;
import java.util.List;
import org.cactoos.Input;
import org.cactoos.Text;
import org.cactoos.iterable.Filtered;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.map.Grouped;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapEnvelope;
import org.cactoos.scalar.LengthOf;
import org.cactoos.text.Split;

/**
 * Cookies.
 * @since 0.1
 */
public final class HtCookies extends MapEnvelope<String, List<String>> {

    /**
     * Ctor.
     * @param rsp Response
     */
    public HtCookies(final Input rsp) {
        super(new Grouped<>(
            new Mapped<>(
                (Text entry) -> {
                    final Iterator<Text> iter = new Split(entry, "=").iterator();
                    return new MapEntry<>(
                        iter.next().asString(),
                        iter.next().asString()
                    );
                },
                new Filtered<>(
                    entry -> new LengthOf(new Split(entry, "=")).value().intValue() == 2,
                    new Joined<>(
                        new Mapped<>(
                            e -> new Split(e, ";\\s+"),
                            new HtHeaders(rsp).get("set-cookie")
                        )
                    )
                )
            ),
            MapEntry::getKey,
            MapEntry::getValue
        ));
    }
}
