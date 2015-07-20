# misc-scala-utils

A collection of scala utils methods that I use across mutliple projects, but does not fit a single category


## Compiling

This project requires java 6 and can be build it using [sbt](http://www.scala-sbt.org/).
To install sbt follow the instructions at [http://www.scala-sbt.org/release/tutorial/Setup.html](http://www.scala-sbt.org/release/tutorial/Setup.html).

Then, in a console, execute:
```
$ sbt
> compile
```

## Using it

To use it in your projects your need to add the following two lines in your `build.sbt`:
```scala
resolvers +=  "dzufferey maven repo" at "https://github.com/dzufferey/my_mvn_repo/raw/master/repository"

libraryDependencies += "io.github.dzufferey" %% "misc-scala-utils" % "0.1-SNAPSHOT"
```

The last line is requried if you want to use it in some other project.
If you want to use it locally do not add the `resolvers` line but instead run `sbt publishLocal`.

## Disabling Logger at compile time

The Logger uses macro to avoid creating strings (or call-by-name closures).
If this is not enough (benchmarking), you can even remove them by setting the right option:

Run sbt using `sbt -DdisableLogging=true` and then recompile your project.
The logging should be gone.
