/**
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
import java.util.Random;
import org.cactoos.io.BytesOf;
import org.cactoos.io.InputOf;
import org.cactoos.text.JoinedText;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link HtHead}.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 * @todo #1:30min This test does not cover all possible cases.
 *  https://codecov.io/gh/yegor256/cactoos-http/pull/24/diff?src=pr&el=tree
 *  #diff-c3JjL21haW4vamF2YS9vcmcvY2FjdG9vcy9odHRwL0h0U3RhdHVzLmphdmE=
 *  Finish covering all branches missed for this implementation.
 *  A refactor may be due as well.
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
            ).asString(),
            Matchers.equalTo("")
        );
    }

    @Test
    public void largeText() throws IOException {
        //@checkstyle MagicNumberCheck (1 lines)
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

}
