/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.net.URI;
import org.cactoos.Input;
import org.cactoos.io.InputOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.Joined;
import org.cactoos.text.TextOf;

/**
 * An {@link Input} to GET an HTTP URI.
 * @since 0.1
 */
public final class Get extends InputEnvelope {

    /**
     * Ctor.
     * @param url Url to GET
     */
    public Get(final URI url) {
        super(new InputOf(
            new Joined(
                // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
                new TextOf("\r\n"),
                new FormattedText(
                    "GET %s HTTP/1.1",
                    url.getPath()
                ),
                new FormattedText(
                    "Host: %s",
                    url.getHost()
                )
            )
        ));
    }
}
