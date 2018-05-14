package mdpm
package iam.impl
package util

import org.joda.time.{DateTime, DateTimeZone}

object DateUtcUtil {
  def now() = DateTime.now(DateTimeZone.UTC)
}
