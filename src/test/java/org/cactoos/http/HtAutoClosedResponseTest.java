/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import org.cactoos.http.io.ReadBytes;
import org.cactoos.text.TextOf;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasString;
import org.llorllale.cactoos.matchers.IsTrue;
import org.llorllale.cactoos.matchers.Throws;
import org.takes.http.FtRemote;
import org.takes.tk.TkText;

/**
 * Test case for {@link HtAutoClosedResponse}.
 * @since 0.1
 */
final class HtAutoClosedResponseTest {

    @Test
    void closesResponseAndThusSocketWhenEofIsReached() throws Exception {
        new FtRemote(new TkText("Hey")).exec(
            home -> {
                final Socket socket = new Socket(home.getHost(), home.getPort());
                final InputStream ins = new HtAutoClosedResponse(
                    new HtResponse(new HtWire(() -> socket), new Get(home))
                ).stream();
                new Assertion<>(
                    "must have a response",
                    new TextOf(new ReadBytes(ins)), new HasString("HTTP/1.1 200 OK")
                ).affirm();
                new Assertion<>(
                    "must close the response, thus the socket, after EOF",
                    socket.isClosed(), new IsTrue()
                ).affirm();
                new Assertion<>(
                    "must behave as closed",
                    ins::available,
                    new Throws<>(IOException.class)
                ).affirm();
            }
        );
    }
}
