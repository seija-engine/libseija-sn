[
  (style "Control"
    {
      :width "120"
      :height "50"
      :template <ControlTemplate>
                 <Panel>
                    <Image sprite="default.white" color="#eeeeee" />
                    <Text text="FUCK" />
                 </Panel>
                </ControlTemplate>

      :sprite #(setter % "BG" "default.button")
    }
  )
]