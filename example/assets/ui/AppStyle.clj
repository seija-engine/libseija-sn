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
                    <VisualStateDict>
                      {
                        :CommonStates 
                        {
                          :Normal    {:sprite (setter "BtnBG" "default.button")         }
                          :MouseOver {:sprite (setter "BtnBG" "default.button-hover")   }
                          :Pressed   {:sprite (setter "BtnBG" "default.button-active")  }
                        }
                      }
                    </VisualStateDict>
                  </ControlTemplate.VSM>
                </ControlTemplate>
    }
  )
]