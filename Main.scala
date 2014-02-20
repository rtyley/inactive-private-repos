import java.time.ZoneId

import org.kohsuke.github.{GHRepository, GitHub}

import scala.collection.convert.wrapAll._
import scala.tools.jline.console.ConsoleReader

object Main extends App {

  val org = args(0)

  val apiToken = new ConsoleReader().readLine("GitHub API Token >", '*')

  val github = GitHub.connectUsingOAuth(apiToken)

  val privateRepos = github.getOrganization(org).listRepositories().filter(_.isPrivate).toSeq

  def summaryForRepo(r: GHRepository): String = {
    val year = Option(r.getPushedAt).map(_.toInstant.atZone(ZoneId.of("UTC")).getYear).getOrElse("----")
    s"""${year} - ${r.getHtmlUrl} - '${r.getDescription}'"""
  }

  val oldPrivateRepos = privateRepos.sortBy(r => Option(r.getPushedAt)).take(20)

  val emptyPrivateRepos = privateRepos.filter(r => Option(r.getPushedAt).isEmpty)

  emptyPrivateRepos.map(summaryForRepo).foreach(println)

  oldPrivateRepos.map(summaryForRepo).foreach(println)

}
