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
package org.cactoos.http.io;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import org.cactoos.func.FuncWithFallback;
import org.cactoos.iterable.Joined;
import org.cactoos.iterable.Mapped;
import org.cactoos.iterable.Reversed;
import org.cactoos.list.ListOf;
import org.cactoos.scalar.FallbackFrom;
import org.cactoos.scalar.Folded;
import org.cactoos.scalar.UncheckedScalar;

/**
 * Useful object for tests that needs to timeout on connect.
 *
 * <p>There is no thread-safety guarantee.
 *
 * @since 0.1
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class BlockingSocketServer implements AutoCloseable {

    /**
     * The server.
     */
    private final ServerSocket server;

    /**
     * Sockets needed to block the server.
     */
    private final List<AutoCloseable> blockers;

    /**
     * Ctor.
     */
    public BlockingSocketServer() {
        this.server = new UncheckedScalar<>(
            () -> new ServerSocket(0, 1)
        ).value();
        this.blockers = new UncheckedScalar<>(
            () -> new ListOf<AutoCloseable>(
                new Socket(this.address(), this.port()),
                new Socket(this.address(), this.port())
            )
        ).value();
    }

    /**
     * Retrieve the server address.
     * @return Server address.
     */
    public InetAddress address() {
        return this.server.getInetAddress();
    }

    /**
     * Retrieve the server port.
     * @return A positive port number.
     */
    public int port() {
        return this.server.getLocalPort();
    }

    // @todo #87:30min Update cactoos to at least 0.42 and then use
    //  the new Binary class to replace the if construction below and have
    //  a full OO code here.
    @Override
    public void close() {
        final RuntimeException fail = new RuntimeException("Cannot close");
        final boolean failed = new UncheckedScalar<>(
            new Folded<>(
                false,
                Boolean::logicalOr,
                new Mapped<>(
                    new FuncWithFallback<>(
                        (AutoCloseable sck) -> {
                            sck.close();
                            return false;
                        },
                        new FallbackFrom<>(
                            Exception.class,
                            ex -> {
                                fail.addSuppressed(ex);
                                return true;
                            }
                        )
                    ),
                    new Reversed<>(new Joined<>(this.server, this.blockers))
                )
            )
        ).value();
        if (failed) {
            throw fail;
        }
    }
}
