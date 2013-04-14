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
    logger info "GET /sign/put"

    val fileInfo = for {
      objectName <- params.getAs[String]("name")
      mimeType <- params.getAs[String]("type")
    } yield S3FileInfo(targetBucketName, objectName, Some(mimeType))

    val fileInfoEither = fileInfo match {
      case Some(s3FileInfo: S3FileInfo) => Right(s3FileInfo)
      case None => Left(new IllegalStateException("invalid params."))
    }

    val result = fileInfoEither.right flatMap {
      val s3SignHelper = new S3SignHelper
      s3SignHelper.putS3ObjectUrl(_)
    }

    result match {
      case Right(url: String) => Ok(Map("url" -> url))
      case Left(e: Exception) => halt(400, e.getMessage)
    }
  }
}

object SignController {
  private val targetBucketName = "classmethod-s3-cors-test"
}
