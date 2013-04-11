package jp.classmethod.s3corstest

import org.scalatra._
import scalate.ScalateSupport
import grizzled.slf4j.Logger

class RootController extends S3CorsTestStack {
  
  private[this] lazy val logger = Logger(getClass)

  get("/") {
    logger info "GET /"
    contentType = "text/html"
    jade("index")
  }

}
