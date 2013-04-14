package jp.classmethod.s3corstest.helpers

import java.util.Date
import java.net.URLEncoder
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.{HttpMethod, AmazonClientException}
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest

class S3SignHelper extends S3URLProvider with AWSCredentialsPropertiesSupport

object S3URLProvider {
  private val expireSeconds = 300
}

trait S3URLProvider { self: AWSCredentialsSupport =>

  import S3URLProvider._

  def putS3ObjectUrl(
    fileInfo: S3FileInfo,
    requestHeaderMap: Map[String, String]): Either[Exception, String] =
    s3ObjectUrl(HttpMethod.PUT, fileInfo, requestHeaderMap)

  def putS3ObjectUrl(fileInfo: S3FileInfo): Either[Exception, String] =
    putS3ObjectUrl(fileInfo, Map.empty[String, String])

  private[this] def s3ObjectUrl(
    httpMethod: HttpMethod,
    fileInfo: S3FileInfo,
    requestHeaderMap: Map[String, String]): Either[Exception, String] = {

    require(fileInfo != null)

    val s3Client = new AmazonS3Client(credentials)
    val request = new GeneratePresignedUrlRequest(fileInfo.bucketName, fileInfo.fileName, httpMethod)
    request.setExpiration(new Date(new Date().getTime + expireSeconds * 1000))
    for (mimeTypeStr <- fileInfo.mimeType) request.setContentType(mimeTypeStr)
    for ((key, value) <- requestHeaderMap) request.addRequestParameter(key, value)

    try {
      val url = s3Client.generatePresignedUrl(request)
      Right(URLEncoder.encode(url.toString, "UTF-8"))
    } catch {
      case e: AmazonClientException => Left(e)
    }
  }
}

case class S3FileInfo(bucketName: String, fileName: String, mimeType: Option[String])
