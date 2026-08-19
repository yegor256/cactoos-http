/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;
import org.cactoos.BiFunc;
import org.cactoos.http.io.ReadBytes;
import org.cactoos.io.DeadInput;
import org.cactoos.io.DeadInputStream;
import org.cactoos.text.FormattedText;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsNot;
import org.junit.jupiter.api.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.HasString;
import org.llorllale.cactoos.matchers.IsTrue;
import org.mockito.Mockito;
import org.takes.http.FtRemote;
import org.takes.tk.TkText;

/**
 * Test case for {@link HtWire}.
 * @since 0.1
 */
final class HtWireTest {

    /**
     * Default port for HTTP.
     */
    private static final int HTTP_PORT = 80;

    /**
     * Default port for HTTPS.
     */
    private static final int HTTPS_PORT = 443;

    @Test
    void guessesCorrectPortForHttp() throws Exception {
        this.checkPorts("http://localhost", HtWireTest.HTTP_PORT);
    }

    @Test
    void guessesCorrectPortForHttps() throws Exception {
        this.checkPorts("https://localhost", HtWireTest.HTTPS_PORT);
    }

    @Test
    void guessesCorrectPortForExplicit() throws Exception {
        final int port = 1234;
        this.checkPorts(
            new FormattedText("https://localhost:%d", port).asString(),
            port
        );
    }

    @Test
    void worksWithProvidedHostNameAndPort() throws Exception {
        new FtRemote(new TkText("Hello")).exec(
            home -> MatcherAssert.assertThat(
                new TextOf(
                    new HtResponse(
                        new HtWire(home.getHost(), home.getPort()),
                        new Get(home)
                    )
                ),
                new HasString("HTTP/1.1 200")
            )
        );
    }

    @Test
    void closesSocketOnlyAfterResponseIsClosed() throws Exception {
        new FtRemote(new TkText("Hey")).exec(
            home -> {
                final Socket socket = new Socket(home.getHost(), home.getPort());
                try (
                    InputStream ins = new HtWire(() -> socket).send(new Get(home)).stream()
                ) {
                    new Assertion<>(
                        "must have a response",
                        new TextOf(new ReadBytes(ins)), new HasString("HTTP/1.1 200 OK")
                    ).affirm();
                    new Assertion<>(
                        "must keep the socket open until response is closed",
                        socket.isClosed(), new IsNot<>(new IsTrue())
                    ).affirm();
                }
                new Assertion<>(
                    "must close the socket once input response is closed",
                    socket.isClosed(), new IsTrue()
                ).affirm();
            }
        );
    }

    /**
     * Verify correct port is returned for given URL.
     * @param url URL to check
     * @param port Port number
     * @throws Exception In case of error
     */
    @SuppressWarnings("unchecked")
    private void checkPorts(final String url, final int port) throws Exception {
        final BiFunc<String, Integer, Socket> function =
            Mockito.mock(BiFunc.class);
        try (Socket socket = this.socket()) {
            Mockito.when(function.apply(Mockito.any(), Mockito.any()))
                .thenReturn(socket);
            new HtWire(URI.create(url), function)
                .send(new DeadInput());
            Mockito.verify(function).apply(Mockito.any(), Mockito.eq(port));
        }
    }

    /**
     * Create a mock socket.
     * @return A mock socket
     * @throws IOException In case of error
     */
    private Socket socket() throws IOException {
        final Socket socket = Mockito.mock(Socket.class);
        Mockito.when(socket.getInputStream()).thenReturn(new DeadInputStream());
        return socket;
    }
}
