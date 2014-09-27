/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.font.BitmapText;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author Joanna
 */
public class CarbonCrystalControl extends AbstractControl {
    private Spatial player;
    private float playerX;
    private float playerY;
    private float playerZ;
    private float thisX;
    private float thisY;
    private float thisZ;
    private float distance;
    private boolean getBlue;
    private BitmapText tarAlertBlueText;
    
    public CarbonCrystalControl() {
        //default constructor
    }    
    
    public CarbonCrystalControl(Spatial p) {
        this.player = p;
    }
      
    @Override
    protected void controlUpdate(float tpf) {
        //throw new UnsupportedOperationException("Not supported yet.");
        //CollisionResults results = new CollisionResults();
        playerX = player.getWorldTranslation().getX();
        playerY = player.getWorldTranslation().getY();
        playerZ = player.getWorldTranslation().getZ();
        thisX = spatial.getWorldTranslation().getX();
        thisY = spatial.getWorldTranslation().getY();
        thisZ = spatial.getWorldTranslation().getZ();

        distance = (float)Math.sqrt((playerX - thisX) * (playerX - thisX) + 
                (playerZ - thisZ) * (playerZ - thisZ));
        spatial.rotate(0, tpf, 0);
        
        if(distance <= 0.5f ) {
//            setGetBlue(true);
            spatial.removeFromParent();
        }
        
           
    }
    
    public void setGetBlue(boolean gb) {
        this.getBlue = gb;
    }
    
    public boolean getGetBlue() {
        return getBlue;
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public Control cloneForSpatial(Spatial spatial) {
        //throw new UnsupportedOperationException("Not supported yet.");
        CarbonCrystalControl ccc = new CarbonCrystalControl();
        ccc.spatial = spatial;
        ccc.setEnabled(isEnabled());
        
        return ccc;
    }
}
