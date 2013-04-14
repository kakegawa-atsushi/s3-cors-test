package jp.classmethod.s3corstest

import org.scalatra.test.specs2._
import org.json4s._
import org.json4s.jackson.JsonMethods._

class SignControllerSpec extends ScalatraSpec { def is =
  "GET /put on SignController"                  ^
    "should return status 400"                  ! put^
    "should return status 400"                  ! putWithName^
    "should return status 400"                  ! putWithType^
    "should return status 200 and return url"   ! putWithNameAndType^
                                                end

  addServlet(classOf[SignController], "/*")

  def put = get("/put") {
    status must_== 400
  }

  def putWithName = get("/put?name=foo") {
    status must_== 400
  }

  def putWithType = get("/put?type=bar") {
    status must_== 400
  }

  def putWithNameAndType = get("/put?name=foo&type=bar") {
    status must_== 200
    val json = parse(body)
    val url = for {
      JObject(child) <- json
      JField("url", JString(url)) <- child
    } yield Option(url)
    url must not be none
  }

}
