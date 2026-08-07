/*
 * SPDX-FileCopyrightText: Copyright (c) 2018-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package org.cactoos.http.io;

import java.util.Arrays;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test case for {@link BoundedByteBuffer}.
 * @since 0.1
 */
final class BoundedByteBufferTest {

    @Test
    void worksWithEqualSameSizeArray() {
        final BoundedByteBuffer buffer = new BoundedByteBuffer(4);
        buffer.offer((byte) 11);
        Arrays.stream(new int[] {1, 2, 3, 4}).forEach(b -> buffer.offer((byte) b));
        MatcherAssert.assertThat(
            buffer.equalTo(new byte[]{1, 2, 3, 4}),
            new IsEqual<>(true)
        );
    }

    @Test
    void worksWithUnequalSameSizeArray() {
        final BoundedByteBuffer buffer = new BoundedByteBuffer(4);
        buffer.offer((byte) 11);
        Arrays.stream(new int[] {1, 2, 3, 5}).forEach(b -> buffer.offer((byte) b));
        MatcherAssert.assertThat(
            buffer.equalTo(new byte[]{1, 2, 3, 4}),
            new IsEqual<>(false)
        );
    }

    @Test
    void onlyKeepsTheLastNBytes() {
        final BoundedByteBuffer buffer = new BoundedByteBuffer(4);
        Arrays.stream(new int[] {1, 2, 3, 4, 5, 6, 7, 8 }).forEach(b -> buffer.offer((byte) b));
        MatcherAssert.assertThat(
            buffer.equalTo(new byte[] {5, 6, 7, 8 }),
            new IsEqual<>(true)
        );
    }

    @Test
    void worksWithSmallerArray() {
        final BoundedByteBuffer buffer = new BoundedByteBuffer(4);
        Arrays.stream(new int[] {1, 2}).forEach(b -> buffer.offer((byte) b));
        MatcherAssert.assertThat(
            buffer.equalTo(new byte[] {1, 2}),
            new IsEqual<>(true)
        );
    }

    @Test
    void worksWithSmallerArrayFailsComparisonWithLarger() {
        final BoundedByteBuffer buffer = new BoundedByteBuffer(4);
        Arrays.stream(new int[] {1, 2}).forEach(b -> buffer.offer((byte) b));
        MatcherAssert.assertThat(
            buffer.equalTo(new byte[] {1, 2, 0}),
            new IsEqual<>(false)
        );
    }

    @Test
    void equalsWhenShiftedOdd() {
        final BoundedByteBuffer buffer = new BoundedByteBuffer(4);
        buffer.offer((byte) 11);
        Arrays.stream(new int[] {1, 2, 1, 2}).forEach(b -> buffer.offer((byte) b));
        MatcherAssert.assertThat(
            buffer.equalTo(new byte[]{1, 2, 1, 2}),
            new IsEqual<>(true)
        );
    }
}
