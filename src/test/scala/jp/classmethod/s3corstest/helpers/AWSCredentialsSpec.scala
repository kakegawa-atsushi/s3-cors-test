package jp.classmethod.s3corstest.helpers

import org.specs2.mutable._

class AWSCredentialsProviderSpec extends Specification with AWSCredentialsPropertiesProvider {
  
  "AWSCredentialsProvider#credentials" should {
    "return credentials" in {
      Option(credentials) must not be none
    }
    
    "have accessKey that is not null and not empty string" in {
      val accessKey = credentials.accessKey
      Option(accessKey) must not be none
      accessKey.length must be_>(0)
    }
    
    "have secretKey that is not null and not empty string" in {
      val secretKey = credentials.secretKey
      Option(secretKey) must not be none
      secretKey.length must be_>(0)
    }
  }
}