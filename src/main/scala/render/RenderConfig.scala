package render
import scalanative.unsafe._
import ui.core.FFISeijaUI

case class RenderConfig(
    configPath: String,
    scriptPath: String,
    renderLibPaths: List[String]) {
        def toPtr(): Ptr[Byte] = {
            val ptr = FFISeijaRender.createRenderConfig()
            FFISeijaRender.renderConfigSetConfigPath(ptr, configPath)
            FFISeijaRender.renderConfigSetScriptPath(ptr, scriptPath)
            renderLibPaths.foreach(path => FFISeijaRender.renderConfigAddRenderLibPath(ptr, path))
            FFISeijaRender.renderConfigAddPbr(ptr);
            FFISeijaUI.renderConfigSetUI(ptr)
            ptr
        }
}