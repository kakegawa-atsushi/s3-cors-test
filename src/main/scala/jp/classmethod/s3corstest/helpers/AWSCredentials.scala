package jp.classmethod.s3corstest.helpers

import java.io._
import java.util.Properties

trait AWSCredentialsProvider {
  protected def credentials: AWSCredentials
}

trait AWSCredentialsPropertiesProvider extends AWSCredentialsProvider {
  protected def credentials = AWSCredentialsPropertiesProvider.credentials
}

object AWSCredentialsPropertiesProvider {
  import jp.classmethod.s3corstest.utils.IOUtil._
  
  private lazy val credentials = loadCredentials

  private[this] def loadCredentials: AWSCredentials = {
    using(new FileInputStream(new File("credentials.properties"))) { is: FileInputStream =>
      val properties = new Properties()
      properties.load(is)
      val accessKey = properties.getProperty("accessKey")
      val secretKey = properties.getProperty("secretKey")
      AWSCredentials(accessKey, secretKey)
    }
  }
}

case class AWSCredentials(accessKey: String, secretKey: String)