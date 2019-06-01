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

import java.util.Random;
import org.cactoos.Text;
import org.cactoos.io.BytesOf;
import org.cactoos.io.InputOf;
import org.cactoos.text.JoinedText;
import org.cactoos.text.RepeatedText;
import org.cactoos.text.ReplacedText;
import org.cactoos.text.TextOf;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.EndsWith;
import org.llorllale.cactoos.matchers.StartsWith;
import org.llorllale.cactoos.matchers.TextHasString;

/**
 * Test case for {@link HtHead}.
 *
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class HtHeadTest {

    @Test
    public void takesHeadOutOfHttpResponse() {
        new Assertion<>(
            "Header does not have 'text/plain'",
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
            ),
            new EndsWith("text/plain")
        ).affirm();
    }

    @Test
    public void emptyHeadOfHttpResponse()  {
        new Assertion<>(
            "Text does not have an empty string",
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
        ).affirm();
    }

    @Test
    public void largeText() throws Exception {
        //@checkstyle MagicNumberCheck (1 lines)
        final byte[] bytes = new byte[18000];
        new Random().nextBytes(bytes);
        new Assertion<>(
            "Header does not have text/plain header",
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
            ),
            new EndsWith("text/plain")
        ).affirm();
    }

    @Test
    public void edgeOfTheBlockTearing() throws Exception {
        final int size = 16384;
        final Text header = new JoinedText(
            "\r\n",
            "HTTP/1.1 200 OK",
            "Referer: http://en.wikipedia.org/wiki/Main_Page#\0",
            "Content-type: text/plain",
            ""
        );
        final Text block = new ReplacedText(
            header,
            "\0",
            new RepeatedText(
                "x",
                size - header.asString().length() + 1
            ).asString()
        );
        new Assertion<>(
            "make sure the constructed block is exact size",
            block.asString().length(),
            new IsEqual<>(
                size
            )
        ).affirm();
        new Assertion<>(
            String.format("Edge of the block tearing for size: %s", size),
            new TextOf(
                new HtHead(
                    new InputOf(
                        new JoinedText(
                            "\r\n",
                            block.asString(),
                            "",
                            "body here"
                        )
                    )
                )
            ),
            Matchers.allOf(
                new StartsWith("HTTP"),
                new TextHasString("OK\r\nReferer"),
                new EndsWith("text/plain")
            )
        ).affirm();
    }
}
