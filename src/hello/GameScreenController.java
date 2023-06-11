/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hello;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author cristian
 */
public class GameScreenController implements ScreenController {
    private int score = 0;
    
    private Nifty nifty;
    private Screen screen;
    
    public void setScore(int score) {
        this.score = score;
        updateElement();
    }
    
    private void updateElement(){
        this.screen.findElementById("scoreText").getRenderer(TextRenderer.class).setText("Score: " + this.score);
    }
    
    public void updateScore(int delta) {
        this.score += delta;
        updateElement();
    }
    
    public int getScore(){
        System.out.println("CIAOCIAO");
        return this.score;
    }

    // Altri metodi per gestire gli eventi della GUI

    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    @Override
    public void onStartScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void onEndScreen() {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
