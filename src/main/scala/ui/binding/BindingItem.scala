package ui.binding
import scala.util.Try
import _root_.core.reflect.Assembly


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
case class BindingItem(sourceType:BindingSource,sourceKey:String,dstKey:String,conv:Option[PropertyConverter]);

object BindingItem {
  

  def parse(value:String,dstKey:String):BindingItem = {
    //{Binding Owner checked  ui.BoolAtlasSprite(default.duikong,default.duihao)}
    val startLen = "{Binding".length();
    val remainString = value.substring(startLen,value.length() - 1).trim();
    var args = remainString.split(' ');
    
    val sourceType = args(0) match
      case "Data"  => BindingSource.Data
      case "Owner" => BindingSource.Owner
      case _ => BindingSource.Data
    val srcKey = args(1);
   
    var conv:Option[PropertyConverter] = None;
    if(args.length > 2) {
      conv = this.parseConverter(args(2))
    }
    BindingItem(sourceType,srcKey,dstKey,conv)
  }

  def parseConverter(value:String):Option[PropertyConverter] = {
    //ui.BoolAtlasSprite(default.duikong,default.duihao)
    val strs = value.split('(');
    if(strs(0).isEmpty() || strs.length < 2) return None;
    val convName = strs(0);
    val args = strs(1).substring(0,strs(1).length() - 1).split(',');
    val convInst = Assembly.createInstance(convName);
    if(convInst.isEmpty) return None;
    val converter = convInst.get.asInstanceOf[PropertyConverter];
    converter.init(args);
    Some(converter)
  }

  var bufferArray:Array[Char] = null;
  var charIndex = 0; 
  def parse2(value:String):Try[Unit] = Try {
    this.charIndex = 0;
    this.bufferArray = value.toCharArray();
    val item = this.takeWhile(_!= ' ').get;
    if(item != "{Binding") throw new Exception("BindingItem parse error")
    val dataSourceType = this.parseIdent().get match
      case "Data" => BindingSource.Data
      case "Owner" => BindingSource.Owner
      case  s => throw new Exception(s"unsupported BindingSource:${s}")
    
    println(dataSourceType)
  }

  def nextChar():Option[Char] = {
    if(this.charIndex > this.bufferArray.length - 1) return None;
    Some(this.bufferArray(this.charIndex))
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