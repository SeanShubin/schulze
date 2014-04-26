package com.seanshubin.schulze.persistence

import java.io.Reader

trait ResourceLoader {
  def withReaderFor[T](resourceName:String)(useReader:Reader => T):T
}
