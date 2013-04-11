package jp.classmethod.s3corstest

import org.scalatra._
import org.scalatra.json._
import org.json4s._
import jp.classmethod.s3corstest.helpers._
import grizzled.slf4j.Logger

class SignController extends ScalatraServlet with JacksonJsonSupport {

  import SignController._

  private[this] lazy val logger = Logger(getClass)
  override protected implicit val jsonFormats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  get("/put") {
    logger info "GET /sign/"

    val fileInfo = for {
      objectName <- params.getAs[String]("name")
    } yield S3FileInfo(targetBucketName, objectName, params.getAs[String]("type"))

    fileInfo match {
      case Some(fileInfo: S3FileInfo) => {
        val s3SignHelper = new S3PUTSignHelper
        val url = s3SignHelper.s3ObjectUrl(fileInfo)
        Ok(Map("url" -> url))
      }
      case None => halt(400, "invalid params.")
    }
  }
}

object SignController {
  private val targetBucketName = "classmethod-s3-cors-test"
}
