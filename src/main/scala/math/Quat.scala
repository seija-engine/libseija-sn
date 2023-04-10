package math

case class Quat(var x:Float,val y:Float,val z:Float,val w:Float) {
  
}

object Quat {
    val identity = new Quat(0,0,0,1)

    def fromXYZW(x:Float,y:Float,z:Float,w:Float):Quat = new Quat(x,y,z,w)

    def fromAxisAngle(axis:Vector3,angle:Float):Quat = {
        val halfAngle = angle * 0.5;
        val s = Math.sin(halfAngle).asInstanceOf[Float];
        val c = Math.cos(halfAngle).asInstanceOf[Float];
        val v = axis.mulScalar(s);
        new Quat(v.x,v.y,v.z,c);   
    }
}
