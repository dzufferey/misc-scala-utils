# misc-scala-utils

![Version 1.1.0](https://img.shields.io/badge/version-1.1.0-green.svg)

A collection of scala utils methods that I use across mutliple projects, but does not fit a single category

## Using it

To use it in your projects your need to add the following two lines in your `build.sbt`:
```scala
resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies += "com.github.dzufferey" %% "misc-scala-utils" % "1.1.0"
```

The first line is requried if you want to use it in some other project.
If you want to use it locally do not add the `resolvers` line but instead run `sbt publishLocal`.

## Disabling Logger at compile time

The Logger uses macro to avoid creating strings (or call-by-name closures).
If this is not enough (benchmarking), you can even remove them by setting the right option:

Run sbt using `sbt -DdisableLogging=true` and then recompile your project.
The logging should be gone.

## Compiling

This project requires java 11 and can be build it using [sbt](http://www.scala-sbt.org/).

Then, in a console, execute:
```
$ sbt
> compile
```

