package jp.classmethod.s3corstest.utils

object URLUtil {
  def createUrlString(
    protocol: String, 
    host: String, 
    port: Option[Int], 
    path: Seq[String], 
    params: Map[String, String]) = {
    
    val hostString = port match {
      case Some(p) => "%s:%d".format(host, p)
      case None => host
    }
    
    val url = "%s://%s/%s".format(protocol, hostString, path.mkString("/"))
    
    if (params.size == 0) {
      url
    } else {
      val queryString = params.foldRight(List.empty[String]) { (kv, acc) =>
        (kv._1 + "=" + kv._2) :: acc
      }.mkString("&")
      url + "?" + queryString
    }
  }
}