plugins {
  `kotlin-dsl`
  id("io.github.985892345.MavenPublisher") version "1.1.1-alpha01-SNAPSHOT"
}

group = "io.github.985892345"
version = "1.1.1"

repositories {
  mavenLocal()
  // mavenCentral 快照仓库
  maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
  mavenCentral()
}

dependencies {
  implementation("com.vanniktech:gradle-maven-publish-plugin:0.26.0")
}

publisher {
  description = "一键发布 985892345 的开源库到 mavenCentral"
  createGradlePlugin(
    name = "MavenPublisher",
    id = "io.github.985892345.MavenPublisher",
    implementationClass = "com.g985892345.publisher.MavenPublisherExtension",
    displayName = "Maven 发布插件",
    tags = listOf("mavenCentral", "Publisher")
  )
}