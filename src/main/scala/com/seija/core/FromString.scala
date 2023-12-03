package com.seija.core
import scala.collection.mutable;
import com.seija.core.reflect.Into;
import scala.util.Try


def formString[T](str: String)(using v: Into[String,T]): Try[T] = v.tryInto(str)
