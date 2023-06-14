package ui.command

import core.reflect.Into

trait ICommand {
    def Execute(params:Any):Unit;
}

case class FCommand(val func:(Any) => Unit) extends ICommand {
  override def Execute(params: Any): Unit = { this.func(params); }
}

object FCommand {
  given Into[FCommand,Option[ICommand]] with {
    def into(fromValue: FCommand): Option[ICommand] = { Some(fromValue) }
  }
}