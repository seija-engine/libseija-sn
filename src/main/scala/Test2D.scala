import com.seija.core.IGameApp
import com.seija.core.Entity
import com.seija.transform.Transform
import com.seija.render.{Camera,given}
import com.seija.`2d`.{ScreenScaler,ScalerMode,Sprite2D,given}
import com.seija.math.Vector2
import com.seija.ui.Atlas
import com.seija.ui.core.Rect2D

class Test2D extends IGameApp {
    var mainCamera:Option[Entity] = None
    var root2D:Option[Entity] = None
    override def OnStart(): Unit = {
        val atlas = Atlas.load("default","ui/default.json").get

        val camera_entity = Entity.spawn().add[Transform]().add[Camera](c => c.sortType = 1).build()
        this.mainCamera = Some(camera_entity);
        val root2D = Entity.spawn()
                           .add[Transform](t => t.parent = Some(camera_entity))
                           .add[ScreenScaler](s => {
                                s.camera = Some(camera_entity)
                                s.mode = ScalerMode.ScreenSizeMatchWH(new Vector2(1024,768),1f)
                            })
                           .build();
        this.root2D = Some(root2D)
        
        val sprite = Entity.spawn().add[Transform](t => { t.position.z = -1f; t.parent = Some(root2D) })
                                   .add[Rect2D](r => { r.width = 100; r.height = 100; })
                                   .add[Sprite2D](s => {
                                        s.atlas = Some(atlas.sheet);
                                        s.spriteIndex = atlas.get("button").get.index
                                    })
                                   .build();
    }

    override def OnUpdate(): Unit = {
        
    }
}