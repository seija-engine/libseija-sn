[
  (style "MenuItem" {
    :width 123 :height 456
    :template 
    <ControlTemplate>
        <#vsm>
        (fn [vmDict]
           (match (vmDict "Common")
              "Normal" #(log 123)
              "Hover"  #(log 456)
           )
        )
        </#vsm>

        <Panel>
            <Image sprite="default.white" color="#ffffff" />
            <Image sprite="default.checkbox" />
            <Text>hh</Text>
        </Panel>
        
    </ControlTemplate>

  })
]
(style :key "SliderThumb" :for-type "Thumb"
    {
        :width 26
        :height 26
        :template <ControlTemplate>
                     <Image Name="BG" sprite="default.scale-slider" />
                     <ControlTemplate.VSM>
                        (match [(% :common) (% :checked)]
                            ["Hover","Checked"]     #((set! "BG" :sprite "default.scale-slider-hover"))
                            ["Hover","Unchecked"]   #((set! "BG" :sprite "default.scale-slider-normal"))
                            ["Normal","Checked"]    #((set!   "BG" :sprite "default.scale-slider-normal"))
                            ["Normal","Unchecked"]  #((set! "BG" :sprite "default.scale-slider-normal"))
                        )
                     </ControlTemplate:VSM>
                  </ControlTemplate>
    }
)

(add-template "MenuHeader" "MenuItem"
    <ControlTemplate>
      
    </ControlTemplate>
)

(def checkTemplate 
    <ControlTemplate>
    </ControlTemplate>
)

(add-style "MyCheck" "CheckBox"
    {
        :width 120 :height 230
        :template @checkTemplate
    }
)

(def lst [1 2 3])
(def tmplId 1000)
<Root>
@(if (= tmplId  1)
  <List>@(map lst #(<ListItem index=@idx />))</List>
  <Single index=@tmplId />
)
</Root>