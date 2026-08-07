/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import org.cactoos.BiFunc;
import org.cactoos.Input;
import org.cactoos.Scalar;
import org.cactoos.io.InputOf;
import org.cactoos.scalar.Constant;
import org.cactoos.scalar.Ternary;

/**
 * Wire.
 * @since 0.1
 */
public final class HtWire implements Wire {

    /**
     * Buffer length.
     */
    private static final int LENGTH = 16_384;

    /**
     * Supplier of sockets.
     */
    private final Scalar<Socket> supplier;

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
        this(() -> spplier.apply(addr, tcp.value()));
    }

    /**
     * Ctor.
     * @param spplier Supplier of sockets
     */
    HtWire(final Scalar<Socket> spplier) {
        this.supplier = spplier;
    }

    @Override
    public Input send(final Input input) throws Exception {
        @SuppressWarnings("PMD.CloseResource")
        final Socket socket = this.supplier.value();
        final InputStream ins = socket.getInputStream();
        @SuppressWarnings("PMD.CloseResource")
        final OutputStream ous = socket.getOutputStream();
        try (InputStream source = input.stream()) {
            final byte[] buf = new byte[HtWire.LENGTH];
            while (true) {
                final int len = source.read(buf);
                if (len < 0) {
                    break;
                }
                ous.write(buf, 0, len);
            }
        }
        return new InputOf(ins);
    }
}
