/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import org.cactoos.Input;

/**
 * A physical connection between two HTTP endpoints.
 * @since 0.1
 */
@FunctionalInterface
public interface Wire {

    /**
     * Send an input and return the response.
     * @param input The data to send
     * @return The remote service's response
     * @throws Exception If an I/O error occurs
     */
    Input send(Input input) throws Exception;
}
