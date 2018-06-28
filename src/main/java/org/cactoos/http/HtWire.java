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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import org.cactoos.BiFunc;
import org.cactoos.Input;
import org.cactoos.Scalar;
import org.cactoos.func.IoCheckedBiFunc;
import org.cactoos.io.BytesOf;
import org.cactoos.io.InputOf;
import org.cactoos.scalar.Constant;
import org.cactoos.scalar.IoCheckedScalar;
import org.cactoos.scalar.Ternary;

/**
 * Wire.
 *
 * @since 0.1
 * @todo #62:30min HtWire should not close the socket opened to the remote
 *  service (this includes not closing associated input and output streams).
 *  It should instead return an `Input` with the socket's inputstream "unread".
 *  Refactor accordingly.
 */
public final class HtWire implements Wire {

    /**
     * Buffer length.
     */
    private static final int LENGTH = 16384;

    /**
     * Address.
     */
    private final String address;

    /**
     * TCP port.
     */
    private final Scalar<Integer> port;

    /**
     * Supplier of sockets.
     */
    private final BiFunc<String, Integer, Socket> supplier;

    /**
     * Ctor.
     * @param uri The address of the server
     */
    public HtWire(final URI uri) {
        this(uri, Socket::new);
    }

    /**
     * Ctor.
     * @param addr The address of the server
     */
    public HtWire(final String addr) {
        // @checkstyle MagicNumber (1 line)
        this(addr, new Constant<>(80), Socket::new);
    }

    /**
     * Ctor.
     * @param addr The address of the server
     * @param tcp The TCP port
     */
    public HtWire(final String addr, final int tcp) {
        this(addr, new Constant<>(tcp), Socket::new);
    }

    /**
     * Ctor.
     * @param uri The address of the server
     * @param spplier Socket supplier
     */
    HtWire(final URI uri, final BiFunc<String, Integer, Socket> spplier) {
        this(
            uri.getHost(),
            new Ternary<>(
                () -> uri.getPort() == -1,
                () -> uri.toURL().getDefaultPort(),
                uri::getPort
            ),
            spplier
        );
    }

    /**
     * Ctor.
     * @param addr The address of the server
     * @param tcp The TCP port source
     * @param spplier Supplier of sockets
     */
    HtWire(final String addr, final Scalar<Integer> tcp,
        final BiFunc<String, Integer, Socket> spplier) {
        this.address = addr;
        this.port = tcp;
        this.supplier = spplier;
    }

    @Override
    public Input send(final Input input) throws IOException {
        try (
            final Socket socket = new IoCheckedBiFunc<>(this.supplier).apply(
                this.address, new IoCheckedScalar<>(this.port).value()
            );
            final InputStream source = input.stream();
            final InputStream ins = socket.getInputStream();
            final OutputStream ous = socket.getOutputStream()
        ) {
            final byte[] buf = new byte[HtWire.LENGTH];
            while (true) {
                final int len = source.read(buf);
                if (len < 0) {
                    break;
                }
                ous.write(buf, 0, len);
            }
            return new InputOf(new BytesOf(ins).asBytes());
        }
    }
}
