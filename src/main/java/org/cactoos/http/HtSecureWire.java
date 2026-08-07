/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import java.net.Socket;
import java.net.URI;
import javax.net.ssl.SSLSocketFactory;
import org.cactoos.BiFunc;
import org.cactoos.Input;
import org.cactoos.scalar.Constant;

/**
 * Wire that supports https.
 * @since 0.1
 */
public final class HtSecureWire implements Wire {

    /**
     * Address.
     */
    private final String address;

    /**
     * TCP port.
     */
    private final int port;

    /**
     * Socket.
     */
    private final BiFunc<String, Integer, Socket> socket;

    /**
     * Ctor.
     * @param uri The address of the server
     */
    public HtSecureWire(final URI uri) {
        this(uri.getHost(), uri.getPort());
    }

    /**
     * Ctor.
     * @param addr The address of the server
     */
    public HtSecureWire(final String addr) {
        this(addr, 443);
    }

    /**
     * Ctor.
     * @param addr The address of the server
     * @param tcp The TCP port
     */
    public HtSecureWire(final String addr, final int tcp) {
        this(
            addr,
            tcp,
            (host, prt) -> SSLSocketFactory.getDefault().createSocket(host, prt)
        );
    }

    /**
     * Ctor.
     * @param addr The address of the server
     * @param tcp The TCP port
     * @param sck Ssl socket
     */
    public HtSecureWire(final String addr,
        final int tcp, final BiFunc<String, Integer, Socket> sck) {
        this.address = addr;
        this.port = tcp;
        this.socket = sck;
    }

    @Override
    public Input send(final Input input) throws Exception {
        return new HtWire(this.address, new Constant<>(this.port), this.socket)
            .send(input);
    }
}
