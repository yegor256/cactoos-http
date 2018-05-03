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

import org.cactoos.io.InputOf;
import org.cactoos.text.FormattedText;
import org.cactoos.text.JoinedText;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.takes.http.FtRemote;
import org.takes.tk.TkText;

/**
 * Test case for {@link HtAutoRedirect}.
 *
 * @author Vedran Vatavuk (123vgv@gmail.com)
 * @version $Id$
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 */
public final class HtAutoRedirectTest {

    @Test
    public void redirectsRequestAutomatically() throws Exception {
        new FtRemote(new TkText("redirected ok")).exec(
            home -> MatcherAssert.assertThat(
                "Does not redirects automatically",
                new TextOf(
                    new HtAutoRedirect(
                        new InputOf(
                            new JoinedText(
                                "\r\n",
                                "HTTP/1.1 301",
                                new FormattedText(
                                    "Location: %s", home
                                ).asString()
                            )
                        )
                    )
                ).asString(),
                Matchers.containsString("HTTP/1.1 200 ")
            )
        );
    }

    @Test
    public void noRedirectionOnStatusOk() throws Exception {
        final String response = "HTTP/1.1 200 OK";
        MatcherAssert.assertThat(
            "Doesn't return status code OK",
            new TextOf(
                new HtAutoRedirect(
                    new InputOf(
                        new TextOf(response)
                    )
                )
            ).asString(),
            Matchers.containsString(response)
        );
    }

    @Test
    public void returnsRedirectResponseForNoLocation() throws Exception {
        final String response = "HTTP/1.1 300";
        MatcherAssert.assertThat(
            "Doesn't return redirection response code",
            new TextOf(
                new HtAutoRedirect(
                    new InputOf(
                        new TextOf(response)
                    )
                )
            ).asString(),
            Matchers.containsString(response)
        );
    }
}
