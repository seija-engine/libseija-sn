package command

trait ICommand {
    def Execute(params:Any):Unit;
}

case class FCommand(val func:(Any) => Unit) extends ICommand {
  override def Execute(params: Any): Unit = { this.func(params); }
}