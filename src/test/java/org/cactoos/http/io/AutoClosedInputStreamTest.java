/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http.io;

import org.cactoos.io.DeadInputStream;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsTrue;

/**
 * Test case for {@link AutoClosedInputStream}.
 * @since 0.1
 */
final class AutoClosedInputStreamTest {

    @Test
    void autoClosesTheStream() throws Exception {
        final CloseableInputStream closeable = new CloseableInputStream(
            new DeadInputStream()
        );
        new ReadBytes(closeable).asBytes();
        new Assertion<>(
            "must not close the stream",
            closeable.wasClosed(),
            new IsNot<>(new IsTrue())
        ).affirm();
    }
}
