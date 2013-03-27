scalaVersion := "2.10.0"

scalaSource in Compile <<= (baseDirectory)(_ / "src")

scalaSource in Test <<= (baseDirectory)(_ / "test")

resolvers += "Sonatype OSS Snapshots Repository" at "http://oss.sonatype.org/content/groups/public/"

resolvers += "scala-tools" at "https://oss.sonatype.org/content/groups/scala-tools/"

resolvers += "sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "NativeLibs4Java Repository" at "http://nativelibs4java.sourceforge.net/maven/"

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "spray repo" at "http://repo.spray.io/"

resolvers += "maven repo" at "http://repo1.maven.org/maven2/"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.1.1"

libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.1.1"

libraryDependencies += "org.mockito" % "mockito-all" % "1.9.0"

libraryDependencies += "junit" % "junit" % "4.10"

