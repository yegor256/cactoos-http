/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.cactoos.Input;
import org.cactoos.io.Sticky;

/**
 * Automatically redirects request if response status code is 30x.
 * @since 0.1
 */
public final class HtAutoRedirect implements Input {

    /**
     * Response.
     */
    private final Input response;

    /**
     * Ctor.
     * @param rsp Response
     */
    public HtAutoRedirect(final Input rsp) {
        this.response = new Sticky(rsp);
    }

    @Override
    public InputStream stream() throws Exception {
        InputStream stream = this.response.stream();
        final String header = "location";
        final int status = new HtStatus(this.response).intValue();
        if (status >= 300 && status <= 308) {
            final Map<String, List<String>> headers = new HtHeaders(
                new HtHead(this.response)
            );
            if (headers.containsKey(header)) {
                stream.close();
                stream = new HtResponse(
                    URI.create(headers.get(header).get(0))
                ).stream();
            }
        }
        return stream;
    }
}
