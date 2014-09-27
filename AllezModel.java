package mygame;

/* Wrongdoer-AllezModel:  author: Xuechi Li
 *                        last edit: Nov 27, 2013
 *                        description: the main class used to implement
 *                                     the game. 
 */

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
 
/** Sample 3 - how to load an OBJ model, and OgreXML model, 
 * a material/texture, or text. */
public class AllezModel extends SimpleApplication 
        implements AnimEventListener {
    
    public static Spatial player;
    public static Spatial teammate;
    public static Spatial crystal;
    private Spatial tCrystal;  //time crystal-blue
    private Spatial cCrystal1;  //Carbon-brown
    private Spatial cCrystal2;
    private Spatial cCrystal3;
    private Spatial cCrystal4;
    private Spatial cCrystal5;
    //private static Spatial tartar = new Tartar(); //#
    
    private static boolean gameLose = false;
    
    private FollowPlayerControl fpControl;
    private FollowControl mControl;
    private TimeCrystalControl tcControl;
    private AnimControl control;
    private AnimControl t_control; //teammate's control
    private static final String ANI_IDLE = "Idle";
    private static final String ANI_WALK = "Walk";
    private AnimChannel channel;
    private AnimChannel t_channel;
    private static final String MAPPING_WALK = "walk";
    private static final String MAPPING_FORWARD = "walk forward";
    private static final String MAPPING_BACKWARD = "walk backward";
    private static final String MAPPING_LEFTWARD = "walk leftward";
    private static final String MAPPING_RIGHTWARD = "walk rightward";
    private static final String MAPPING_PINPOINT = "pinpoint";
    private int distance = 0;
    private int playerX;
    private int playerY;
    private int playerZ;
    private int teammateX;
    private int teammateY;
    private int teammateZ;
    private BitmapText distanceText;
    private BitmapText authorText;
    private BitmapText playerWorldTranslationText;
    private BitmapText teammateWorldTranslationText;
    private BitmapText pinpointText;
    private BitmapText timeCrystalText;
    private BitmapText tarAlertBlueText;
    private BitmapText tarGoodJobText;
    private BitmapText entropyText;
    private BitmapText tarAlertTimeText;
    private BitmapText loseText;
    private BitmapText winText;
    private static Trigger TRIGGER_FORWARD = 
            new KeyTrigger(KeyInput.KEY_UP);
    private static Trigger TRIGGER_BACKWARD =
            new KeyTrigger(KeyInput.KEY_DOWN);
    private static Trigger TRIGGER_LEFTWARD =
            new KeyTrigger(KeyInput.KEY_LEFT);
    private static Trigger TRIGGER_RIGHTWARD =
            new KeyTrigger(KeyInput.KEY_RIGHT);
    private static Trigger TRIGGER_PINPOINT =
            new KeyTrigger(KeyInput.KEY_P);
    private static Vector3f[] axes = new Vector3f[3];
    private Box mesh;
    private Box mesh2;
    private Box mesh3;
    private Box mesh4;
    private Box mesh5;
    private Box mesh6;
    private Box mesh7;
    private Box mesh8;
    private Box mesh9;
    private ColorRGBA ether = new ColorRGBA(34/255, 47/255, 66/255, 0.5f);
    private BulletAppState bulletAppState = new BulletAppState();
    private RigidBodyControl scenePhy = new RigidBodyControl(0f);
    private static int numOfTC = 0;
    private static float entropyNum = 0;
    private CarbonCrystalControl ccControl;
//    private static boolean tarAlertBlue = true;
 
    public static void main(String[] args) {
        AllezModel app = new AllezModel();
        app.start();
   
    }
 
    @Override
    public void simpleInitApp() {
        
        settings.setTitle("Wrongdoer");
        
        // Add a sound effect
        AudioNode eatCrystalAudio = new AudioNode(assetManager,
                "Sounds/100_marimba_loop.ogg");
        eatCrystalAudio.setVolume(5);
        
        // Add a terrain - currently unavailable
        Spatial terrain1 = 
                assetManager.loadModel("Scenes/wScene2.j3o");
//        rootNode.attachChild(terrain1);
        
        // Add an ambient light
        AmbientLight ambient = new AmbientLight();
        rootNode.addLight(ambient);
        viewPort.setBackgroundColor(ether);
        
        //load H2O model
        //Spatial H2OModel = assetManager.loadModel("Models/H2O/H2O.j3o");
        
        stateManager.attach(bulletAppState);
        terrain1.addControl(scenePhy);
        bulletAppState.getPhysicsSpace().add(terrain1);
        bulletAppState.getPhysicsSpace().add(scenePhy);
        rootNode.attachChild(terrain1);
        
        flyCam.setDragToRotate(true);
        
        Vector3f camPos = new Vector3f(0, 3.43f, 10.17f);
        Vector3f camWorld = new Vector3f(0, 1, 0);
        cam.setLocation(camPos);
        cam.lookAt(camPos, camWorld);
        Vector3f camDir = new Vector3f(0, -0.17f, -0.98f);
        cam.lookAtDirection(camDir, camWorld);
//        Quaternion roll045 = new Quaternion();
//        roll045.fromAngleAxis( 1*FastMath.DEG_TO_RAD, Vector3f.UNIT_Y);
//        cam.setRotation(roll045);
        
        inputManager.setCursorVisible(true);
        inputManager.addMapping(MAPPING_FORWARD, TRIGGER_FORWARD);
        inputManager.addMapping(MAPPING_BACKWARD, TRIGGER_BACKWARD);
        inputManager.addMapping(MAPPING_LEFTWARD, TRIGGER_LEFTWARD);
        inputManager.addMapping(MAPPING_RIGHTWARD, TRIGGER_RIGHTWARD);
        inputManager.addMapping(MAPPING_PINPOINT, TRIGGER_PINPOINT);
        inputManager.addListener(analogListenerForward, 
                new String[] {MAPPING_FORWARD});
        inputManager.addListener(analogListenerBackward,
                new String[] {MAPPING_BACKWARD});
        inputManager.addListener(analogListenerLeftward,
                new String[] {MAPPING_LEFTWARD});
        inputManager.addListener(analogListenerRightward,
                new String[] {MAPPING_RIGHTWARD});
        inputManager.addListener(analogListenerPinpoint,
                new String[] {MAPPING_RIGHTWARD});
        
        
        //Spatial terrainGeo =
        //assetManager.loadModel("Scenes/myTerrain.j3o");
        //rootNode.attachChild(terrainGeo);
        
        player = (Node) assetManager.loadModel("Models/monkeyExport/Jaime.j3o");
        player.scale(0.5f);
        player.rotate(0, FastMath.DEG_TO_RAD * 180, 0);
        rootNode.attachChild(player);
        
        teammate = assetManager.loadModel("Models/monkeyExport/Jaime.j3o");
        //add variables to teammate
        //teammate.attachChild(tartar);
        teammate.setLocalTranslation(new Vector3f(2, 0, 0));
        teammate.setUserData("name", "Tartar");
        rootNode.attachChild(teammate);
        
        //display H2O model
        //H2OModel.setLocalTranslation(new Vector3f(5, 0, 0));
        //rootNode.attachChild(H2OModel);
        
        Spatial woodBox = assetManager.loadModel("Models/Woodbox/woodbox.j3o");
        woodBox.setLocalTranslation(new Vector3f(-9, 1, 1));
        rootNode.attachChild(woodBox);
        
//        crystal = assetManager.loadModel("Models/broccoli/broccoli/broccoli.obj");
//        crystal.setLocalTranslation(new Vector3f(4, 0, 0));
//        crystal.setUserData("name", "Time Crystal");
//        rootNode.attachChild(crystal);
        
        mesh = new Box(new Vector3f(4, 1, 0), 1, 1, 1);
        tCrystal = new Geometry("Time Crystal", mesh);
        Material mat = new Material(assetManager, 
                "Common/MatDefs/Misc/Unshaded.j3md");
        //Material matS = new Material(assetManager,
        //        "Common/MatDefs/Misc/solidColor.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        mat.setColor("Color", ColorRGBA.randomColor());
        tCrystal.setMaterial(mat);
        //tCrystal.setMaterial(matS);
        tCrystal.setLocalTranslation(new Vector3f(4, 0, 0));
        tCrystal.scale(0.1f);
        rootNode.attachChild(tCrystal);
        
        mesh2 = new Box(new Vector3f(8, 0, 0), 1, 1, 1 );
        Material cMat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        cMat.setColor("Color", ColorRGBA.Brown);
        cCrystal1 = new Geometry("Carbon", mesh2);
        cCrystal1.setMaterial(cMat);
        cCrystal1.setLocalTranslation(new Vector3f(8, 0, 0));
        cCrystal1.scale(0.1f);
        cCrystal1.addControl(new CarbonCrystalControl(player));
        rootNode.attachChild(cCrystal1);
        
        mesh3 = new Box(new Vector3f(10, 0, -3), 1, 1, 1);       
        cCrystal2 = new Geometry("Carbon", mesh3);
        cCrystal2.setMaterial(cMat);
        cCrystal2.setLocalTranslation(new Vector3f(10, 0, -3));
        cCrystal2.scale(0.1f);
        cCrystal2.addControl(new CarbonCrystalControl(player));
        rootNode.attachChild(cCrystal2);
        
        mesh4 = new Box(new Vector3f(-2, 0, 12), 1, 1, 1);       
        cCrystal3 = new Geometry("Carbon", mesh4);
        cCrystal3.setMaterial(cMat);
        cCrystal3.setLocalTranslation(new Vector3f(-2, 0, 12));
        cCrystal3.scale(0.1f);
        cCrystal3.addControl(new CarbonCrystalControl(player));
        rootNode.attachChild(cCrystal3);
        //rootNode.attachChild(cCrystal1);
//        CarbonCrystalControl ccControl = new CarbonCrystalControl(player);
//        cCrystal1.addControl(ccControl);
        
        tCrystal.addControl(new TimeCrystalControl(player, eatCrystalAudio));
            
        control = player.getControl(AnimControl.class);
        t_control = teammate.getControl(AnimControl.class);
        control.addListener(this);
      
        teammate.addControl(new FollowControl(player));
        
        player.addControl(new CameraControl(cam));
        
        //the teammate's control doesn't need to listen to the user
        //the teammate is almost completely out of control
        
        inputManager.addMapping(MAPPING_WALK, new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(analogListener, MAPPING_WALK);
        inputManager.addListener(actionListener, MAPPING_FORWARD, 
                MAPPING_BACKWARD, MAPPING_LEFTWARD, MAPPING_RIGHTWARD);
        
        //for (String anim : control.getAnimationNames())
        //    { System.out.println(anim); }
 
        channel = control.createChannel();
        channel.setAnim(ANI_IDLE);
        channel.setSpeed(2f);
        t_channel = t_control.createChannel();
        t_channel.setAnim(ANI_IDLE);
        t_channel.setSpeed(2f);
        
        //Spatial teapot = assetManager.loadModel("Models/Teapot/Teapot.obj");
        //Material mat_default = new Material( 
        //    assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        //teapot.setMaterial(mat_default);
        //rootNode.attachChild(teapot);
 
        // Create a wall with a simple texture from test_data
        //Box box = new Box(Vector3f.ZERO, 2.5f,2.5f,1.0f);
        //Spatial wall = new Geometry("Box", box );
        //Material mat_brick = new Material( 
        //    assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat_brick.setTexture("ColorMap", 
        //    assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg"));
        //wall.setMaterial(mat_brick);
        //wall.setLocalTranslation(2.0f,-2.5f,0.0f);
        //rootNode.attachChild(wall);
 
        // Display a line of text with a default font
        
        setDisplayStatView(false);
        setDisplayFps(false);
        
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        authorText = new BitmapText(guiFont);
        authorText.setSize(guiFont.getCharSet().getRenderedSize());
        authorText.move(
        (settings.getWidth())/2, // X
        authorText.getLineHeight(), // Y
        0); // Z (depth layer)
        authorText.setColor(ColorRGBA.Black);        
        guiNode.attachChild(authorText);
        
        playerWorldTranslationText = new BitmapText(guiFont);
        playerWorldTranslationText.setSize(guiFont.getCharSet().getRenderedSize());
        playerWorldTranslationText.move(
                0, 
                settings.getHeight(), 
                0);
        guiNode.attachChild(playerWorldTranslationText);
        
        teammateWorldTranslationText = new BitmapText(guiFont);
        teammateWorldTranslationText.setSize(guiFont.getCharSet().getRenderedSize());
        teammateWorldTranslationText.move(
                settings.getWidth() / 5 * 3,
                settings.getHeight(),
                0);
        guiNode.attachChild(teammateWorldTranslationText);
        
        timeCrystalText = new BitmapText(guiFont);
        timeCrystalText.setSize(guiFont.getCharSet().getRenderedSize());
        timeCrystalText.move(
                0,
                settings.getHeight() - playerWorldTranslationText.getHeight(),
                0);
        guiNode.attachChild(timeCrystalText);
        
        tarAlertBlueText = new BitmapText(guiFont);
        tarAlertBlueText.setSize(guiFont.getCharSet().getRenderedSize());
        tarAlertBlueText.move(
                settings.getWidth()/2,
                settings.getHeight()/2,
                0);
        tarAlertBlueText.setColor(ColorRGBA.Black);
        //tarAlertBlueText.setShadowMode(RenderQueue.ShadowMode.Cast);
        guiNode.attachChild(tarAlertBlueText);
        
        tarGoodJobText = new BitmapText(guiFont);
        tarGoodJobText.setSize(guiFont.getCharSet().getRenderedSize());
        tarGoodJobText.move(
                settings.getWidth()/2,
                settings.getHeight()/2,
                0);
        tarGoodJobText.setColor(ColorRGBA.Blue);
        guiNode.attachChild(tarGoodJobText);
        
        entropyText = new BitmapText(guiFont);
        entropyText.setSize(guiFont.getCharSet().getRenderedSize());
        entropyText.move(
                0,
                settings.getHeight() - playerWorldTranslationText.getHeight(),
                0);
        //entrophyText.setColor(ColorRGBA.White);
        guiNode.attachChild(entropyText);
        
        tarAlertTimeText = new BitmapText(guiFont);
        tarAlertTimeText.setSize(guiFont.getCharSet().getRenderedSize());
        tarAlertTimeText.move(
                settings.getWidth()/2,
                settings.getHeight()/2,
                0);
        tarAlertTimeText.setColor(ColorRGBA.Black);
        guiNode.attachChild(tarAlertTimeText);
        
        loseText = new BitmapText(guiFont);
        loseText.setSize(guiFont.getCharSet().getRenderedSize());
        loseText.move(
                settings.getWidth()/2,
                settings.getHeight()/2,
                0);
        loseText.setColor(ColorRGBA.Black);
        guiNode.attachChild(loseText);
        
        winText = new BitmapText(guiFont);
        winText.setSize(guiFont.getCharSet().getRenderedSize());
        winText.move(
                settings.getWidth()/2,
                settings.getHeight()/2,
                0);
        winText.setColor(ColorRGBA.Black);
        guiNode.attachChild(winText);
        
        //guiNode.detachAllChildren();
        //guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        //BitmapText helloText = new BitmapText(guiFont, false);
        //helloText.setSize(guiFont.getCharSet().getRenderedSize());
        //helloText.setText("Xuechi");
        //helloText.setLocalTranslation(300, helloText.getLineHeight(), 0);
        //guiNode.attachChild(helloText);
 
        // Load a model from test_data (OgreXML + material + texture)
        //Spatial ninja = assetManager.loadModel("Models/Sinbad/Sinbad.mesh.xml");
        //ninja.scale(1f, 1f, 1f);
        //ninja.rotate(0.0f, -3.0f, 0.0f);
        //ninja.setLocalTranslation(0.0f, -5.0f, -2.0f);
        //rootNode.attachChild(ninja);
        // You must add a light to make the model visible
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        rootNode.addLight(sun);
        
        DirectionalLight sun1 = new DirectionalLight();
        sun1.setDirection(new Vector3f(-0.39f, -0.32f, -0.74f));
        rootNode.addLight(sun1);
 
    }
    
    // try use KEY_UP,DOWN,LEFT,RIGHT to make jaime move
    private AnalogListener analogListenerForward = new AnalogListener() {
        public void onAnalog(String name, float intensity, float tpf) {
            if(name.equals(MAPPING_FORWARD)){
                // the direction of facing
                
                axes[0] = new Vector3f(0, 0, 0); //left
                axes[1] = new Vector3f(0, 0.5f, 0.5f); //up
                axes[2] = new Vector3f(0, 0.5f, 0.5f); //dir

                player.getLocalRotation().fromAxes(axes);
                player.move(0, 0, -tpf);
            }
        }
    };
    
    // move the player backward (towards the camera)
    private AnalogListener analogListenerBackward = new AnalogListener() {
        public void onAnalog(String name, float intensity, float tpf) {
            if(name.equals(MAPPING_BACKWARD)){
                // interesting !!! ---- can actually try to design a daoguajingou
                // walking on the ceiling, with head downward.
                // set y to minus  ('w')
                // II set axes[1] and axes[2] to (0,0.5,-0.5)
                // the player can lay down!!! ('`W`')
                // the direction of facing
                axes[0] = new Vector3f(0, 0, 0); //left
                axes[1] = new Vector3f(0, 0, 0); //up, when the z number is
                // negative, it decides the degree of the monkey raising up
                // its head
                axes[2] = new Vector3f(0, 0, -0.5f); //dir

                player.getLocalRotation().fromAxes(axes);
                player.move(0, 0, tpf);
            }
        }
    };
    
    // move the player leftward 
    private AnalogListener analogListenerLeftward = new AnalogListener() {
        public void onAnalog(String name, float intensity, float tpf) {
            // when axes[0] - (-1, 0, 0)
            //      axes[1] - (0, 0, 0)
            //      axes[2] - (1, 0, 0)
            // the player walks on the ceiling~
            if(name.equals(MAPPING_LEFTWARD)){
                axes[0] = new Vector3f(1, 0, 0); //left
                axes[1] = new Vector3f(0, 0, 0); //up
                axes[2] = new Vector3f(3, 0, 0); //dir
                player.getLocalRotation().fromAxes(axes);
                player.move(-tpf, 0, 0);
            }
        }       
    };
    
    private AnalogListener analogListenerRightward = new AnalogListener() {
        public void onAnalog(String name, float intensity, float tpf) {
            if(name.equals(MAPPING_RIGHTWARD)){
                axes[0] = new Vector3f(1, 0, 0); // if the number is of
                // an opposite sign of the number in axes[2], they can be
                // compensated for each other
                axes[1] = new Vector3f(0, 0, 0);
                axes[2] = new Vector3f(-3, 0, 0);  // the larger the number is,
                // the direction can be more different
                player.getLocalRotation().fromAxes(axes);
                player.move(tpf, 0, 0);
            }
        }
    };
    
    private AnalogListener analogListenerPinpoint = new AnalogListener() {
        public void onAnalog(String name, float intensity, float tpf) {
            if(name.equals(MAPPING_PINPOINT)) {
                //pinpoint to the player's position
                cam.setLocation(player.getWorldTranslation());
            }
        }
    };
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            if(name.equals(MAPPING_FORWARD)||
               name.equals(MAPPING_BACKWARD)||
               name.equals(MAPPING_LEFTWARD)||
               name.equals(MAPPING_RIGHTWARD)){
                if(!channel.getAnimationName().equals(ANI_WALK)){
                    channel.setAnim(ANI_WALK);
                }
                if(name.equals(MAPPING_FORWARD)||
                   name.equals(MAPPING_BACKWARD)||
                   name.equals(MAPPING_LEFTWARD)||
                   name.equals(MAPPING_RIGHTWARD)) {
                    channel.setAnim(ANI_IDLE);
                }
            }
        }
    };
    
    private AnalogListener analogListener = new AnalogListener() {
      public void onAnalog(String name, float intensity, float tpf) {
          if(name.equals(MAPPING_WALK)){
              player.move(0, 0, tpf);
              
          }
      }  
    };
    
    public void onAnimChange(AnimControl control, 
            AnimChannel channel, String animName) {
        if(animName.equals(ANI_WALK)) {
            //System.out.println(control.getSpatial().getName() 
            //        + " started walking.");
        }else if(animName.equals(ANI_IDLE)) {
            //System.out.println(control.getSpatial().getName() 
            //        + " started being idle.");
        }
    }
    
    public void onAnimCycleDone(AnimControl control, 
            AnimChannel channel, String animName) {
        if(animName.equals(ANI_WALK)) {
            //System.out.println(control.getSpatial().getName() 
            //        + " completed one walk loop.");
        }else if(animName.equals(ANI_IDLE)) {
            //System.out.println(control.getSpatial().getName() 
            //        + " completed one idle loop");
        }
    }
    
    public void simpleUpdate(float tpf) {
        //distance = (int)Vector3f.ZERO.distance(cam.getLocation());
        //distanceText.setText("Distance: "+distance);
       
//        boolean tarAlertBlue = true;
        
        authorText.setText("Wrongdoer | Xuechi Li");
        playerX = (int)player.getWorldTranslation().getX();
        playerY = (int)player.getWorldTranslation().getY();
        playerZ = (int)player.getWorldTranslation().getZ();
        playerWorldTranslationText.setText(
                "Player's position: (" + 
                playerX + ", " + 
                playerY + ", " + 
                playerZ + ")");
        teammateX = (int)teammate.getWorldTranslation().getX();
        teammateY = (int)teammate.getWorldTranslation().getY();
        teammateZ = (int)teammate.getWorldTranslation().getZ();
        teammateWorldTranslationText.setText(
                "Teammate's position: (" + 
                teammateX + ", " + 
                teammateY + ", " + 
                teammateZ + ")");
        entropyNum += 0.1;
        entropyText.setText("Entropy: " + (int)entropyNum + "/1000");
        
        //when the entrophy hits 1000, game over.
        
//        if(rootNode.hasChild(tCrystal)) {
//            tarAlertBlueText.setText("Aneth, go get the blue crystal!");
//            //tarAlertBlue = false;
//        }else if(rootNode.hasChild(cCrystal1)){
//            tarAlertBlueText.setText("");
//            tarGoodJobText.setText("Good job! Now go get the brown one.");
//            
//            //CarbonCrystalControl ccControl = new CarbonCrystalControl(player);
//            
//            //            cCrystal1.addControl(ccControl);
////            rootNode.attachChild(cCrystal2);
////            CarbonCrystalControl ccControl2 = new CarbonCrystalControl(player);
////            cCrystal2.addControl(ccControl2);
//        }
//        else {
//            tarAlertBlueText.setText("");
//            tarGoodJobText.setText("");
//            //rootNode.attachChild(cCrystal1);
//        }
//        
//        if(!rootNode.hasChild(tCrystal) &&
//                !rootNode.hasChild(cCrystal1)) {
//            tarGoodJobText.setText("");
//        }
        
        if(entropyNum >= 1000 && entropyNum <= 1100) {
            gameLose = true;
            loseText.setText("You Lose");
        }
        if(entropyNum > 1100) {
            stop();
        }
        if(entropyNum >= 600 && entropyNum <= 700) {
            tarGoodJobText.setText("");
            tarAlertTimeText.setText("Quickly! Time is running short.");
            entropyText.setColor(ColorRGBA.Red);
        } else {
            tarAlertTimeText.setText("");
        }
        
        
//        tarAlertBlue = false;
//        timeCrystalText.setText(
//                "Time Crystal: " + getTCNum());
        //System.out.println(cam.getLocation());
        //teammate.move(-tpf, 0, 0);
        
        /*影分身术:
        int moveChoice = (int) (Math.random()*4 + 1);
        switch (moveChoice) {
            case 1://move forward
                teammate.move(0, 0, -1);
                break;
            case 2://move backward
                teammate.move(0, 0, 1);
                break;
            case 3://move leftward
                teammate.move(-1, 0, 0);
                break;
            case 4://move rightward
                teammate.move(1, 0, 0);
                break;
        }
        **/
        
        /* problematic method:
        int tMove = (int)(Math.random()*4 + 1);
        switch(tMove) {
            case 1://move forward
                t_channel.setAnim(ANI_WALK);
                //axes[0] = new Vector3f(0, 0, 0); //left
                //axes[1] = new Vector3f(0, 0.5f, 0.5f); //up
                //axes[2] = new Vector3f(0, 0.5f, 0.5f); //dir

                //teammate.getLocalRotation().fromAxes(axes);
                teammate.move(0, 0, -tpf);
                
                break;
            case 2://move backward
                t_channel.setAnim(ANI_WALK);
                //axes[0] = new Vector3f(0, 0, 0); //left
                //axes[1] = new Vector3f(0, 0, 0); //up,
                //axes[2] = new Vector3f(0, 0, -0.5f); //dir

                //teammate.getLocalRotation().fromAxes(axes);
                teammate.move(0, 0, tpf);
                
                break;
            case 3://move leftward
                t_channel.setAnim(ANI_WALK);
                //axes[0] = new Vector3f(1, 0, 0); //left
                //axes[1] = new Vector3f(0, 0, 0); //up
                //axes[2] = new Vector3f(3, 0, 0); //dir
                //teammate.getLocalRotation().fromAxes(axes);
                teammate.move(-tpf, 0, 0);
                
                break;
                //hint: the teammate may control (actually the system is doing
                //the control) the player to freeze or something
            case 4://move rightward
                t_channel.setAnim(ANI_WALK);
                //axes[0] = new Vector3f(1, 0, 0); 
                //axes[1] = new Vector3f(0, 0, 0);
                //axes[2] = new Vector3f(-3, 0, 0);  
                
                //teammate.getLocalRotation().fromAxes(axes);
                teammate.move(tpf, 0, 0);
                
                break;
        }
        * */
    }
}
