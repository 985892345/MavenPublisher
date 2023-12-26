# MavenPublisher
![Maven Central](https://img.shields.io/maven-central/v/io.github.985892345/MavenPublisher?server=https://s01.oss.sonatype.org&label=MavenPublisher)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/io.github.985892345/MavenPublisher?server=https://s01.oss.sonatype.org&label=MavenPublisher-SNAPSHOT)

一个一键发布 github 开源库到 mavenCentral 的 gradle 插件

支持如下项目的发布：
- Android
- Java
- Kotlin/Jvm
- Kotlin Multiplatform
- gradle plugin

## 使用
build.gradle.kts 引入以下插件:
```kotlin
plugins {
  id("io.github.985892345.MavenPublisher") version "x.y.z"
}
```
注意该插件需要在以下插件后引入才能判断模块类型:
- com.android.library
- com.gradle.plugin-publish
- org.jetbrains.kotlin.multiplatform
- org.jetbrains.kotlin.jvm
- java

相关配置更多可以查看 [Publisher](./src/main/kotlin/com/g985892345/publisher/Publisher.kt) 类代码注释
```kotlin
// build.gradle.kts 中
publisher {
  masterDeveloper = DeveloperInformation("985892345")
  license = "MIT"
  description = "一键发布 985892345 的开源库到 mavenCentral"
  createGradlePlugin( // 只有 gradle 插件才需要
    id = "io.github.985892345.MavenPublisher",
    implementationClass = "com.g985892345.publisher.MavenPublisherExtension",
    displayName = "Maven 发布插件",
    tags = listOf("mavenCentral", "Publisher")
  )
}
```

最后运行以下命令即可发布:
```shell
# 发布到 mavenCentral，如果 version 以 SNAPSHOT 结尾，则发布到 mavenCentral 的快照仓库
./gradlew toMavenCentral

# 发布到本地仓库
./gradlew toMavenLocal
```

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
本插件只支持 github 发布 mavenCentral 的配置，其他非 github 仓库虽未支持，
但可以参考该项目进行修改，只需简单改下 url 即可

如果你还未拥有自己的 mavenCentral 仓库，可以查看以下教程：
- [Android库发布到Maven Central超详细攻略](https://juejin.cn/post/7044831526671876110)
- [来开源吧！发布开源组件到 MavenCentral 仓库超详细攻略](https://juejin.cn/post/7135457823055413278)
- [向 MavenCentral 丢个包](https://juejin.cn/post/7074773046707355661)
- [2022年1月最新实践将依赖上传Maven中央仓库](https://blog.csdn.net/slipperySoap/article/details/122732707?utm_medium=distribute.pc_aggpage_search_result.none-task-blog-2~aggregatepage~first_rank_ecpm_v1~rank_v31_ecpm-1-122732707-null-null.pc_agg_new_rank&utm_term=%E7%94%B3%E8%AF%B7maven%E4%BB%93%E5%BA%93&spm=1000.2123.3001.4430)

**你只需要注册 maven 账号并且创建好密钥即可使用该插件**
