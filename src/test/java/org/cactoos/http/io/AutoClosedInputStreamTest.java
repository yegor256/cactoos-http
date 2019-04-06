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

import java.io.IOException;
import java.io.InputStream;
import org.cactoos.io.DeadInputStream;
import org.junit.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.IsTrue;

/**
 * Test case for {@link AutoClosedInputStream}.
 *
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle JavadocTypeCheck (500 lines)
 * @checkstyle JavadocVariableCheck (500 lines)
 */
public final class AutoClosedInputStreamTest {

    @Test
    public void autoClosesTheStream() {
        new Assertion<Boolean>(
            "must autoclose the stream",
            () -> {
                final CloseableInputStream closeable =
                    new CloseableInputStream(new DeadInputStream());
                try (final InputStream ins = new AutoClosedInputStream(
                    closeable
                )) {
                    int read;
                    do {
                        read = ins.read();
                    } while (read != -1);
                    return closeable.isClosed();
                }
            },
            new IsTrue()
        ).affirm();
    }

    private static final class CloseableInputStream extends InputStream {

        private final InputStream origin;
        private boolean closed;

        CloseableInputStream(final InputStream origin) {
            super();
            this.origin = origin;
        }

        public boolean isClosed() {
            return this.closed;
        }

        @Override
        public int read() throws IOException {
            return this.origin.read();
        }

        @Override
        public void close() throws IOException {
            this.closed = true;
            this.origin.close();
        }
    }
}
