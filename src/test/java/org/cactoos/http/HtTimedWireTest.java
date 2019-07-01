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
import org.cactoos.text.TextOf;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.TypeSafeDiagnosingMatcher;
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

    private static final String UNUSED = "UNUSED";
    private static final String TIMEOUT = "Did not failed after timeout";
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

    // @todo #87:30m For now I can't find proper OOP alternative for waiting.
    //  Needs to create a new one. I think this can looks like this:
    //  new Wait().seconds(long seconds). Needs to do this in the test
    //  org.cactoos.http.HtTimedWireTest.failsAfterTimeoutCheckIfWait too
    //  @checkstyle MagicNumberCheck (1 line)
    @Test(timeout = 1000)
    public void failsAfterTimeout() throws Exception {
        // @checkstyle MagicNumberCheck (1 line)
        final long timeout = 100;
        final HtTimedWire wire = new HtTimedWire(
            input -> {
                TimeUnit.SECONDS.sleep(timeout + 1);
                return input;
            },
            timeout
        );
        new Assertion<>(
            HtTimedWireTest.TIMEOUT,
            () -> wire.send(new InputOf(HtTimedWireTest.UNUSED)),
            new TimeoutExceptionMatcher()
        )
            .affirm();
    }

    // @checkstyle MagicNumberCheck (1 line)
    @Test(timeout = 1000)
    public void failsAfterTimeoutCheckIfWait() throws Exception {
        final long timeout = 100;
        final long sleep = 10 * timeout;
        final HtTimedWire wire = new HtTimedWire(
            input -> {
                TimeUnit.SECONDS.sleep(sleep);
                return input;
            },
            timeout
        );
        final long current = System.currentTimeMillis();
        new Assertion<>(
            HtTimedWireTest.TIMEOUT, () -> wire.send(
            new InputOf(HtTimedWireTest.UNUSED)
        ), new AllOf(
            new TimeoutExceptionMatcher(),
            new TypeSafeDiagnosingMatcher<Scalar<Input>>() {
                @Override
                protected boolean matchesSafely(
                    final Scalar<Input> item,
                    final Description description) {
                    final long failed = System.currentTimeMillis();
                    return failed - current < sleep;
                }

                @Override
                @SuppressWarnings("PMD.UncommentedEmptyMethodBody")
                public void describeTo(final Description description) {
                }
            }));
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
    private final class TimeoutExceptionMatcher extends
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

    /**
     * Class that matches that use
     * all of {@link org.cactoos.http.HtTimedWireTest.AllOf#matchers}.
     * @todo #87:30m For a some reason org.hamcrest.core.AllOf matcher with
     *  org.cactoos.iterable.IterableOf is not working (compilation error with
     *  unchecked generic array creation for varargs parameter).
     *  Needs to replace this with org.hamcrest.core.AllOf
     *  and deal with compilation error.
     *  And needs try to remove {@link java.lang.SuppressWarnings} in
     *  {@link org.cactoos.http.HtTimedWireTest}.
     */
    private final class AllOf extends TypeSafeDiagnosingMatcher<Scalar<Input>> {

        private final Matcher<Scalar<Input>>[] matchers;

        @SafeVarargs
        private AllOf(final Matcher<Scalar<Input>>... matchers) {
            super();
            this.matchers = matchers;
        }

        @Override
        public void describeTo(final Description description) {
            //Not used
        }

        @Override
        public boolean matchesSafely(
            final Scalar<Input> item,
            final Description description) {
            boolean matches = true;
            for (final Matcher<Scalar<Input>> matcher : this.matchers) {
                matcher.matches(item);
                if (!matcher.matches(item)) {
                    matches = false;
                }
            }
            return matches;
        }
    }

}
