# MavenPublisher
![Maven Central](https://img.shields.io/maven-central/v/io.github.985892345/MavenPublisher?server=https://s01.oss.sonatype.org&label=release)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/io.github.985892345/MavenPublisher?server=https://s01.oss.sonatype.org&label=SNAPSHOT)

一个一键发布 github 开源库到 mavenCentral 的 gradle 插件

基于 [com.vanniktech.maven.publish](https://github.com/vanniktech/gradle-maven-publish-plugin) 定制，
简化操作 (感谢 com.vanniktech.maven.publish)

## 使用
build.gradle.kts 引入以下插件:
```kotlin
plugins {
  id("io.github.985892345.MavenPublisher") version "x.y.z"
}
```

相关配置更多可以查看 [Publisher](./src/main/kotlin/com/g985892345/publisher/Publisher.kt) 类代码注释
```kotlin
// build.gradle.kts 中
publisher {
  masterDeveloper = DeveloperInformation("985892345")
  description = "一键发布 985892345 的开源库到 mavenCentral"
  
  // 用于发布 gradle 插件
  // 但仍然需要你主动依赖 `java-gradle-plugin` 插件
  createGradlePlugin(
    name = "MavenPublisher",
    id = "io.github.985892345.MavenPublisher",
    implementationClass = "com.g985892345.publisher.MavenPublisherExtension",
    displayName = "Maven 发布插件",
    tags = listOf("mavenCentral", "Publisher")
  )
}
```

最后运行以下命令即可发布:
```shell
# 发布到本地仓库
./gradlew publishToMavenLocal

# 如果 version 以 SNAPSHOT 结尾，发布到 mavenCentral 的快照仓库
./gradlew publishToMavenCentral
```
详细步骤可以查看 [com.vanniktech.maven.publish Publishing release](https://vanniktech.github.io/gradle-maven-publish-plugin/central/#publishing-releases)

<details>
<summary>mavenCentral 快照仓库配置</summary>

```kotlin
// setting.gradle.kts
// gradle 插件仓库地址
pluginManagement {
  repositories {
    // ...
    // mavenCentral 快照仓库
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
  }
}

// 依赖地址
// 这个 dependencyResolutionManagement 为 Android 端的写法，该写法用于统一所有模块依赖
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    // ...
    // mavenCentral 快照仓库
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
  }
}
```
如果不使用 `dependencyResolutionManagement` 则采取以下写法
```kotlin
// build.gradle.kts
repositories {
  // mavenCentral 快照仓库
  maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}
```
</details>

## 注
本插件只支持 github 发布 mavenCentral 的配置

如果你还未拥有自己的 mavenCentral 仓库，可以查看以下教程：
- [Android库发布到Maven Central超详细攻略](https://juejin.cn/post/7044831526671876110)
- [来开源吧！发布开源组件到 MavenCentral 仓库超详细攻略](https://juejin.cn/post/7135457823055413278)
- [向 MavenCentral 丢个包](https://juejin.cn/post/7074773046707355661)
- [2022年1月最新实践将依赖上传Maven中央仓库](https://blog.csdn.net/slipperySoap/article/details/122732707?utm_medium=distribute.pc_aggpage_search_result.none-task-blog-2~aggregatepage~first_rank_ecpm_v1~rank_v31_ecpm-1-122732707-null-null.pc_agg_new_rank&utm_term=%E7%94%B3%E8%AF%B7maven%E4%BB%93%E5%BA%93&spm=1000.2123.3001.4430)

**你只需要注册 maven 账号并且创建好密钥即可使用该插件**

## 报错
因为单独用代码设置的 groupId，所以在 gradle.properties 设置 GROUP 时会报以下异常，取消 GROUP 即可
```
The value for extension 'mavenPublishing' property 'groupId$plugin' is final and cannot be changed any further.
```