package com.seija.core

trait ICopy[T] {
   def copy(cur:T):T = { cur }
}


def copyObject[T](cur:T)(using c:ICopy[T]):T = c.copy(cur);