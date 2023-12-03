package com.seija.core

import scala.scalanative.posix.timeOps.tmOps
import scala.scalanative.posix.time
import scala.scalanative.posix.time.tm
import scala.scalanative.unsafe.{Ptr, fromCString}
import scala.scalanative.unsafe.stackalloc
import scala.scalanative.posix.time.time_t

class Date(val milliseconds: Long, dms: Ptr[time.time_t]) {
    time.tzset()
    val date = new tmOps(time.localtime(dms).asInstanceOf[Ptr[tm]])

    private val dst: Int = if (time.daylight() == 1) { 3600000 } else { 0 }

    def hour24: Int = date.tm_hour
    def minuteOfHour: Int = date.tm_min
    def secondOfMinute: Int = date.tm_sec
    def milliOfSecond: Int = (milliseconds % 1000L).asInstanceOf[Int]
    def isAM: Boolean = hour24 < 12
    def timeZoneOffsetMillis: Int = dst - (1000 * time.timezone().asInstanceOf[Int])
    def year: Int = date.tm_year + 1900
    def month: Int = date.tm_mon
    def dayOfWeek: Int = date.tm_wday + 1
    def dayOfMonth: Int = date.tm_mday
    def dayOfYear: Int = date.tm_yday

    def timeZone: String = {
        val ptr = if (time.daylight() == 0) {
            time.tzname()._1
        } else {
            time.tzname()._2
        }
        fromCString(ptr)
    }

    def Y: String = year.toString
    def m: String = NumberFormatUtil.int(month + 1, 2)
    def L: String = NumberFormatUtil.int(milliOfSecond, 3) 
    def d: String = NumberFormatUtil.int(dayOfMonth, 2)
    def T: String = s"$H:$M:$S"
    def H: String = NumberFormatUtil.int(hour24, 2)
    def M: String = NumberFormatUtil.int(minuteOfHour, 2)
    def S: String = NumberFormatUtil.int(secondOfMinute, 2)
}

object Date {
    def apply(l:Long):Date = {
        val bmsptr = stackalloc[time_t]()
        !bmsptr = l / 1000L
        new Date(l, bmsptr)
    }
}

object NumberFormatUtil {
  def int(i: Int, digits: Int): String = {
    val s = i.toString
    val padTo = digits - s.length
    padTo match {
      case _ if `padTo` <= 0 => s
      case 1 => "0".concat(s)
      case 2 => "00".concat(s)
      case 3 => "000".concat(s)
      case 4 => "0000".concat(s)
      case 5 => "00000".concat(s)
      case 6 => "000000".concat(s)
      case 7 => "0000000".concat(s)
      case 8 => "00000000".concat(s)
      case 9 => "000000000".concat(s)
      case _ => "".padTo(padTo, '0')
    }
  }
}