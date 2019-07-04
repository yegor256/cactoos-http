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

import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.cactoos.Input;
import org.cactoos.Scalar;
import org.cactoos.io.InputOf;
import org.cactoos.iterable.IterableOf;
import org.cactoos.text.TextOf;
import org.hamcrest.Description;
import org.hamcrest.MatcherAssert;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.core.AllOf;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.llorllale.cactoos.matchers.Assertion;
import org.llorllale.cactoos.matchers.TextHasString;
import org.takes.http.FtRemote;
import org.takes.tk.TkText;

/**
 * Test case for {@link HtTimedWire}.
 *
 * @since 0.1
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle JavadocVariableCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class HtTimedWireTest {

    private ServerSocket server;

    @Before
    public void openServerWithOneSlot() throws Exception {
        this.server = new ServerSocket(0, 1);
    }

    @After
    public void closeServer() throws Exception {
        this.server.close();
    }

    @Test
    public void worksFine() throws Exception {
        // @checkstyle MagicNumberCheck (1 line)
        final long timeout = 1000;
        new FtRemote(new TkText("Hello, world!")).exec(
            home -> MatcherAssert.assertThat(
                new TextOf(
                    new HtResponse(
                        new HtTimedWire(new HtWire(home), timeout),
                        new Get(home)
                    )
                ),
                new TextHasString("HTTP/1.1 200 ")
            )
        );
    }

    // @checkstyle MagicNumberCheck (1 line)
    @Test(timeout = 1000)
    public void failsAfterTimeout() throws Exception {
        // @checkstyle MagicNumberCheck (1 line)
        final long timeout = 100;
        final Wire wire = new HtTimedWire(
            input -> {
                TimeUnit.SECONDS.sleep(timeout + 1);
                return input;
            },
            timeout
        );
        new Assertion<>(
            "fails with timeout exception",
            () -> wire.send(new InputOf()),
            new ThrowsTimeoutExceptionMatcher()
        ).affirm();
    }

    // @checkstyle MagicNumberCheck (1 line)
    @Test(timeout = 1000)
    @SuppressWarnings("unchecked")
    // @todo #87:30m In the creation of IterableOf without
    //  @SuppressWarnings("unchecked") complication failed with:
    //  Warning:(124, 29) java: unchecked generic array creation
    //  for varargs parameter of type
    //  org.hamcrest.Matcher<? super org.cactoos.Scalar<org.cactoos.Input>>[].
    //  Needs to investigate and fix this warning.
    public void failsAfterTimeoutCheckIfWait() throws Exception {
        final long timeout = 100;
        final long sleep = 10 * timeout;
        final Wire wire = new HtTimedWire(
            input -> {
                TimeUnit.SECONDS.sleep(sleep);
                return input;
            },
            timeout
        );
        final long start = System.currentTimeMillis();
        new Assertion<>(
            "Execution isn't interrupted when a timeout is exceeded",
            () -> wire.send(new InputOf()),
            new AllOf<>(
                new IterableOf<>(
                    new ThrowsTimeoutExceptionMatcher(),
                    new MaximumTimeMatcher(start, sleep)
                ))).affirm();
    }

    /**
     * Class that matches, that execution in not more than
     * {@link org.cactoos.http.HtTimedWireTest.MaximumTimeMatcher#duration}.
     * @todo #87:30m Need to replace this with
     *  https://github.com/llorllale/cactoos-matchers/issues/133
     *  when an issue will be resolved. This is a temporary solution
     *  for asserting a maximum time.
     */
    private final class MaximumTimeMatcher
        extends TypeSafeDiagnosingMatcher<Scalar<Input>> {
        private final long start;
        private final long duration;

        MaximumTimeMatcher(final long start, final long duration) {
            super();
            this.start = start;
            this.duration = duration;
        }

        @Override
        @SuppressWarnings("PMD.UncommentedEmptyMethodBody")
        public void describeTo(final Description description) {
        }

        @Override
        public boolean matchesSafely(
            final Scalar<Input> item,
            final Description description) {
            return System.currentTimeMillis() - start < duration;
        }
    }

    /**
     * Class that matches that {@link java.util.concurrent.TimeoutException}
     * is thrown.
     * @todo #87:60m For now we can't use
     *  {@link org.llorllale.cactoos.matchers.Throws}
     *  to match exception message because message in the exception is null.
     *  I've tried to update to the new version of matchers and
     *  use constructor of Throws with {@link org.hamcrest.Matcher},
     *  but it leads update the cactoos
     *  and after that I can't build a project.
     *  So needs to update dependencies, fix conflicts and replace
     *  this class with {@link org.llorllale.cactoos.matchers.Throws}.
     *  And needs try to remove {@link java.lang.SuppressWarnings} in
     *  {@link org.cactoos.http.HtTimedWireTest}.
     */
    private final class ThrowsTimeoutExceptionMatcher extends
        TypeSafeDiagnosingMatcher<Scalar<Input>> {

        @Override
        @SuppressWarnings("PMD.AvoidCatchingGenericException")
        public boolean matchesSafely(
            final Scalar<Input> item,
            final Description description) {
            // @checkstyle IllegalCatchCheck (10 lines)
            boolean matches;
            try {
                item.value();
                matches = false;
            } catch (final TimeoutException exception) {
                matches = exception.getMessage() == null;
            } catch (final Exception exception) {
                matches = false;
            }
            return matches;
        }

        @Override
        public void describeTo(final Description description) {
            //Not used
        }
    }
}
