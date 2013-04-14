package jp.classmethod.s3corstest.helpers

import java.io.File
import com.amazonaws.auth.{AWSCredentials, PropertiesCredentials}

trait AWSCredentialsSupport {
  protected def credentials: AWSCredentials
}

trait AWSCredentialsPropertiesSupport extends AWSCredentialsSupport {
  protected def credentials = AWSCredentialsPropertiesSupport.credentials
}

object AWSCredentialsPropertiesSupport {
  private lazy val credentials: AWSCredentials = new PropertiesCredentials(new File("credentials.properties"))
}
