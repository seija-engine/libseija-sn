package ui

class Template {
    var children:List[BaseControl] = List()

    def applyTo(parent:BaseControl) = {
        for(control <- this.children) {
            parent.AddChild(control)
        }
    }
}
