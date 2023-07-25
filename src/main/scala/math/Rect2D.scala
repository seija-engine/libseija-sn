package math

case class Rect2D(width:Float,height:Float,anchorX:Float,anchorY:Float) {
  def left:Float = -this.width * anchorX
  def right:Float = this.width * anchorX
  def top:Float = this.height * anchorY
  def bottom:Float = -this.height * anchorY
}

object Rect2D {
  def zero: Rect2D = Rect2D(0,0,0,0)
}
