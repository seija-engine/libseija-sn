package ui.binding
import scala.util.Try
import _root_.core.reflect.Assembly
import scala.collection.mutable.ListBuffer
import scala.sys.Prop
import scala.util.Success


enum BindingSource(val value:Int) {
  case Owner extends BindingSource(0)
  case Data  extends BindingSource(1)
}

enum BindingType(val value:Int) {
  case Src2Dst extends BindingType(0)
  case Dst2Src extends BindingType(1)
  case Both    extends BindingType(2)
}

/*
{Binding Owner checked  Conv=ui.BoolAtlasSprite(default.duikong,default.duihao) Type=Both}
BindingItem规则
1. 第一个和第二个是固定参数
    a. 第一个为数据源类型,Data表示DataContext里的数据源，Owner表示如果当前控件是别的控件的模板的话，那么是模板所属控件的数据源
    b. 第二个是数据源的属性key，this是特殊值表示是数据源本身
2. 后面所有的属性要带上属性名
    a. Conv表示转换器，支持转换数据类型，默认为空
    b. Type表示绑定类型,默认为Src2Dst单向绑定
*/
case class BindingItem(
  sourceType:BindingSource,
  sourceKey:String,
  dstKey:String,
  conv:Option[PropertyConverter],
  typ:BindingType = BindingType.Src2Dst
);

object BindingItem {
  var bufferArray:Array[Char] = null;
  var charIndex = 0; 
  def parse(key:String,value:String):Try[BindingItem] = Try {
    
    //{Binding Owner checked  Conv=ui.BoolAtlasSprite(default.duikong,default.duihao) Type=Both}
    this.charIndex = 0;
    this.bufferArray = value.toCharArray();
    val item = this.takeWhile(_!= ' ').get;
    if(item != "{Binding") throw new Exception("BindingItem parse error")
    val dataSourceType = this.parseIdent().get match
      case "Data" => BindingSource.Data
      case "Owner" => BindingSource.Owner
      case  s => throw new Exception(s"unsupported BindingSource:${s}")
    this.skipWhite();
    val fieldName = this.parseIdent().get;
    this.skipWhite();
    var nextChar = this.nextChar();
    var conv:Option[PropertyConverter] = None;
    var typ = BindingType.Src2Dst;
    while(nextChar.isDefined && nextChar.get != '}') {
      val propName = this.parseIdent().get;
      propName match {
        case "Conv" => 
          this.skipWhite();
          if(this.nextChar().get != '=') throw new Exception("BindingItem parse error")
          this.charIndex += 1;
          this.skipWhite();
          conv = Some(this.parseConv().get);
        case "Type" =>
          if(this.nextChar().get != '=') throw new Exception("BindingItem parse error")
          this.charIndex += 1;
          typ = BindingType.valueOf(this.parseIdent().get);
        case _ =>
      }
      nextChar = this.nextChar();
    }
    BindingItem(dataSourceType,fieldName,key,conv,typ)
  }

  def nextChar():Option[Char] = {
    if(this.charIndex > this.bufferArray.length - 1) return None;
    Some(this.bufferArray(this.charIndex))
  }

  def parseConv():Try[PropertyConverter] = Try {
    val convName = this.takeWhile(c => c.isLetterOrDigit || c == '.' );
    this.skipWhite();
    if(this.nextChar().get != '(') throw new Exception("BindingItem parse error")
    this.charIndex += 1;
   
    var nextChar = this.nextChar();
    var args:ListBuffer[String] = ListBuffer[String]();
    while(nextChar.isDefined && nextChar.get != ')') {
      val paramValue = this.takeWhile(c => c.isLetterOrDigit || c == '.' || c == '-' );
      this.skipWhite();
      if(this.nextChar() == Some(','))  {
        this.charIndex += 1;
      }
      args.addOne(paramValue.get);
      nextChar = this.nextChar();
    }
    this.charIndex += 1;
    val converter = Assembly.createInstance(convName.get).get.asInstanceOf[PropertyConverter];
    converter.init(args.toArray);
    converter
  }  

  def parseIdent():Option[String] = {
    this.skipWhite();
    this.takeWhile(c => c.isLetterOrDigit)
  }

  

  def skipWhite():Unit = {
    while(this.nextChar().isDefined && this.nextChar().get.isWhitespace) {
      this.charIndex += 1;
    }
  }

  def takeWhile(f:Char => Boolean):Option[String] = {
    var result = "";
    while(this.nextChar().isDefined && f(this.nextChar().get)) {
      result += this.nextChar().get;
      this.charIndex += 1;
    }
    if(result.isEmpty()) return None;
    Some(result);
  }
}