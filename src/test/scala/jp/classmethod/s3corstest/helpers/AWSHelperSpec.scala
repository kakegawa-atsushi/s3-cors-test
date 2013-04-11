package jp.classmethod.s3corstest.helpers

import org.specs2.mutable._
import java.lang.IllegalArgumentException
import java.net.URLDecoder
import java.util.Date
import org.scalatra.Put

class S3PUTSignHelperSpec extends Specification {
  
  "S3PUTSignHelper#s3ObjectUrl" should {
    "return url for S3FileInfo" in {
      val bucketName = "bucketTest"
      val fileName = "fileNameTest"
      val fileInfo = S3FileInfo(bucketName, fileName, Some("mimeType"))
      
      val s3SignHelper = new S3PUTSignHelper
      val url = s3SignHelper.s3ObjectUrl(fileInfo)
      Option(url) must not be none
      url.length must be_>(0)
      
      val decodedUrl = URLDecoder.decode(url, "UTF-8")
      decodedUrl must beMatching("^http.*/%s/%s?.*".format(bucketName, fileName))
      decodedUrl must beMatching(".*AWSAccessKeyId=.*")
      decodedUrl must beMatching(".*Expires=.*")
      decodedUrl must beMatching(".*Signature=.*")
    }
    
    "return exception for null" in {
      val s3SignHelper = new S3PUTSignHelper
      s3SignHelper.s3ObjectUrl(null) must throwA[IllegalArgumentException]
    }
  }
}

class S3PUTSignatureSupportSpec extends Specification with AWSCredentialsProviderStub {

  import S3SignatureProvider._
  
  "S3PUTSignatureSupport#s3Signature" should {
    "return valid signature if it is provided empty requestHeaderMap" in {
      val fileInfo = S3FileInfo("bucketTest", "fileNameTest", Some("mimeTypeTest"))
      val expires = new Date(10000).getTime / 1000 + 300
      val sign = s3Signature(Put, fileInfo, expires, Map.empty[String, String], credentials.secretKey)
      Option(sign) must not be none
      sign.length must be_>(0)
      sign must_== "NqZJ9iKRBOAb1djB5DqwUqo2h78%3D"
    }
    
    "return valid signature if it is not provided empty requestHeaderMap" in {
      val fileInfo = S3FileInfo("bucketTest", "fileNameTest", Some("mimeTypeTest"))
      val expires = new Date(10000).getTime / 1000 + 300
      val sign = s3Signature(Put, fileInfo, expires, Map("x-amz-acl" -> "public-read"), credentials.secretKey)
      Option(sign) must not be none
      sign.length must be_>(0)
      sign must_== "YDQ9fmBEqKyGmxT06SBKyi3%2BjyM%3D"
    }
  }
}

trait AWSCredentialsProviderStub extends AWSCredentialsProvider {
  protected def credentials = AWSCredentials("DummyAccessKey", "DummySecretKey")
}
