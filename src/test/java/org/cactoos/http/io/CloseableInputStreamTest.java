/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http.io;

import org.cactoos.Text;
import org.cactoos.io.DeadInputStream;
import org.cactoos.io.InputOf;
import org.cactoos.text.TextOf;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasContent;
import org.llorllale.cactoos.matchers.IsTrue;

/**
 * Test case for {@link CloseableInputStreamTest}.
 * @since 0.1
 */
final class CloseableInputStreamTest {

    @Test
    void doesNotCloseTheStream() throws Exception {
        final CloseableInputStream closeable = new CloseableInputStream(
            new DeadInputStream()
        );
        new Assertion<>(
            "must not be marked as closed before close is called",
            closeable.wasClosed(),
            new IsNot<>(new IsTrue())
        ).affirm();
        closeable.close();
        new Assertion<>(
            "must be marked as closed after close is called",
            closeable.wasClosed(),
            new IsTrue()
        ).affirm();
    }

    @Test
    void wrapsAndInputStream() throws Exception {
        final Text text = new TextOf("test");
        new Assertion<>(
            "must allow to read the stream",
            new InputOf(
                new CloseableInputStream(new InputOf(text).stream())
            ),
            new HasContent(text)
        ).affirm();
    }
}
