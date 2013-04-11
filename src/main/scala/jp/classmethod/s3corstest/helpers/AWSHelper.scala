package jp.classmethod.s3corstest.helpers

import java.util.Date
import javax.crypto._
import javax.crypto.spec.SecretKeySpec
import java.net.URLEncoder
import sun.misc.BASE64Encoder
import org.scalatra.{Put, HttpMethod}

class S3PUTSignHelper
  extends S3URLProvider
  with PUTMethodProvider
  with AWSCredentialsPropertiesProvider

object S3URLProvider {
  private val protocol = "http"
  private val s3Domain = "s3.amazonaws.com"
  private val awsAccessKeyIdKey = "AWSAccessKeyId"
  private val expiresKey = "Expires"
  private val signatureKey = "Signature"
  private val expireSeconds = 300
}

trait S3URLProvider { self: HttpMethodProvider with AWSCredentialsProvider =>

  import S3URLProvider._
  import S3SignatureProvider._
  import jp.classmethod.s3corstest.utils.URLUtil._

  def s3ObjectUrl(
    fileInfo: S3FileInfo,
    requestHeaderMap: Map[String, String]): String = {

    require(fileInfo != null)
    
    val expires = new Date().getTime / 1000 + expireSeconds
    val signature = s3Signature(httpMethod, fileInfo, expires, requestHeaderMap, credentials.secretKey)

    val params = Map(
        awsAccessKeyIdKey -> credentials.accessKey,
        expiresKey -> expires.toString,
        signatureKey -> signature)
    val url = createUrlString(protocol, s3Domain, None, Seq(fileInfo.bucketName, fileInfo.fileName), params)

    URLEncoder.encode(url, "UTF-8")
  }

  def s3ObjectUrl(fileInfo: S3FileInfo): String = s3ObjectUrl(fileInfo, Map.empty[String, String])
}

trait HttpMethodProvider {
  protected def httpMethod: HttpMethod
}

trait PUTMethodProvider extends HttpMethodProvider {
  protected val httpMethod = Put
}

object S3SignatureProvider {
  private val secretAlgorithm = "HmacSHA1"
  
  def s3Signature(
    httpMethod: HttpMethod,
    fileInfo: S3FileInfo,
    expires: Long,
    headers: Map[String, String],
    secretKey: String): String = {

    def createStringToSign: String = {
      val headerStrs = for {
        (key, value) <- headers
      } yield key + ":" + value

      val contentMD5 = ""
      val contentType = fileInfo.mimeType getOrElse ""
      val resource = "/" + fileInfo.bucketName + "/" + fileInfo.fileName

      val params = Seq(httpMethod.toString, contentMD5, contentType, expires.toString) ++ headerStrs :+ resource
      params.mkString("\n")
    }

    val stringToSign = createStringToSign
    signatureByString(stringToSign, secretKey)
  }

  private def signatureByString(stringToSign: String, secretKey: String) = {
    val key = new SecretKeySpec(secretKey.getBytes("UTF-8"), secretAlgorithm)
    val mac = Mac.getInstance(key.getAlgorithm)
    mac.init(key)
    val binary = mac.doFinal(stringToSign.getBytes("UTF-8"))
    val base64 = new BASE64Encoder().encode(binary)
    URLEncoder.encode(base64, "UTF-8")
  }
}

case class S3FileInfo(bucketName: String, fileName: String, mimeType: Option[String])

