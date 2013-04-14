package jp.classmethod.s3corstest

import org.scalatra.test.specs2._
import org.json4s._
import org.json4s.jackson.JsonMethods._

class SignControllerSpec extends ScalatraSpec { def is =
  "GET /get on SignController"                  ^
    "should return status 400"                  ! getWithoutName^
    "should return status 200 and return url"   ! getWithName^
  "GET /put on SignController"                  ^
    "should return status 400"                  ! put^
    "should return status 400"                  ! putWithName^
    "should return status 400"                  ! putWithType^
    "should return status 200 and return url"   ! putWithNameAndType^
                                                end

  addServlet(classOf[SignController], "/*")

  def getWithoutName = get("/get") {
    status must_== 400
  }

  def getWithName = get("/get?name=foo") {
    status must_== 200
    val json = parse(body)
    val url = for {
      JObject(child) <- json
      JField("url", JString(url)) <- child
    } yield Option(url)
    url must not be none
  }

  def put = get("/put") {
    status must_== 400
  }

  def putWithName = get("/put?name=foo") {
    status must_== 200
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
