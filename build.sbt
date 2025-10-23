name := "p3-news-sources"
version := "1.0.0"
scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .enablePlugins(PlayJava)
  .settings(
    libraryDependencies ++= Seq(
      guice,
      "com.typesafe.play" %% "play-ahc-ws" % play.core.PlayVersion.current,
      "com.typesafe.play" %% "play-ehcache" % play.core.PlayVersion.current,
      // Test
      "junit" % "junit" % "4.13.2" % Test,
      "org.mockito" % "mockito-core" % "5.12.0" % Test,
      "org.assertj" % "assertj-core" % "3.25.3" % Test,
      "com.typesafe.play" %% "play-test" % play.core.PlayVersion.current % Test
    )
  )
