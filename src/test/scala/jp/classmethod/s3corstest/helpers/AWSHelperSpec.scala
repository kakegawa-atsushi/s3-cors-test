package jp.classmethod.s3corstest.helpers

import org.specs2.mutable._
import java.lang.IllegalArgumentException
import java.net.URLDecoder
import com.amazonaws.auth.BasicAWSCredentials

class S3PUTSignHelperSpec extends Specification {
  
  "S3SignHelper#putS3ObjectUrl" should {
    "return url for S3FileInfo" in {
      val bucketName = "bucketTest"
      val fileName = "fileNameTest"
      val fileInfo = S3FileInfo(bucketName, fileName, Some("mimeType"))
      
      val s3SignHelper = new S3SignHelper
      val url = s3SignHelper.putS3ObjectUrl(fileInfo)
      url must not be left

      val urlStr = url.right.get
      urlStr.length must be_>(0)
    }
    
    "return exception for null" in {
      val s3SignHelper = new S3SignHelper
      s3SignHelper.putS3ObjectUrl(null) must throwA[IllegalArgumentException]
    }
  }
}

trait AWSCredentialsProviderStub extends AWSCredentialsSupport {
  protected def credentials = new BasicAWSCredentials("DummyAccessKey", "DummySecretKey")
}
