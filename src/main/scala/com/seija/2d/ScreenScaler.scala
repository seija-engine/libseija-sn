package com.seija.`2d`
import com.seija.core.{RawComponentBuilder,RawComponent}
import com.seija.core.Entity
import com.seija.core.App;
import com.seija.math.Vector2
import com.seija.math.Vector3

class ScreenScaler;

enum ScalerMode {
    case ConstantPixelSize
    case ScreenSizeMatchWH(designSize:Vector2,whRate:Float)
}

class ScreenScalerBuilder extends RawComponentBuilder {
    var camera:Option[Entity] = None;
    var mode:ScalerMode = ScalerMode.ConstantPixelSize
    override def build(entity: Entity): Unit = {
        mode match
            case ScalerMode.ConstantPixelSize => 
                FFISeija2D.addScreenScaler(App.worldPtr,entity,camera.get,0,Vector3.zero);
            case ScalerMode.ScreenSizeMatchWH(designSize, whRate) =>  
                FFISeija2D.addScreenScaler(App.worldPtr,entity,camera.get,1,new Vector3(designSize.x,designSize.y,whRate))
        
    }
}

object ScreenScaler {
    given ScreenScalerComponent:RawComponent[ScreenScaler] with  {
     type BuilderType = ScreenScalerBuilder;
     type RawType = Unit
     override def builder(): BuilderType = new ScreenScalerBuilder()

     override def getRaw(entity: Entity,isMut:Boolean): RawType = ()
   }
}

