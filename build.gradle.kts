plugins {
  `kotlin-dsl`
  id("io.github.985892345.MavenPublisher") version "1.1.4"
}

group = "io.github.985892345"
version = "1.1.4"

repositories {
  mavenLocal()
  // mavenCentral 快照仓库
  maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
  mavenCentral()
}

dependencies {
  // https://github.com/vanniktech/gradle-maven-publish-plugin
  // https://vanniktech.github.io/gradle-maven-publish-plugin/central/#publishing-releases
  implementation("com.vanniktech:gradle-maven-publish-plugin:0.34.0")
}

publisher {
  masterDeveloper = DeveloperInformation(
    githubName = "985892345",
    email = "guo985892345@formail.com"
  )
  description = "一键发布 github 开源库到 mavenCentral"
  createGradlePlugin(
    name = "MavenPublisher",
    id = "io.github.985892345.MavenPublisher",
    implementationClass = "com.g985892345.publisher.MavenPublisherExtension",
    displayName = "Maven 发布插件",
    tags = listOf("mavenCentral", "Publisher")
  )
}