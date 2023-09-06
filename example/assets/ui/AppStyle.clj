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
                        :CommonStates #(match %
                          "Normal"    (println 1)
                          "MouseOver" 2
                          "Pressed"   3)
                      }
                    </VisualStateDict>
                  </ControlTemplate.VSM>
                </ControlTemplate>
    }
  )
]