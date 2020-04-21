/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package za.co.bruynhuis.findlight.ui;

import com.bruynhuis.galago.ui.TextAlign;
import com.bruynhuis.galago.ui.button.TouchButton;
import com.bruynhuis.galago.ui.effect.TouchEffect;
import com.bruynhuis.galago.ui.panel.Panel;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;

/**
 *
 * @author NideBruyn
 */
public class Button extends TouchButton {

    public Button(Panel panel, String id, String text) {
        super(panel, id, "Resources/blank.png", 256, 64, true);
        setFontSize(32);
        setText(text);
        setTextColor(ColorRGBA.LightGray);
        addEffect(new TouchEffect(this));
        setTextOffset(new Vector3f(0, 4, 0));
        setTextAlignment(TextAlign.LEFT);
    }

}
