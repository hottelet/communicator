import sbt._

object Resolvers {
  val allResolvers = Seq(
    Resolver.bintrayRepo("websudos", "oss-releases"),
    "Typesafe repository snapshots"    at "http://repo.typesafe.com/typesafe/snapshots/",
    "Typesafe repository releases"     at "http://repo.typesafe.com/typesafe/releases/",
    "Sonatype repo"                    at "https://oss.sonatype.org/content/groups/scala-tools/",
    "Sonatype releases"                at "https://oss.sonatype.org/content/repositories/releases",
    "Sonatype snapshots"               at "https://oss.sonatype.org/content/repositories/snapshots",
    "Sonatype staging"                 at "http://oss.sonatype.org/content/repositories/staging",
    "Java.net Maven2 Repository"       at "http://download.java.net/maven/2/",
    "Twitter Repository"               at "http://maven.twttr.com",
    "Spray Repository"                 at "http://repo.spray.io",
    "Maven Central Server"             at "http://repo1.maven.org/maven2"
  )
}