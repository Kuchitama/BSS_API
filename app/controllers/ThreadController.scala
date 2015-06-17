package controllers

import daos.ThreadDao
import play.api.mvc._
import utils.{ConfigFeature, LoggerFeature}

import scala.concurrent.Await
import skinny.util.JSONStringOps._
import entities.{User, Thread}


object ThreadController extends Controller
                        with FutureTimeoutFeature
                        with AuthFeature
                        with ConfigFeature
                        with LoggerFeature {

  val SearchLimit = config.getInt("thread.searchLimit")

  def index(id: Long) = Action { implicit request =>
    val thread = Await.result(ThreadDao.findById(id), timeout).getOrElse(throw new IllegalArgumentException(s"Thread[${id}] is not found."))
    Ok(toJSONString(thread))
  }

  def create() = Action { implicit request =>
    request.body.asJson map { jValue =>
      logger.debug(s"======== ${jValue.toString}")

      val title = Option((jValue \ "title").as[String]).find(s=> 0 < s.size && s.size <= 40)
        .getOrElse(throw new IllegalArgumentException("Invalid title length."))
      val tags: Seq[Thread.Tag] = (jValue \ "tags").as[List[Thread.Tag]]

      val idOpt = Await.result(ThreadDao.create(title, tags, ???), timeout)
      val thread = idOpt.flatMap(id => Await.result(ThreadDao.findById(id), timeout))
                       .getOrElse(throw new IllegalArgumentException(s"Created thread is not found."))

      Ok(toJSONString(thread))
    } getOrElse {
      throw new IllegalArgumentException("Expecting Json data")
    }
  }

  def search(q:String, offset:Option[Int] = None, limit:Option[Int] = None, strict:Boolean = true) = Action { implicit request =>
    val user = auth
    val (_offset, _limit) = getOffsetAndLimit(offset, limit)
    val threads = Await.result(ThreadDao.findByTitle(q, _offset, _limit,strict), timeout).toList

    Ok(toJSONString(threads))
  }

  def searchByTags(tags: String, offset:Option[Int] = None, limit:Option[Int] = None) = Action{ implicit request =>
    val user = auth
    val _tags:List[Thread.Tag] = tags.split(",").map(_.trim).toList
    val (_offset, _limit) = getOffsetAndLimit(offset, limit)
    val threads = Await.result(ThreadDao.findByTags(_tags, _offset, _limit), timeout).toList

    Ok(toJSONString(threads))
  }

  private def getOffsetAndLimit(offset: Option[Int], limit: Option[Int]): (Int, Int)= {
    val _offset = offset.getOrElse(0)
    val _limit = limit.map(_.min(SearchLimit)).getOrElse(SearchLimit)
    (_offset, _limit)
  }
}
