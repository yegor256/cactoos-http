<img src="http://cf.jare.io/?u=http%3A%2F%2Fwww.yegor256.com%2Fimages%2Fbooks%2Felegant-objects%2Fcactus.svg" height="100px" />

[![Donate via Zerocracy](https://www.0crat.com/contrib-badge/C63314D6Z.svg)](https://www.0crat.com/contrib/C63314D6Z)

[![EO principles respected here](http://www.elegantobjects.org/badge.svg)](http://www.elegantobjects.org)
[![Managed by Zerocracy](https://www.0crat.com/badge/C63314D6Z.svg)](https://www.0crat.com/p/C63314D6Z)
[![DevOps By Rultor.com](http://www.rultor.com/b/yegor256/cactoos-http)](http://www.rultor.com/p/yegor256/cactoos-http)
[![We recommend IntelliJ IDEA](http://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![Build Status](https://travis-ci.org/yegor256/cactoos-http.svg?branch=master)](https://travis-ci.org/yegor256/cactoos-http)
[![Javadoc](http://www.javadoc.io/badge/org.cactoos/cactoos-http.svg)](http://www.javadoc.io/doc/org.cactoos/cactoos-http)
[![PDD status](http://www.0pdd.com/svg?name=yegor256/cactoos-http)](http://www.0pdd.com/p?name=yegor256/cactoos-http)
[![Maven Central](https://img.shields.io/maven-central/v/org.cactoos/cactoos-http.svg)](https://maven-badges.herokuapp.com/maven-central/org.cactoos/cactoos-http)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/yegor256/cactoos-http/blob/master/LICENSE.txt)

[![Test Coverage](https://img.shields.io/codecov/c/github/yegor256/cactoos-http.svg)](https://codecov.io/github/yegor256/cactoos-http?branch=master)
[![SonarQube](https://img.shields.io/badge/sonar-ok-green.svg)](https://sonarcloud.io/dashboard?id=org.cactoos%3Acactoos-http)

Cactoos-HTTP is a experimental HTTP client, fully object-oriented.

These are the [design principles](http://www.elegantobjects.org#principles) behind Cactoos-HTTP.

All you need is this:

```xml
<dependency>
  <groupId>org.cactoos</groupId>
  <artifactId>cactoos-http</artifactId>
  <version><!-- Get it here: https://github.com/yegor256/cactoos-http/releases --></version>
</dependency>
```

Java version required: 1.8+.

StackOverflow tag is [cactoos](https://stackoverflow.com/questions/tagged/cactoos).

We are well aware of competitors
(most likely they are more powerful than our client, but less object-oriented):

  * [JDK HTTP Client](https://developer.oracle.com/java/jdk-http-client)
  * [Apache HttpClient](https://hc.apache.org/httpcomponents-client-ga/)
  * [JCabi-Http](http://http.jcabi.com)
  * [Google HTTP Java Client](https://github.com/google/google-http-java-client)
  * [Unirest](http://unirest.io/java.html)
  * [OkHttp](http://square.github.io/okhttp/)

## How to use

To make a simple HTTP GET request and read its body:

```java
String body = new TextOf(
  new BodyOf(
    new DefaultResponse(
      new JdkRequest("http://www.google.com")
    )
  ).asString()
);
```

## Questions

Ask your questions related to cactoos library on [Stackoverflow](https://stackoverflow.com/questions/ask) with [cactoos](https://stackoverflow.com/tags/cactoos/info) tag.

## How to contribute?

Just fork the repo and send us a pull request.

Make sure your branch builds without any warnings/issues:

```
mvn clean install -Pqulice
```

## License (MIT)

Copyright (c) 2018 Yegor Bugayenko

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
