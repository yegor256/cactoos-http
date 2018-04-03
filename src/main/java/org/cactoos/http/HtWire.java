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
import java.net.Socket;
import java.net.URI;
import org.cactoos.Input;
import org.cactoos.io.BytesOf;
import org.cactoos.io.InputOf;

/**
 * Wire.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.1
 */
public final class HtWire {

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
     * Ctor.
     * @param uri The address of the server
     */
    public HtWire(final URI uri) {
        this(uri.getHost(), uri.getPort());
    }

    /**
     * Ctor.
     * @param addr The address of the server
     */
    public HtWire(final String addr) {
        // @checkstyle MagicNumber (1 line)
        this(addr, 80);
    }

    /**
     * Ctor.
     * @param addr The address of the server
     * @param tcp The TCP port
     */
    public HtWire(final String addr, final int tcp) {
        this.address = addr;
        this.port = tcp;
    }

    /**
     * Send and read result.
     * @param input The source of data
     * @return The stream to read from
     * @throws IOException If fails
     */
    public Input send(final Input input) throws IOException {
        try (final Socket socket = new Socket(this.address, this.port);
            final InputStream source = input.stream();
            final InputStream ins = socket.getInputStream();
            final OutputStream ous = socket.getOutputStream()) {
            final byte[] buf = new byte[HtWire.LENGTH];
            while (true) {
                final int len = source.read(buf);
                if (len < 0) {
                    break;
                }
                ous.write(buf, 0, len);
            }
            return new InputOf(
                new BytesOf(
                    new InputOf(ins)
                ).asBytes()
            );
        }
    }

}
