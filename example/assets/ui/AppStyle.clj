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
        <StackPanel spacing="10"  isCanvas="true" isClip="true" />
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
    <Text height="22" fontSize="20" text="{Binding Data this}" />
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
    :itemsPanel 
      <ItemsPanelTemplate>
        <StackPanel orientation="Hor" />
      </ItemsPanelTemplate>
    :template <ControlTemplate>
                  <Panel>
                    <Image  sprite="default.white" />
                    <Image imageType="Slice" sprite="default.menu-border" />
                    <ItemsPresenter />
                  </Panel>
              </ControlTemplate>
  })

  (style "MenuItem" {
    :itemsPanel 
      <ItemsPanelTemplate>
        <StackPanel  />
      </ItemsPanelTemplate>
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
        <Image Name="BG" sprite="default.white" />
        <ContentPresenter contentSource="header" />
      </Panel>
      <ControlTemplate.vsm><VisualStateList>
                        [
                             :FocusStates {
                               :Focused     {:color  (setter "BG" "#a54358")   }
                               :Unfocused   {:color  (setter "BG" "#000000")   }
                             }
                        ]
      </VisualStateList></ControlTemplate.vsm>
    </ControlTemplate>
  })

  (style "ListBoxItem" {
    :ver "Center"
    :template <ControlTemplate>
                <Panel>
                  <Image Name="Border" color="#aaa" sprite="default.white" />
                  <Image margin="2,2,2,2" Name="BG" color="#aaa" sprite="default.white" />
                  <ContentPresenter />
                </Panel>
                <ControlTemplate.vsm><VisualStateList>
                [
                  :CommonStates {
                    :Normal     {:color (setter "BG" "#aaa")         }
                    :MouseOver  {:color (setter "BG" "#006611")      }
                  }

                  :SelectionStates {
                    :Selected  { :color (setter  "Border" "#0022ff") }
                    :Unselected { :color (setter "Border" "#aaa") }
                  }
                ]
                </VisualStateList></ControlTemplate.vsm>
              </ControlTemplate>
  })

  (style "ListBox" {
    :itemsPanel 
      <ItemsPanelTemplate>
        <StackPanel spacing="10" isCanvas="true"  />
      </ItemsPanelTemplate>
    
    :template <ControlTemplate>
                <Panel>
                  <Image color="#ddd" sprite="default.white" />
                  <ItemsPresenter  />
                </Panel>
              </ControlTemplate>
  })

  (style "TextBox" {
    :template <ControlTemplate>
                <Panel>
                  <Image Name="BG" sprite="default.entry" imageType="Slice" />
                  <InputText text="{Binding Owner text Type=Both}" margin="5,0,0,0" caretColor="#000"  />
                </Panel>
                <ControlTemplate.vsm><VisualStateList>
                [
                  :CommonStates {
                    :Normal  {:sprite  (setter  "BG" "default.entry")    }
                    :Pressed  {:sprite (setter "BG" "default.entry-active") }
                  }
                ]
                </VisualStateList></ControlTemplate.vsm>
              </ControlTemplate>
  })

  (style "ToggleButton" {
    :template <ControlTemplate>
                <Panel>
                  <Image Name="BG" />
                </Panel>
                <ControlTemplate.vsm>
                    <VisualStateList>
                      [
                        :CheckStates {
                          :Checked    {:sprite (setter "BG" "default.pan-down")    }
                          :Unchecked {:sprite (setter  "BG" "default.pan-right")   }
                        }
                      ]
                    </VisualStateList>
                  </ControlTemplate.vsm>
              </ControlTemplate>
  })

  (style "TreeView" {
    :itemsPanel 
      <ItemsPanelTemplate>
        <StackPanel isCanvas="true"  />
      </ItemsPanelTemplate>
    :template 
      <ControlTemplate>
        <Panel>
          <Image sprite="default.white" color="#ddd" />
          <ItemsPresenter />
        </Panel>
      </ControlTemplate>
  })

  (style "TreeViewItem" {
    :ver "Start"

    :itemsPanel 
      <ItemsPanelTemplate>
        <StackPanel  isCanvas="true"  />
      </ItemsPanelTemplate>
    :template 
      <ControlTemplate>
        <Panel>
          ;<Image sprite="default.white" color="#66666699" />
          <ToggleButton Name="TreeBtn" isChecked="{Binding Owner IsExpanded Type=Both}" ver="Start" hor="Start" margin="2,8,0,0" width="16" height="16" />
          <StackPanel padding="20,0,0,0">
            <Panel height="30" >
              <Image Name="Select" sprite="default.white" color="#0000ff66" margin="-2,0,0,0" />
              <ContentPresenter Name="PART_Header"  contentSource="header" height="30" >
               
              </ContentPresenter>
            </Panel>
            <ItemsPresenter  Name="TreeChild"  active="false" />
          </StackPanel>
        </Panel>
        <ControlTemplate.vsm>
          <VisualStateList>
            [
              :ExpansionStates {
                :Expanded    {:active (setter "TreeChild" true)    }
                :Collapsed   {:active (setter "TreeChild" false)   }
              }
              :HasItemsStates {
                :HasItems    {:active (setter "TreeBtn" true)    }
                :NoItems     {:active (setter "TreeBtn" false)   }
              }
              :CommonStates {
                :Normal    {:color (setter "Select" "#0000ff00")   }
                :MouseOver {:color (setter "Select" "#0000ff00")   }
                :Pressed   {:color (setter "Select" "#0000ff66")   }
              }
            ]
          </VisualStateList>
        </ControlTemplate.vsm>
      </ControlTemplate>
  })

  (style "ContextMenu" {
    :itemsPanel 
      <ItemsPanelTemplate>
        <StackPanel orientation="Ver" />
      </ItemsPanelTemplate>
    :template <ControlTemplate>
                <Panel  >
                  <Image  sprite="default.white"  />
                  <Image imageType="Slice"  sprite="default.menu-border" />
                  <ItemsPresenter />
                </Panel>
              </ControlTemplate>
  })

  (style {:type "MenuItem" :key "ctxMenuItem"} {
    
    :height "30"
    :template <ControlTemplate>
                <Panel width="120" height="30">
                  <ContentPresenter contentSource="header" />
                  <Image  width="120" imageType="Slice" sprite="default.frame" color="#ffffff" />
                </Panel>
              </ControlTemplate>
  })
]