package com.seanshubin.schulze

import com.seanshubin.black_box_test_5.application.{DefaultMavenTableOfContentsApplication, DefaultMavenSpecificationRunnerApplication}

object Main extends App {
  DefaultMavenTableOfContentsApplication.main(Array(".", "target"))
  DefaultMavenSpecificationRunnerApplication.main(Array("run-every", "1", "out-of", "1", "default-timeout", "1 hour"))
}
