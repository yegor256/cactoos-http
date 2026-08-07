/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Scanner;
import org.cactoos.Input;
import org.cactoos.io.InputStreamOf;

/**
 * Head of HTTP response.
 * @since 0.1
 */
public final class HtHead implements Input {

    /**
     * Header separator.
     */
    // @checkstyle ProhibitLineSeparatorInStringsCheck (1 line)
    private static final String DELIMITER = "\r\n\r\n";

    /**
     * Charset that is used to read headers.
     */
    private static final Charset CHARSET = Charset.defaultCharset();

    /**
     * Response.
     */
    private final Input response;

    /**
     * Ctor.
     * @param rsp Response
     */
    public HtHead(final Input rsp) {
        this.response = rsp;
    }

    @Override
    public InputStream stream() throws Exception {
        try (
            Scanner scanner = new Scanner(
                this.response.stream(),
                HtHead.CHARSET
            )
        ) {
            scanner.useDelimiter(HtHead.DELIMITER);
            return new InputStreamOf(scanner.next());
        }
    }
}
