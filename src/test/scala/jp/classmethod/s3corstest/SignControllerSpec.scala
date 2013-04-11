package jp.classmethod.s3corstest

import org.scalatra.test.specs2._
import org.json4s._
import org.json4s.jackson.JsonMethods._

class SignControllerSpec extends ScalatraSpec { def is =
  "GET / on SignController"                     ^
    "should return status 400"                  ! root^
    "should return status 400"                  ! rootWithName^
    "should return status 400"                  ! rootWithType^
    "should return status 200 and return url"   ! rootWithNameAndType^
                                                end

  addServlet(classOf[SignController], "/*")

  def root = get("/put") {
    status must_== 400
  }
  
  def rootWithName = get("/put?name=foo") {
    status must_== 200
  }
  
  def rootWithType = get("/put?type=bar") {
    status must_== 400
  }

  def rootWithNameAndType = get("/put?name=foo&type=bar") {
    status must_== 200
    val json = parse(body)
    val url = for {
      JObject(child) <- json
      JField("url", JString(url)) <- child
    } yield Option(url)
    url must not be none
  }

}
