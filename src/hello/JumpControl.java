/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JME3 Classes/Control.java to edit this template
 */
package hello;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import java.io.IOException;
import com.jme3.scene.Node;


/**
 *
 * @author cristian
 */
public class JumpControl extends AbstractControl implements ActionListener {
    //Any local variables should be encapsulated by getters/setters so they
    //appear in the SDK properties window and can be edited.
    //Right-click a local variable to encapsulate it with getters and setters.

    private boolean jump = false;
    private Vector3f utility = new Vector3f();
    
    public JumpControl(InputManager inputManager, Node rootNode) {
        inputManager.addListener(this, "Jump");
    }

    @Override
    protected void controlUpdate(float tpf) {
        //TODO: add code that controls Spatial,
        //e.g. spatial.rotate(tpf,tpf,tpf);
        if(jump){
            jump();
        }
    }

    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
        if(!isEnabled()) 
            return;
        
        if(name.equals("Jump"))
            jump = keyPressed;
            
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
    private void jump() {
        Spatial dino = getSpatial();
        RigidBodyControl rbc = dino.getControl(RigidBodyControl.class);
        
        rbc.applyImpulse(new Vector3f(0,300,0), Vector3f.ZERO);
    }
}
