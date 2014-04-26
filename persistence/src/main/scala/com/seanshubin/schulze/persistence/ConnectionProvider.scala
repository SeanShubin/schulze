package com.seanshubin.schulze.persistence

import datomic.Connection

trait ConnectionProvider {
  def connection: Connection
}
