/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http;

import org.cactoos.Input;

/**
 * Wire that is capable of upgrading itself upon an 101 status code.
 * @since 0.1
 * @todo #23:30 min As discovered in #53, the upgrade is not a job of the
 *  wire because it need reading the contents of the response (in this case,
 *  status code). So, this feature would be under the responsibility of a
 *  response-like object. It must be implemented someway like this:
 *  new HtUpgradedResponse(
 *  new IterableOf&lt;&gt;(
 *  new MapEntry&lt;Func&lt;String, Boolean&gt;, Func&lt;URI, Wire&gt;&gt;(
 *  upgrade -&gt; upgrade.contains("TLS"),
 *  HtSecureWire::new
 *  )
 *  )
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
     * @param origin Origin wire
     */
    public HtUpgradeWire(final Wire origin) {
        this.origin = origin;
    }

    @Override
    public Input send(final Input input) throws Exception {
        return this.origin.send(input);
    }
}
