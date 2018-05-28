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
import org.cactoos.Input;

/**
 * Wire that is capable of upgrading itself upon an 101 status code.
 * @author Paulo Lobo (pauloeduardolobo@gmail.com)
 * @version $Id$
 * @since 0.1
 * @todo #23:30 min As discovered in #53, the upgrade is not a job of the
 *  wire because it need reading the contents of the response (in this case,
 *  stvatus code). So, this feature would be under the responsibility of a
 *  response-like object. It must be implemented someway like this:
 *  new HtUpgradedResponse(
 *     new IterableOf<>(
 *         new MapEntry<Func<String, Boolean>, Func<URI, Wire>>(
 *             upgrade -> upgrade.contains("TLS"),
 *             HtSecureWire::new
 *         )
 *     )
 *  )
 *  The test HtUpgradeWireTest#testHtUpgrade must be removed after the
 *  implementation of this class.
 */
public final class HtUpgradeWire implements Wire {

    /**
     * Origin wire.
     */
    private final Wire origin;

    /**
     * Ctor.
     * @param origin Origin wire.
     */
    public HtUpgradeWire(final Wire origin) {
        this.origin = origin;
    }

    @Override
    public Input send(final Input input) throws IOException {
        return this.origin.send(input);
    }
}
