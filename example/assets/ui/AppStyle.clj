[
  (style "ButtonBase"
    {
      :width "120"
      :height "35"
      :template <ControlTemplate>
                  <Panel>
                    <Image Name="BtnBG" imageType="Slice" sprite="default.button" />
                    <ContentPresenter />
                  </Panel>
                  <ControlTemplate.vsm>
                    <VisualStateList>
                      [
                        :CommonStates {
                          :Normal    {:sprite (setter "BtnBG" "default.button")         }
                          :MouseOver {:sprite (setter "BtnBG" "default.button-hover")   }
                          :Pressed   {:sprite (setter "BtnBG" "default.button-active")  }
                        }
                      ]
                    </VisualStateList>
                  </ControlTemplate.vsm>
                </ControlTemplate>
    }
  )

  (style "ContentControl"
    {
      :width "300"
      :height "80"
      :template <ControlTemplate>
                  <Panel>
                    <Image sprite="default.white" color="#eeeeee" />
                    <ContentPresenter />
                  </Panel>
                </ControlTemplate>
    }
  )

  (style "ItemsControl"
    {
      :itemsPanel 
      <ItemsPanelTemplate>
        <StackPanel spacing="10"/>
      </ItemsPanelTemplate>
      
      :template <ControlTemplate>
                  <Panel >
                    <Image sprite="default.white" color="#cccccc" />
                    <ItemsPresenter  />
                  </Panel>
                </ControlTemplate>
    }
  )

  (style {:type "ContentPresenter" :key "ItemsPresenterStyle"} {
    :height "30"
  })

  (style "ScrollViewer"
    {
      :template <ControlTemplate>
                  <Panel isCanvas="true" isClip="true" >
                      <Image sprite="default.white" color="#cccccc" />
                      <ScrollContentPresenter />
                    <ScrollBar active="{Binding Owner showVerBar}" 
                     orientation="Ver" hor="End"
                     barLength="{Binding Owner barHeight}"
                     maxValue="{Binding Owner scrollableHeight}" />
          
                    </Panel>
                </ControlTemplate>
    }
  )

  (style "CheckBox"
    {
      :template <ControlTemplate>
                  <StackPanel orientation="Hor"> 
                      <Image Name="IMG" width="16" height="16" sprite="default.checkbox-unchecked" />
                      <ContentPresenter />
                  </StackPanel>
                  <ControlTemplate.vsm>
                    <VisualStateList>
                      [
                        :CheckStates {
                          :Checked    {:sprite  (setter "IMG" "default.checkbox-checked")    }
                          :Unchecked  {:sprite  (setter "IMG" "default.checkbox-unchecked")   }
                        }
                      ]
                    </VisualStateList>
                  </ControlTemplate.vsm>
                </ControlTemplate>
    }
  )

  (style {:type "Thumb" :key "SliderThumb"} {
    :width "26"
    :height "26"
    :template <ControlTemplate>
                <Image Name="BG" sprite="default.scale-slider" />
                <ControlTemplate.vsm>
                <VisualStateList>
                  [
                    :CommonStates {
                      :Normal     {:sprite  (setter "BG" "default.scale-slider")    }
                      :MouseOver  {:sprite  (setter "BG" "default.scale-slider-hover")   }
                      :Pressed    {:sprite  (setter "BG" "default.scale-slider-active")   }
                    }
                  ]
                </VisualStateList>
              </ControlTemplate.vsm>
              </ControlTemplate>
  })
  
  (style {:type "Thumb" :key "ScrollBarVerThumb"} {
    :width "13"
    :template <ControlTemplate>
                <Image Name="BG" imageType="Slice" sprite="default.scrollbar-vert-slider" />
                <ControlTemplate.vsm>
                  <VisualStateList>
                  [
                    :CommonStates {
                      :Normal { :sprite  (setter "BG" "default.scrollbar-vert-slider")  }
                      :MouseOver { :sprite  (setter "BG" "default.scrollbar-vert-slider-hover")  }
                      :Pressed { :sprite  (setter "BG" "default.scrollbar-vert-slider-active")  }
                    }
                  ]
                  </VisualStateList>
                </ControlTemplate.vsm>
              </ControlTemplate>
  })

  (style {:type "Thumb" :key "ScrollBarHorThumb"} {
    :height "13"
    :template <ControlTemplate>
                <Image Name="BG" imageType="Slice" sprite="default.scrollbar-horz-slider" />
                <ControlTemplate.vsm>
                  <VisualStateList>
                  [
                    :CommonStates {
                      :Normal { :sprite  (setter "BG" "default.scrollbar-horz-slider")  }
                      :MouseOver { :sprite  (setter "BG" "default.scrollbar-horz-slider-hover")  }
                      :Pressed { :sprite  (setter "BG" "default.scrollbar-horz-slider-active")  }
                    }
                  ]
                  </VisualStateList>
                </ControlTemplate.vsm>
              </ControlTemplate>
  })
  
  <ControlTemplate key="HorScrollBar" forType="ScrollBar">
        <Panel isCanvas="true">
            <Image imageType="Slice" sprite="default.scrollbar-horz-trough" />
            <Track Name="PART_Track" orientation="Hor"
                   thumbSize="{Binding Owner barLength}"
                   minValue="{Binding Owner minValue}"
                   maxValue="{Binding Owner maxValue}"
                   value="{Binding Owner value}">
                <Track.thumb>
                    <Thumb style="{Res ScrollBarHorThumb}" />
                </Track.thumb>
            </Track>
        </Panel>
  </ControlTemplate>

  <ControlTemplate key="VerScrollBar" forType="ScrollBar">
        <Panel isCanvas="true">
            <Image imageType="Slice" sprite="default.scrollbar-vert-trough" />
            <Track Name="PART_Track" orientation="Ver"
                   thumbSize="{Binding Owner barLength}"
                   minValue="{Binding Owner minValue}"
                   maxValue="{Binding Owner maxValue}"
                   value="{Binding Owner value}">
                <Track.thumb>
                    <Thumb style="{Res ScrollBarVerThumb}" />
                </Track.thumb>
            </Track>
        </Panel>
  </ControlTemplate>

  (style "ScrollBar" {
    :vsm <VisualStateList>
          [
            :OrientationStates {
                :Horizontal  { :height "14" :template (res "HorScrollBar") }
                :Vertical    {  :width "14" :template (res "VerScrollBar") }
              }
          ]
         </VisualStateList>
  })

  <DataTemplate dataType="java.lang.String">
    <Text height="22" fontSize="18" text="{Binding Data this}" />
  </DataTemplate>

  <ControlTemplate key="HorSlider" forType="Slider">
        <Panel isCanvas="true">
            <Image imageType="Slice" sprite="default.scale-horz-trough" />
            <Track Name="PART_Track"
                   minValue="{Binding Owner minValue}"
                   maxValue="{Binding Owner maxValue}"
                   value="{Binding Owner value}">
                <Track.template>
                    <ControlTemplate>
                        <Image imageType="Slice" hor="Start" width="{Binding Owner fillLength}"
                            sprite="default.scale-horz-trough-active" />
                    </ControlTemplate>
                </Track.template>
                <Track.thumb>
                    <Thumb style="{Res SliderThumb}" />
                </Track.thumb>
            </Track>
        </Panel>
  </ControlTemplate>

  <ControlTemplate key="VerSlider" forType="Slider">
        <Panel isCanvas="true">
            <Image imageType="Slice" sprite="default.scale-vert-trough" />
            <Track Name="PART_Track" orientation="Ver"
                   minValue="{Binding Owner minValue}"
                   maxValue="{Binding Owner maxValue}"
                   value="{Binding Owner value}">
                <Track.template>
                    <ControlTemplate>
                        <Image imageType="Slice" ver="Start" height="{Binding Owner fillLength}"
                            sprite="default.scale-vert-trough-active" />
                    </ControlTemplate>
                </Track.template>
                <Track.thumb>
                    <Thumb style="{Res SliderThumb}" />
                </Track.thumb>
            </Track>
        </Panel>
  </ControlTemplate>

  (style "Slider" {
    :vsm <VisualStateList>
          [
            :OrientationStates {
                :Horizontal  { :height "26" :template (res "HorSlider") }
                :Vertical    {  :width "26" :template (res "VerSlider") }
              }
          ]
         </VisualStateList>
  })

  (style "Menu" {
    :template <ControlTemplate>
                  <Panel>
                    <Image  sprite="default.white" />
                    <Image imageType="Slice" sprite="default.menu-border" />
                    <ItemsPresenter />
                  </Panel>
              </ControlTemplate>
  })

  (style "MenuItem" {
    :triggers <TriggerList>
                ["role"
                    [
                      "TopLevelHeader" {:template (res "TopLevelHeaderTemplate") }
                      "TopLevelItem"   {:template (res "TopLevelItemTemplate")   }
                      "SubmenuHeader" {:template (res "SubmenuHeaderTemplate") }
                      "SubmenuItem" {:template (res "SubmenuItemTemplate") }
                    ]
                ]
              </TriggerList>
    
    
  })


  <ControlTemplate key="TopLevelHeaderTemplate" forType="MenuItem">
        <Panel margin="0,1,0,1">
            <Image Name="BG" sprite="default.white" color="#ffffff" />
            <ContentPresenter contentSource="header" />
            <Popup width="120" isOpen="{Binding Owner isSubmenuOpen}" mode="Bottom">
              <Panel  width="120">
                <Image  width="120" sprite="default.white" color="#ffffff" />
                <Image  width="120" imageType="Slice" sprite="default.frame" color="#ffffff" />
                <ItemsPresenter  width="120" />
              </Panel>
            </Popup>
        </Panel>
        <ControlTemplate.vsm>
            <VisualStateList forType="MenuItem">
               [
                  :CommonStates {
                     :Normal     {:sprite (setter "BG" "default.white")         }
                     :MouseOver  {:sprite (setter "BG" "default.menubar-item-active")   }
                  }
               ]
            </VisualStateList>
        </ControlTemplate.vsm>
  </ControlTemplate>

  <ControlTemplate key="SubmenuHeaderTemplate" forType="MenuItem">
        <Panel width="120" padding="1,1,1,1">
            <Image Name="BG" sprite="default.white" color="#ffffff" />
            <Image hor="End" sprite="default.menu-pan-right" width="16" height="16" />
            <ContentPresenter contentSource="header" />
            <Popup width="120" hor="Center" ver="Center" isOpen="{Binding Owner isSubmenuOpen}" mode="Right">
              <Panel>
                <Image sprite="default.white" color="#ffffff" />
                <Image imageType="Slice" sprite="default.frame" color="#ffffff" />
                <ItemsPresenter />
              </Panel>
            </Popup>
        </Panel>
        <ControlTemplate.vsm><VisualStateList forType="MenuItem">
            [
                 :CommonStates {
                   :Normal     {:sprite (setter "BG" "default.white")         }
                   :MouseOver  {:sprite (setter "BG" "default.menubar-item-active")   }
                 }
            ]
        </VisualStateList></ControlTemplate.vsm>
  </ControlTemplate>

  <ControlTemplate key="TopLevelItemTemplate" forType="MenuItem">
      <Panel width="120" padding="1,1,1,1">
        <Image Name="BG" sprite="default.white" color="#ffffff" />
        <ContentPresenter contentSource="header" />
      </Panel>
      <ControlTemplate.vsm><VisualStateList>
                  [
                       :CommonStates {
                         :Normal     {:sprite (setter "BG" "default.white")         }
                         :MouseOver  {:sprite (setter "BG" "default.menubar-item-active")   }
                       }
                  ]
      </VisualStateList></ControlTemplate.vsm>
  </ControlTemplate>

  <ControlTemplate key="SubmenuItemTemplate" forType="MenuItem">
      <Panel width="120" padding="1,1,1,1">
         <Image Name="BG" sprite="default.white" color="#ffffff" />
         <ContentPresenter contentSource="header" />
      </Panel>
      <ControlTemplate.vsm><VisualStateList>
                        [
                             :CommonStates {
                               :Normal     {:sprite (setter "BG" "default.white")         }
                               :MouseOver  {:sprite (setter "BG" "default.menubar-item-active")   }
                             }
                        ]
      </VisualStateList></ControlTemplate.vsm>
  </ControlTemplate>

  (style "TabItem" {
    :headerTemplate
    <DataTemplate>
      <Text color="#fff"  fontSize="26" text="{Binding Data this}" />
    </DataTemplate>
    :template
    <ControlTemplate>
      <Panel>
        <Image sprite="default.white" color="#000"  />
        <ContentPresenter contentSource="header" />
      </Panel>
    </ControlTemplate>
  })
]