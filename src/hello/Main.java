package hello;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import de.lessvoid.nifty.Nifty;


/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    
    
    private final static int DIFFICULTY_MULTIPLIER = 10;
    private final static int MAX_GENERATED_ALLOWED = 80;
    private final static int REDUCTOR = 1;
    private int generatedMeteors = 0;
    private BulletAppState bulletAppState;
    private Node enemies;
    private float lastMeteorElapsedTime = 0.0f;
    private float meteorsSpawnInterval = 10.0f;
    private GameScreenController gameScreenController;
    private Nifty nifty;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    

    @Override
    public void simpleInitApp() {
        
        NiftyJmeDisplay niftyDisplay = NiftyJmeDisplay.newNiftyJmeDisplay(
            assetManager, inputManager, audioRenderer, guiViewPort);
        /** Create a new NiftyGUI object */
        nifty = niftyDisplay.getNifty();
        this.gameScreenController = new GameScreenController();
        /** Read your XML and initialize your custom ScreenController */
        nifty.fromXml("Interface/scoregui.xml", "gameScreen", this.gameScreenController);
        // nifty.fromXml("Interface/helloworld.xml", "start", new MySettingsScreen(data));
        // attach the Nifty display to the gui view port as a processor
        guiViewPort.addProcessor(niftyDisplay);
        
        flyCam.setEnabled(false);
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        bulletAppState.setDebugEnabled(true);
        
        configureInputs();
        
        Light amb=new AmbientLight(ColorRGBA.DarkGray);
        rootNode.addLight(amb);
        Light dir=new DirectionalLight(new Vector3f(-1f, -1f, 0f));
        rootNode.addLight(dir);
        Light dir2=new DirectionalLight(new Vector3f(1f, 1f, 0f),
                                    new ColorRGBA(0.1f,0.1f,0.1f,1f));
        rootNode.addLight(dir2);
        
        
        
        
        
        Spatial terrain=createTerrain();
        rootNode.attachChild(terrain);
        
        Spatial sky=createSky();
        rootNode.attachChild(sky);

        enemies=new Node("Enemies");
        rootNode.attachChild(enemies);

        
        Spatial dino=createDino();
        rootNode.attachChild(dino);
        
        
        
        Spatial meteor=createMeteor(new Vector3f(2.0f, 40.0f,-4.0f));
        enemies.attachChild(meteor);


        cam.setLocation(new Vector3f(0f,1f,4f));
    }
    
    private Geometry createBox(float size, ColorRGBA color) {
        Box b = new Box(size, size, size);
        Geometry geom = new Geometry("Box", b);
        geom.setMaterial(createMaterial(color));
        return geom;
    }
    
    private Geometry createSphere(float radius, ColorRGBA color) {
        Sphere s=new Sphere(16, 32, radius);
        s.setTextureMode(Sphere.TextureMode.Polar);
        Geometry geom = new Geometry("Sphere", s);
        geom.setMaterial(createMaterial(color));
        return geom;
    }
    
    private Material createMaterial(ColorRGBA color) {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setColor("Ambient", color);
        mat.setColor("Diffuse", color);
        //mat.setColor("Specular", ColorRGBA.White);
        //mat.setFloat("Shininess", 100f);
        mat.setBoolean("UseMaterialColors", true);
        return mat;
    }
    
    
    private Geometry createFloor() {
        Quad q=new Quad(100f,100f);
        q.scaleTextureCoordinates(new Vector2f(100f,100f));
        Geometry geom=new Geometry("Floor", q);
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture tex=assetManager.loadTexture("Textures/tiles.png");
        tex.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("DiffuseMap", tex);
        geom.setMaterial(mat);
        geom.rotate(-FastMath.PI*0.5f, 0f, 0f);
        geom.move(-50f,0f,50f);
        return geom;
    }
    
    private Spatial createTerrain() {
        Texture heightImage=
                assetManager.loadTexture("Textures/terrain/height_map.png");
        ImageBasedHeightMap hmap=new ImageBasedHeightMap(
                heightImage.getImage(),
                         0.15f);
        hmap.load();
        Material mat = new Material(assetManager,
            "Common/MatDefs/Terrain/Terrain.j3md");
        Texture splat=
                assetManager.loadTexture("Textures/terrain/splat_map.png");
        mat.setTexture("Alpha", splat);
        Texture grass=
                assetManager.loadTexture("Textures/terrain/grass.png");
        grass.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("Tex1", grass);
        mat.setFloat("Tex1Scale", 128.0f);
        
        
        Texture road=
                assetManager.loadTexture("Textures/terrain/road.png");
        road.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("Tex2", road);
        mat.setFloat("Tex2Scale", 128.0f);
        
        Texture dirt=
                assetManager.loadTexture("Textures/terrain/dirt.png");
        dirt.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("Tex3", dirt);
        mat.setFloat("Tex3Scale", 128.0f);
        
        final int IMAGE_SIZE=512, BLOCK_SIZE=64;
        
        TerrainQuad terrain=new TerrainQuad("Terrain",
                BLOCK_SIZE+1, IMAGE_SIZE+1,
                hmap.getHeightMap());
        
        terrain.setMaterial(mat);
        
        TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
        control.setLodCalculator( new DistanceLodCalculator(BLOCK_SIZE+1, 2.7f) ); 
        terrain.addControl(control);
        
        CollisionShape collisionShape=
                CollisionShapeFactory.createMeshShape(terrain);
        RigidBodyControl rbc=new RigidBodyControl(collisionShape, 0.0f);
        terrain.addControl(rbc);
        bulletAppState.getPhysicsSpace().add(rbc);
        
        terrain.setName("Terrain");
        return terrain;
    }
    
    private Spatial createSky() {
        Texture up=assetManager.loadTexture("Textures/sky_up.png");
        Texture down=assetManager.loadTexture("Textures/sky_down.png");
        Texture side=assetManager.loadTexture("Textures/sky_side.png");
    
        return SkyFactory.createSky(assetManager, side, side, side, side, up, down);
    }
    
    private Spatial createDino() {
        Spatial dino= assetManager.loadModel("Models/Dino.j3o");
        dino.scale(0.25f);
        Control c=new MotionControl(inputManager);
        dino.addControl(c);
        Control cc=new CamControl(cam, inputManager);
        dino.addControl(cc);
        Control jc = new JumpControl(inputManager, rootNode);
        dino.addControl(jc);
        DamageControl dc=new DamageControl(rootNode, gameScreenController);
        bulletAppState.getPhysicsSpace().addCollisionListener(dc);
        dino.addControl(dc);
        
        //Control hc=new TerrainHeightControl(rootNode, 0.6f);
        //dino.addControl(hc);
        
        dino.setName("Dino");
        
        CollisionShape collisionShape=
                CollisionShapeFactory.createDynamicMeshShape(dino);
        RigidBodyControl rbc=new RigidBodyControl(collisionShape, 1000.0f);
        dino.addControl(rbc);
        bulletAppState.getPhysicsSpace().add(rbc);
        
        System.out.println("Old friction:"+rbc.getFriction());
        rbc.setFriction(2.0f);
        rbc.setAngularDamping(0.5f);
        
        return dino;
    }
    
    private Spatial createMeteor(Vector3f pos) {
        Geometry sphere=createSphere(0.5f, new ColorRGBA(0.4f,0.4f,0.2f,1.0f));
        sphere.move(0f,0.5f,0f);
        Texture bumpMap=assetManager.loadTexture("Textures/bump_map.jpg");
        sphere.getMaterial().setTexture("NormalMap", bumpMap);
        
        
        //MeteorControl mc=new MeteorControl(rootNode);
        //sphere.addControl(mc);
        
        //TerrainHeightControl hc=new TerrainHeightControl(rootNode, 0.3f);
        //sphere.addControl(hc);
        
        CollisionShape collisionShape=
                CollisionShapeFactory.createDynamicMeshShape(sphere);
        RigidBodyControl rbc=new RigidBodyControl(collisionShape, 1000.0f);
        sphere.addControl(rbc);
        bulletAppState.getPhysicsSpace().add(rbc);
        rbc.setPhysicsLocation(pos);
        rbc.setLinearVelocity(new Vector3f(0f,-30f,0f));
        
        
        return sphere;
    }
    
    private void configureInputs() {
        inputManager.addMapping("Forward",
                new KeyTrigger(KeyInput.KEY_I),
                new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Backward",
                new KeyTrigger(KeyInput.KEY_K),
                new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("RotateLeft",
                new KeyTrigger(KeyInput.KEY_J),
                new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("RotateRight",
                new KeyTrigger(KeyInput.KEY_L),
                new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Jump", 
                new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("CamLeft",
                new KeyTrigger(KeyInput.KEY_LEFT),
                new MouseAxisTrigger(MouseInput.AXIS_X,true)
                );
        inputManager.addMapping("CamRight",
                new KeyTrigger(KeyInput.KEY_RIGHT),
                new MouseAxisTrigger(MouseInput.AXIS_X,false)
                );

    }
    
    private void updateMeteorStorm(float tpf){
        lastMeteorElapsedTime += tpf;

        // Verifica se è trascorso abbastanza tempo per generare un nuovo meteorite
        if (lastMeteorElapsedTime >= meteorsSpawnInterval && generatedMeteors <= MAX_GENERATED_ALLOWED) {
            // Genera un nuovo meteorite
            enemies.attachChild(createMeteor(getRandomSkyPosition(50, 150)));

            // Incrementa il numero di meteoriti generati finora
            generatedMeteors++;

            // Resetta il tempo trascorso
            lastMeteorElapsedTime = 0;
            this.gameScreenController.updateScore(generatedMeteors);
        }
        
    
    }

    private Vector3f getRandomSkyPosition(float minDistance, float maxDistance){
        float distance = FastMath.nextRandomFloat() * (maxDistance - minDistance) + minDistance;
        float azimuth = FastMath.nextRandomFloat() * FastMath.TWO_PI;
        float elevation = FastMath.nextRandomFloat() * FastMath.HALF_PI;

        float x = distance * FastMath.cos(azimuth) * FastMath.sin(elevation);
        float y = distance * FastMath.cos(elevation);
        float z = distance * FastMath.sin(azimuth) * FastMath.sin(elevation);

        return new Vector3f(x, y, z);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        updateMeteorStorm(tpf);
        gameScreenController.updateScore((int)tpf);
        nifty.update();
    }
}
