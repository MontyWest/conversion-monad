val fs2Version      = "3.0.4"
val catsVersion     = "2.6.1"

val cats = List(
  "org.typelevel" %% "cats-core"   % catsVersion,
  "org.typelevel" %% "cats-effect" % "3.1.1",
  "org.typelevel" %% "kittens"     % "2.2.1"
)

val fs2 = List(
  "co.fs2" %% "fs2-core"
).map(_ % fs2Version)

val testing = List(
  "org.scalatest" %% "scalatest"                     % "3.2.9",
  "org.typelevel" %% "cats-effect-testing-scalatest" % "1.1.1",
  "org.typelevel" %% "cats-laws"                     % catsVersion,
  "org.typelevel" %% "discipline-scalatest"          % "2.1.5",
).map(_ % "test")

lazy val root = (project in file("."))
  .settings(
    organization := "pierwest",
    name := "conversion-monad",
    version := "0.0.1",
    scalaVersion := "2.13.6",
    libraryDependencies ++= (cats ++ fs2 ++ testing)
  )
  .settings(scalaSettings)
  .settings(compilerPluginSettings)

lazy val scalaSettings = Seq(
  scalacOptions ++= Seq(
        "-deprecation",  // Emit warning and location for usages of deprecated APIs.
        "-explaintypes", // Explain type errors in more detail.
        "-encoding",
        "UTF-8",
        "-feature",                      // Emit warning and location for usages of features that should be imported explicitly.
        "-language:existentials",        // Existential types (besides wildcard types) can be written and inferred
        "-language:higherKinds",         // Allow higher-kinded types
        "-language:implicitConversions", // Allow definition of implicit functions called views
        "-unchecked",                    // Enable additional warnings where generated code depends on assumptions.
        "-Xcheckinit",                   // Wrap field accessors to throw an exception on uninitialized access.
        "-Xfatal-warnings",              // Fail the compilation if there are any warnings.
        "-Xlint:adapted-args",           // Warn if an argument list is modified to match the receiver.
        "-Xlint:constant",               // Evaluation of a constant arithmetic expression results in an error.
        "-Xlint:delayedinit-select",     // Selecting member of DelayedInit.
        "-Xlint:doc-detached",           // A Scaladoc comment appears to be detached from its element.
        "-Xlint:inaccessible",           // Warn about inaccessible types in method signatures.
        "-Xlint:infer-any",              // Warn when a type argument is inferred to be `Any`.
        "-Xlint:missing-interpolator",   // A string literal appears to be missing an interpolator id.
        "-Xlint:nullary-unit",           // Warn when nullary methods return Unit.
        "-Xlint:option-implicit",        // Option.apply used implicit view.
        "-Xlint:package-object-classes", // Class or object defined in package object.
        "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
        "-Xlint:private-shadow",         // A private field (or class parameter) shadows a superclass field.
        "-Xlint:stars-align",            // Pattern sequence wildcard must align with sequence component.
        "-Xlint:type-parameter-shadow",  // A local type parameter shadows a type already in scope.
        "-Ywarn-dead-code",              // Warn when dead code is identified.
        "-Ywarn-extra-implicit",         // Warn when more than one implicit parameter section is defined.
        "-Ywarn-numeric-widen",          // Warn when numerics are widened.
        "-Ywarn-unused:implicits",       // Warn if an implicit parameter is unused.
        "-Ywarn-unused:imports",         // Warn if an import selector is not referenced.
        "-Ywarn-unused:locals",          // Warn if a local definition is unused.
        "-Ywarn-unused:params",          // Warn if a value parameter is unused.
        "-Ywarn-unused:patvars",         // Warn if a variable bound in a pattern is unused.
        "-Ywarn-unused:privates",        // Warn if a private member is unused.
        "-Ywarn-value-discard",          // Warn when non-Unit expression results are unused.
        "-Ybackend-parallelism",
        "8",                                         // Enable paralellisation — change to desired number!
        "-Ycache-plugin-class-loader:last-modified", // Enables caching of classloaders for compiler plugins
        "-Ycache-macro-class-loader:last-modified"   // and macro definitions. This can lead to performance improvements.
      )
)

lazy val compilerPluginSettings = Seq(
  addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.0" cross CrossVersion.full),
  addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
)
