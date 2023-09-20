package slog.output

sealed trait LogOutput extends Any {
  def plainText: String
  def length: Int = plainText.length

  def map(f: String => String): LogOutput
  def splitAt(index: Int): (LogOutput, LogOutput)
}

object EmptyOutput extends LogOutput {
  override val plainText: String = ""

  override def map(f: String => String): LogOutput = f(plainText) match {
    case "" => EmptyOutput
    case s => new TextOutput(s)
  }

  override def splitAt(index: Int): (LogOutput, LogOutput) = (EmptyOutput, EmptyOutput)

  override def toString: String = "empty"
}

class TextOutput(val value: String) extends AnyVal with LogOutput {
  def plainText: String = if (value == null) "null" else value

  override def map(f: String => String): LogOutput = new TextOutput(f(plainText))

  override def splitAt(index: Int): (LogOutput, LogOutput) =
    (new TextOutput(plainText.substring(0, index)), new TextOutput(plainText.substring(index)))

  override def toString: String = s"text($plainText)"
}

class CompositeOutput(val entries: List[LogOutput]) extends LogOutput {
  override lazy val plainText: String = entries.map(_.plainText).mkString

  override def map(f: String => String): LogOutput = new CompositeOutput(entries.map(_.map(f)))

  override def splitAt(index: Int): (LogOutput, LogOutput) = {
    def recurse(left: List[LogOutput], right: List[LogOutput], chars: Int): (LogOutput, LogOutput) = {
      if (right.isEmpty) {
        (new CompositeOutput(left), EmptyOutput)
      } else {
        val head = right.head
        val length = head.length
        chars + length match {
          case l if l == index => (new CompositeOutput(left ::: List(head)), new CompositeOutput(right.tail))
          case l if l > index =>
            val (left1, left2) = head.splitAt(index - chars)
            (new CompositeOutput(left ::: List(left1)), new CompositeOutput(left2 :: right.tail))
          case l => recurse(left ::: List(head), right.tail, l)
        }
      }
    }
    recurse(Nil, entries, 0)
  }

  override def toString: String = s"composite(${entries.mkString(", ")})"
}