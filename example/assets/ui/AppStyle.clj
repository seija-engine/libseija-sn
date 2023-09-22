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
      :template <ControlTemplate>
                  <Panel >
                    <Image sprite="default.white" color="#cccccc" />
                    <ItemsPresenter  />
                  </Panel>
                </ControlTemplate>
    }
  )

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
    :template
    <ControlTemplate>
                <Panel margin="0,1,0,1">
                    <Image sprite="default.white" color="#ffffff" />
                    <ContentPresenter contentSource="header" />
                    <Popup width="150" height="30" hor="Center" ver="Center" isOpen="{Binding Owner isSubmenuOpen}" mode="Bottom">
                        <Panel>
                            <Image sprite="default.white" color="#ffffff" />
                            <Image imageType="Slice" sprite="default.frame" color="#ffffff" />
                            
                            <Text  fontSize="16" text="新建项目" color="#000000" />
                        </Panel>
                    </Popup>
                </Panel>
    </ControlTemplate>
  })

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

  (style "ScrollBar" {
    :vsm <VisualStateList>
          [
            :OrientationStates {
                :Horizontal  {:height "26" }
                :Vertical    {:width "26" }
              }
          ]
         </VisualStateList>
  })

  <DataTemplate dataType="java.lang.String">
    <Text height="22" fontSize="18" text="{Binding Data this}" />
  </DataTemplate>
]