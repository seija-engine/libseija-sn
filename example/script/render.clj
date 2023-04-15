(require "all_uniform")

(defn init [set]
    (all_uniform/decl set)
)

(defcomp start []
    (uniform "ObjectBuffer")
    (uniform "CameraBuffer")
    (uniform "LightBuffer")
    ;(uniform "PostEffect")
    ;(uniform "IBLEnv")
    ;(uniform "UIAtlas")
    (node CameraNodeID "CameraBuffer")
    (node TransformNodeID "ObjectBuffer")
    (node PBRCameraExNodeID "CameraBuffer")
    (node PBRLightNodeID "LightBuffer")
    ;(node IBLNodeID "IBLEnv")
)

(defcomp foward-path [env]
  (let [depth-texture (texture {:format "Depth32Float" :width WINDOW_WIDTH :height WINDOW_HEIGHT}) 
        camera-id (env :camera-id)
        camera-query (env :camera-query)
        camera-target (env :path-target)
      ]
    (node WinResizeNodeID [depth-texture])
    (node DrawPassNodeID camera-query camera-id  [camera-target] depth-texture "Foward")
  )
)

(add-render-path "Foward" foward-path)