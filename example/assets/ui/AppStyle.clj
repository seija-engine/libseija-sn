(match ["3" "4"]
  ["1" "2"] (println "1,2")
  ["3" "4"] (do
          (println "3,4")
          (println "哈哈哈")
        )
  _ (println "empty")
)

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
                </ControlTemplate>
    }
  )
]