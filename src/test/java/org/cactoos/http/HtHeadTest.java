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
package org.cactoos.http;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;
import org.cactoos.io.BytesOf;
import org.cactoos.io.InputOf;
import org.cactoos.matchers.MatcherOf;
import org.cactoos.text.JoinedText;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.llorllale.cactoos.matchers.TextHasString;

/**
 * Test case for {@link HtHead}.
 *
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @checkstyle MagicNumberCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class HtHeadTest {

    @Test
    public void takesHeadOutOfHttpResponse() throws IOException {
        MatcherAssert.assertThat(
            new TextOf(
                new HtHead(
                    new InputOf(
                        new JoinedText(
                            "\r\n",
                            "HTTP/1.1 200 OK",
                            "Content-type: text/plain",
                            "",
                            "Hello, dude!"
                        )
                    )
                )
            ).asString(),
            Matchers.endsWith("text/plain")
        );
    }

    @Test
    public void emptyHeadOfHttpResponse() throws IOException {
        MatcherAssert.assertThat(
            new TextOf(
                new HtHead(
                    new InputOf(
                        new JoinedText(
                            "\r\n",
                            "",
                            "",
                            "Body"
                        )
                    )
                )
            ),
            new TextHasString("")
        );
    }

    @Test
    public void largeText() throws IOException {
        final byte[] bytes = new byte[18000];
        new Random().nextBytes(bytes);
        MatcherAssert.assertThat(
            new TextOf(
                new HtHead(
                    new InputOf(
                        new JoinedText(
                            "\r\n",
                            "HTTP/1.1 200 OK",
                            "Content-type: text/plain",
                            "",
                            new TextOf(new BytesOf(bytes)).asString()
                        )
                    )
                )
            ).asString(),
            Matchers.endsWith("text/plain")
        );
    }

    @Test
    public void attackInTheMiddle() throws IOException {
        final int size = 16384;
        final ByteBuffer buf = ByteBuffer.allocate(size * 2);
        for (int pos = 0; pos < size - 4; ++pos) {
            buf.put((byte) 'a');
        }
        buf.put((byte) 'b');
        buf.put((byte) '\r');
        buf.put((byte) '\n');
        buf.put((byte) '\r');
        buf.put((byte) '\n');
        for (int pos = buf.position(); pos < 2 * size; ++pos) {
            buf.put((byte) 'a');
        }
        MatcherAssert.assertThat(
            "Can't find when separator broken by two buffers",
            new TextOf(
                new HtHead(
                    new InputOf(
                        new TextOf(new BytesOf(buf.array())).asString()
                    )
                )
            ).asString(),
            Matchers.endsWith("b")
        );
    }

    @Test
    public void attackOnBufferStart() throws IOException {
        final int size = 16384;
        final ByteBuffer buf = ByteBuffer.allocate(size * 2);
        for (int pos = 0; pos < size - 1; ++pos) {
            buf.put((byte) 'a');
        }
        buf.put((byte) 'b');
        buf.put((byte) '\r');
        buf.put((byte) '\n');
        buf.put((byte) '\r');
        buf.put((byte) '\n');
        for (int pos = buf.position(); pos < 2 * size; ++pos) {
            buf.put((byte) 'a');
        }
        MatcherAssert.assertThat(
            "Can't find when buffers starts from separator",
            new TextOf(
                new HtHead(
                    new InputOf(
                        new TextOf(new BytesOf(buf.array())).asString()
                    )
                )
            ).asString(),
            Matchers.endsWith("b")
        );
    }

    @Test
    public void fakeSeparator() throws IOException {
        final int size = 16384;
        final ByteBuffer buf = ByteBuffer.allocate(size * 2);
        for (int pos = 0; pos < size - 2; ++pos) {
            buf.put((byte) 'a');
        }
        buf.put((byte) '\r');
        buf.put((byte) '\n');
        for (int pos = size; pos < 2 * size - 5; ++pos) {
            buf.put((byte) 'a');
        }
        buf.put((byte) 'b');
        buf.put((byte) '\r');
        buf.put((byte) '\n');
        buf.put((byte) '\r');
        buf.put((byte) '\n');
        MatcherAssert.assertThat(
            "Can't add previous buffer with part of separator",
            new TextOf(
                new HtHead(
                    new InputOf(
                        new TextOf(new BytesOf(buf.array())).asString()
                    )
                )
            ).asString(),
            new MatcherOf<>(
                str -> str.length() == size * 2 - 4 && str.endsWith("b")
            )
        );
    }

    @Test
    public void rollbackState() throws IOException {
        final int size = 16384;
        final ByteBuffer buf = ByteBuffer.allocate(size * 4);
        for (int pos = 0; pos < 2 * size - 2; ++pos) {
            buf.put((byte) 'a');
        }
        buf.put((byte) '\r');
        buf.put((byte) '\n');
        for (int pos = 2 * size; pos < 4 * size - 5; ++pos) {
            buf.put((byte) 'a');
        }
        buf.put((byte) 'b');
        buf.put((byte) '\r');
        buf.put((byte) '\n');
        buf.put((byte) '\r');
        buf.put((byte) '\n');
        MatcherAssert.assertThat(
            "Can't rollback state to initial state",
            new TextOf(
                new HtHead(
                    new InputOf(
                        new TextOf(new BytesOf(buf.array())).asString()
                    )
                )
            ).asString(),
            new MatcherOf<>(
                str -> str.length() == size * 4 - 4 && str.endsWith("b")
            )
        );
    }

    @Test
    public void checkEmptyInput() throws IOException {
        MatcherAssert.assertThat(
            "Can't process empty input",
            new TextOf(
                new HtHead(
                    new InputOf(
                        ""
                    )
                )
            ).asString(),
            Matchers.isEmptyString()
        );
    }
}
