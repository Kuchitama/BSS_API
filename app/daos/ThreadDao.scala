package daos

import entities.{Thread, Threads}
import org.joda.time.DateTime

import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import com.github.tototoshi.slick.H2JodaSupport._

object ThreadDao extends UseDBFeature with CurrentTimeFeature {
  def findById(id: Long) = useDB{ db =>
    val filterQuery = for(thread <- Threads.tableQuery if thread.id === id) yield thread
    db.run(filterQuery.result).map(_.headOption).map(_.map(asThread))
  }


  def asThread: PartialFunction[(Option[Long], String, String, Long, DateTime, DateTime), Thread] = {
    case (id: Option[Long], title: String, tags: String, accountId: Long, createdTime: DateTime, updatedTime: DateTime) => {
      Thread(id.get, title, tags, accountId, createdTime, updatedTime)
    }
  }
}