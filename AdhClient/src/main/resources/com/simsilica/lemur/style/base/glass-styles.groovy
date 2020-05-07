package com.simsilica.lemur.style.base

import com.jme3.math.ColorRGBA
import com.simsilica.lemur.Button
import com.simsilica.lemur.Command
import com.simsilica.lemur.Insets3f
import com.simsilica.lemur.VAlignment
import com.simsilica.lemur.component.IconComponent
import com.simsilica.lemur.component.TbtQuadBackgroundComponent

def gradient = TbtQuadBackgroundComponent.create(
        texture( name:"/com/simsilica/lemur/icons/bordered-gradient.png",
                generateMips:false ),
        1, 1, 1, 126, 126,
        1f, false )

def pressedCommand = new Command<Button>() {
    public void execute( Button source ) {
        if( source.isPressed() ) {
            source.move(1, -1, 0);
        } else {
            source.move(-1, 1, 0);
        }
    }
};

def stdButtonCommands = [
        (Button.ButtonAction.Down):[pressedCommand],
        (Button.ButtonAction.Up)  :[pressedCommand]
]

// WINDOW

selector( "window-title-bar", "glass" ) {
    // background = new QuadBackgroundComponent(ColorUtils.fromHex("#365d8f"))
    background = gradient.clone()
    background.setColor(color(0.25, 0.5, 0.5, 1.0))
    //background.setMargin(2, 2)
}

selector( "window-title-label", "glass" ) {
    background = null
    textVAlignment = VAlignment.Center
    color = new ColorRGBA(0.8, 0.8, 0.8, 1.0)
    insets = new Insets3f( 5, 5, 5, 15 )
}

selector( "window-content-outer", "glass") {
    background = gradient.clone()
    background.setColor(color(0.25, 0.5, 0.5, 1.0))
    background.setMargin(5,5)
}

selector( "window-content-inner", "glass" ) {
    background = gradient.clone()
    background.setColor(color(0.25, 0.5, 0.5, 0.25))
    background.setMargin(5,5)
}

selector( "window-button-minimize", "glass" ) {
    background = null
    icon = new IconComponent("Style/Icon/button.png")
    icon.setColor(color(1.0, 0.7412, 0.298, 1.0))
    insets = new Insets3f(5,5,5,5)
    fontSize = 0
}

selector( "window-button-maximize", "glass" ) {
    background = null
    icon = new IconComponent("Style/Icon/button.png")
    icon.setColor(color(0.0, 0.7921, 0.3411, 1.0))
    insets = new Insets3f(5,0,5,5)
    fontSize = 0
}

selector( "window-button-close", "glass" ) {
    background = null
    icon = new IconComponent("Style/Icon/button.png")
    icon.setColor(color(1.0, 0.3882, 0.3568, 1.0))
    insets = new Insets3f(5,0,5,5)
    fontSize = 0
}

selector( "dialog-button", "glass") {
    background = gradient.clone()
    color = color(0.8, 0.9, 1, 0.85f)
    background.setColor(color(0, 0.75, 0.75, 0.5))
    background.setMargin(15, 5)

    highlightColor = ColorRGBA.Yellow.clone()
    focusColor = ColorRGBA.Green.clone()

    insets = new Insets3f( 2, 2, 2, 2 );

    buttonCommands = stdButtonCommands;
}
