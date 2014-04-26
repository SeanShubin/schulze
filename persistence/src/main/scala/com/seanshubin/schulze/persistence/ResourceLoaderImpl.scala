package com.seanshubin.schulze.persistence

import java.io.{InputStreamReader, Reader}
import java.nio.charset.Charset

class ResourceLoaderImpl(charset:Charset) extends ResourceLoader {
  def withReaderFor[T](resourceName: String)(useReader: (Reader) => T):T = {
    val reader:Reader = new InputStreamReader(getClass.getResourceAsStream(resourceName), charset)
    try {
      useReader(reader)
    } finally {
      reader.close()
    }
  }
}
