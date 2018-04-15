name := "procesadorimagenes"
 
version := "1.0" 
      
lazy val `procesadorimagenes` = (project in file(".")).enablePlugins(PlayJava)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
scalaVersion := "2.11.11"

crossScalaVersions := Seq("2.11.12", "2.12.4")

testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

libraryDependencies ++= Seq( javaJdbc , cache , javaWs , "org.mongodb" % "mongo-java-driver" % "3.4.3", "com.google.cloud" % "google-cloud-vision" % "1.24.1")

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

      