package ui.binding

object DataBindingManager {
  def binding(srcObject:Any,srcKey:Option[String],dstObject:Any,dstKey:String,conv:Option[PropertyConverter]): Unit = {
    if(!srcObject.isInstanceOf[INotifyPropertyChanged]) {
      this.applyOnce(srcObject,srcKey,dstObject,dstKey,conv);
      return;
    }
    //println(s"binding srcObject:${srcObject} srcKey:${srcKey} dstObject:${dstObject} dstKey:${dstKey} conv:${conv}")
  }

  protected def applyOnce(srcObject:Any,srcKey:Option[String],dstObject:Any,dstKey:String,conv:Option[PropertyConverter]):Unit = {

  }

  def removeByDst(dstObject:Any): Unit = {
      
  }

}
