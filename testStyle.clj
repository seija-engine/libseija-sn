(style :key "SliderThumb" :for-type "Thumb"
    {
        :width 26
        :height 26
        :template (control-template
                    {:vsm 
                       {
                        :state-name "CommonStates"
                        :hover  { :sprite "default.scale-slider-hover" }
                        :active { :sprite "default.scale-slider-active" }
                        :normal {:sprite "default.scale-slider" }
                       }
                    }
                    [
                        (image {:name "BG" :sprite "default.scale-slider"})
                    ]
                  )
    }
)
<Style key="SliderThumb" forType="Thumb">
        <Setter key="width" value="26" />
        <Setter key="height" value="26" />
        <Setter key="template">
            <ControlTemplate>
                <Image Name="BG" sprite="default.scale-slider" />
                <ControlTemplate.visualStateGroups>
                    <VisualStateGroupList>
                        <VisualStateGroup name="CommonStates">
                            <VisualState name="Normal">
                                <Setter target="BG" key="sprite" value="default.scale-slider" />
                            </VisualState>
                            <VisualState name="MouseOver">
                                <Setter target="BG" key="sprite" value="default.scale-slider-hover" />
                            </VisualState>
                            <VisualState name="Pressed">
                                <Setter target="BG" key="sprite" value="default.scale-slider-active" />
                            </VisualState>
                        </VisualStateGroup>
                    </VisualStateGroupList>
                </ControlTemplate.visualStateGroups>
            </ControlTemplate>
        </Setter>
    </Style>