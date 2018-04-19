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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.util.function.BiFunction;
import org.cactoos.Input;
import org.cactoos.io.BytesOf;
import org.cactoos.io.InputOf;

/**
 * Wire.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class HtWire implements Wire {

    /**
     * Buffer length.
     */
    private static final int LENGTH = 16384;

    /**
     * Address of the server.
     */
    private final String address;

    /**
     * TCP port.
     */
    private final int port;

    /**
     * Supplier of sockets.
     */
    private final BiFunction<String, Integer, Socket> supplier;

    /**
     * Ctor.
     * @param uri The address of the server
     */
    public HtWire(final URI uri) {
        this(uri, HtWire::socket);
    }

    /**
     * Ctor.
     * @param addr The address of the server
     */
    public HtWire(final String addr) {
        // @checkstyle MagicNumber (1 line)
        this(addr, 80, HtWire::socket);
    }

    /**
     * Ctor.
     * @param addr The address of the server
     * @param tcp The TCP port
     */
    public HtWire(final String addr, final int tcp) {
        this(addr, tcp, HtWire::socket);
    }

    /**
     * Ctor.
     * @param uri The address of the server
     * @param spplier Socket supplier
     */
    HtWire(final URI uri,
        final BiFunction<String, Integer, Socket> spplier) {
        this(uri.getHost(), HtWire.prt(uri), spplier);
    }

    /**
     * Ctor.
     * @param addr The address of the server
     * @param tcp The TCP port
     * @param spplier Supplier of sockets
     */
    HtWire(final String addr, final int tcp,
        final BiFunction<String, Integer, Socket> spplier) {
        this.address = addr;
        this.port = tcp;
        this.supplier = spplier;
    }

    @Override
    public Input send(final Input input) throws IOException {
        try (
            final Socket socket = this.supplier.apply(this.address, this.port);
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

    /**
     * Get port number from URI.
     * @param uri URI to get the port from
     * @return Port number
     */
    private static int prt(final URI uri) {
        final int port;
        if (uri.getPort() == -1) {
            try {
                port = uri.toURL().getDefaultPort();
            } catch (final MalformedURLException ex) {
                throw new IllegalStateException(ex);
            }
        } else {
            port = uri.getPort();
        }
        return port;
    }

    /**
     * Create socket from address and port.
     * @param addr Address for the socked
     * @param tcp Port for the socket
     * @return Created socket
     */
    private static Socket socket(final String addr, final Integer tcp) {
        try {
            return new Socket(addr, tcp);
        } catch (final IOException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
