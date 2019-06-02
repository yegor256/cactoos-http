/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Yegor Bugayenko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.cactoos.http.io;

import org.cactoos.Text;
import org.cactoos.io.DeadInputStream;
import org.cactoos.io.InputOf;
import org.cactoos.text.TextOf;
import org.hamcrest.core.IsNot;
import org.junit.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.InputHasContent;
import org.llorllale.cactoos.matchers.IsTrue;

/**
 * Test case for {@link CloseableInputStreamTest}.
 *
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle JavadocTypeCheck (500 lines)
 * @checkstyle JavadocVariableCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class CloseableInputStreamTest {
    @Test
    public void doesNotCloseTheStream() throws Exception {
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
    public void wrapsAndInputStream() throws Exception {
        final Text text = new TextOf("test");
        final CloseableInputStream closeable = new CloseableInputStream(
            new InputOf(text).stream()
        );
        new Assertion<>(
            "must allow to read the stream",
            new InputOf(closeable),
            new InputHasContent(text)
        ).affirm();
    }
}
