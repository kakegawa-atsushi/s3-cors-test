package jp.classmethod.s3corstest.utils

import java.io._

object IOUtil {
  def using[A <: Closeable, B](resource: A)(f: A => B) = {
    try {
      f(resource)
    } finally {
      resource.close
    }
  }
}