package ui
import java.util.ArrayList

class BaseControl {
    var Name: String = "";
    var ClassName: String = "";
    protected var parent: BaseControl = null;
    protected var childrenList: ArrayList[BaseControl] = null;

    def Enter():Unit = {}

    def Exit():Unit = {}
}