/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.cactoos.Input;
import org.cactoos.Text;
import org.cactoos.number.NumberEnvelope;
import org.cactoos.text.UncheckedText;

/**
 * Status of HTTP response.
 * @since 0.1
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
        super(
            Double.parseDouble(
                new UncheckedText(
                    (Text) () -> new BufferedReader(
                        new InputStreamReader(head.stream(), StandardCharsets.UTF_8)
                    ).readLine().split(" ", 3)[1]
                ).asString()
            )
        );
    }
}
