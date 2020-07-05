name := "fast-ppr"

version := "13.1"
scalaVersion := "2.11.0"
resolvers ++= Seq(
  "twitterrepo" at "http://maven.twttr.com"
)

// https://mvnrepository.com/artifact/com.twitter/cassovary-core
libraryDependencies += "com.twitter.common" % "metrics" % "0.0.37"
libraryDependencies += "com.twitter" %% "cassovary-core" % "7.1.0"
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.5"
mainClass in (Compile,run) := Some("soal.fastppr.Main")