/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.util.List;
import org.cactoos.Input;
import org.cactoos.Scalar;
import org.cactoos.list.ListOf;

/**
 * Content-Type of HTTP response.
 *
 * <p>As per
 * <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec7.html#sec7.2.1">
 * section 7.2.1 of the HTTP/1.1 RFC</a>,
 * a missing <code>content-type</code> header is interpreted as
 * <code>application/octet-stream</code></p>
 *
 * @since 0.1
 */
public final class HtContentType implements Scalar<List<String>> {

    /**
     * Response head part.
     */
    private final Input head;

    /**
     * Ctor.
     * @param head Response head part
     */
    public HtContentType(final Input head) {
        this.head = head;
    }

    @Override
    public List<String> value() {
        return new HtHeaders(this.head).getOrDefault(
            "content-type",
            new ListOf<>("application/octet-stream")
        );
    }
}
