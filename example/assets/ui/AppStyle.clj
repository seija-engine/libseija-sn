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

  <DataTemplate dataType="java.lang.String">
    <Text height="22" fontSize="18" text="{Binding Data this}" />
  </DataTemplate>
]