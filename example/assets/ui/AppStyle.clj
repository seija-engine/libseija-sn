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
                          :Normal    {:sprite "default.button" }
                          :MouseOver {:sprite "default.button-hover" }
                          :Pressed   {:sprite "default.button-active" }  
                        }
                      }
                    </VisualStateDict>
                  </ControlTemplate.VSM>
                </ControlTemplate>
    }
  )
]