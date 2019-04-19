import RenderEngine.DisplayManager;
import RenderEngine.Loader;
import RenderEngine.MasterRenderer;
import RenderEngine.OBJLoader;
import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        DisplayManager.createDisplay();
        Loader loader = new Loader();
        //Terrains
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        TerrainTexturePack texturePack=new TerrainTexturePack(backgroundTexture,rTexture,gTexture,bTexture);
        TerrainTexture blendMap=new TerrainTexture(loader.loadTexture("blendMap"));

        RawModel model = OBJLoader.loadObjModel("lowPolyTree", loader);
        TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("lowPolyTree")));
        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader),new ModelTexture(loader.loadTexture("fern")));
        TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("grassModel",loader),new ModelTexture(loader.loadTexture("grassTexture")));
        TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("grassModel",loader),new ModelTexture(loader.loadTexture("flower")));
        TexturedModel tree1 = new TexturedModel(OBJLoader.loadObjModel("tree", loader),new ModelTexture(loader.loadTexture("tree")));
        grass.getTexture().setHasTransparency(true);
        grass.getTexture().setUsingFakeLighting(true);
        flower.getTexture().setHasTransparency(true);
        flower.getTexture().setUsingFakeLighting(true);
        fern.getTexture().setHasTransparency(true);
        List<Entity> entities = new ArrayList<Entity>();
        Random random = new Random(676452);
        for(int i=0;i<400;i++){
            if(i%7==0) {
                entities.add(new Entity(grass, new Vector3f(random.nextFloat() * 400 - 200, 0, random.nextFloat() * -400), 0, 0, 0, 1.8f));
                entities.add(new Entity(flower, new Vector3f(random.nextFloat() * 400 - 200, 0, random.nextFloat() * -400), 0, 0, 0, 2.3f));
            }
            if(i%3==0){
            entities.add(new Entity(fern, new Vector3f(random.nextFloat() * 400 - 200, 0, random.nextFloat() * -400), 0, random.nextFloat()*360, 0, 0.9f));
            entities.add(new Entity(staticModel, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, random.nextFloat()*360, 0, random.nextFloat()*0.1f+0.4f));
            entities.add(new Entity(tree1, new Vector3f(random.nextFloat() * 800 - 400, 0, random.nextFloat() * -600), 0, 0, 0,random.nextFloat()*1+ 3));
            }
        }

        Light light = new Light(new Vector3f(20000,40000,2000),new Vector3f(1,1,1));
        Terrain terrain =new Terrain(0,-1,loader,texturePack,blendMap);
        Terrain terrain1 =new Terrain(-1,-1,loader,texturePack,blendMap);
        Camera camera = new Camera();
        MasterRenderer renderer = new MasterRenderer();

        while(!Display.isCloseRequested()){
            camera.move();

            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain1);
           /* renderer.processTerrain(terrain3);
            renderer.processTerrain(terrain4);
            renderer.processTerrain(terrain5);
            renderer.processTerrain(terrain6);
            */for(Entity entity:entities){
                renderer.processEntity(entity);
            }
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }

        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }


}