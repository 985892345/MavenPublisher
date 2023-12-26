import org.gradle.api.publish.maven.MavenPomDeveloper
import org.gradle.api.publish.maven.MavenPomDeveloperSpec

/**
 * .
 *
 * @author 985892345
 * 2023/12/26 20:11
 */

/**
 * @param githubName github 名称
 * @param name 名称
 * @param email 邮箱
 * @param url 链接 (比如博客)
 * @param organization 组织名称
 * @param organizationUrl 组织 url
 * @param roles 开发承担的角色
 * @param timezone 时区
 * @param properties 属性? (我也不知道是什么)
 */
class DeveloperInformation(
  val githubName: String,
  val name: String? = null,
  val email: String? = null,
  val url: String? = null,
  val organization: String? = null,
  val organizationUrl: String? = null,
  val roles: Set<String>? = null,
  val timezone: String? = null,
  val properties: Map<String, String>? = null,
) {

  internal fun config(developer: MavenPomDeveloper) {
    developer.id.set(githubName)
    name?.let { developer.name.set(it) }
    email?.let { developer.email.set(it) }
    url?.let { developer.url.set(url) }
    organization?.let { developer.organization.set(it) }
    roles?.let { developer.roles.set(it) }
    timezone?.let { developer.timezone.set(it) }
    properties?.let { developer.properties.set(it) }
  }
}