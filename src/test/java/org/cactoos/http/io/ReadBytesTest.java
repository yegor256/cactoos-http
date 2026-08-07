/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http.io;

import org.cactoos.io.DeadInputStream;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsTrue;

/**
 * Test case for {@link ReadBytesTest}.
 * @since 0.1
 */
final class ReadBytesTest {

    @Test
    void doesNotCloseTheStream() throws Exception {
        final CloseableInputStream closeable = new CloseableInputStream(
            new DeadInputStream()
        );
        new ReadBytes(new AutoClosedInputStream(closeable)).asBytes();
        new Assertion<>(
            "must autoclose the stream",
            closeable.wasClosed(),
            new IsTrue()
        ).affirm();
    }
}
