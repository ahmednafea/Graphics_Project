import RenderEngine.DisplayManager;
import RenderEngine.Loader;
import RenderEngine.MasterRenderer;
import RenderEngine.OBJLoader;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;

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
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
        TerrainTexturePack texturePack=new TerrainTexturePack(backgroundTexture,rTexture,gTexture,bTexture);
        TerrainTexture blendMap=new TerrainTexture(loader.loadTexture("blendMap"));

        RawModel model = OBJLoader.loadObjModel("lowPolyTree", loader);
        TexturedModel staticModel = new TexturedModel(model,new ModelTexture(loader.loadTexture("lowPolyTree")));
        TexturedModel fern = new TexturedModel(OBJLoader.loadObjModel("fern", loader),new ModelTexture(loader.loadTexture("fern")));
        TexturedModel grass = new TexturedModel(OBJLoader.loadObjModel("fern",loader),new ModelTexture(loader.loadTexture("grassTexture")));
        TexturedModel flower = new TexturedModel(OBJLoader.loadObjModel("fern",loader),new ModelTexture(loader.loadTexture("flower")));
       // TexturedModel tree1 = new TexturedModel(OBJLoader.loadObjModel("tree", loader),new ModelTexture(loader.loadTexture("tree")));
        grass.getTexture().setHasTransparency(true);
        grass.getTexture().setUsingFakeLighting(true);
        flower.getTexture().setHasTransparency(true);
        flower.getTexture().setUsingFakeLighting(true);
        fern.getTexture().setHasTransparency(true);
        List<Terrain> terrains;
        terrains=new ArrayList<>();
        terrains.add(new Terrain(0,-1,loader,texturePack,blendMap,"hieghtmap"));
        terrains.add(new Terrain(-1,-1,loader,texturePack,blendMap,"hieghtmap"));

        List<Entity> entities = new ArrayList<Entity>();
        Random random = new Random(676452);
        for(int i=0;i<400;i++){
            if(i%2==0){
            float x,y,z;
            x=random.nextFloat() * 800 - 400;
            z=random.nextFloat() * -600;
            y=terrains.get(0).getHieghtofTerrain(x,z);
            entities.add(new Entity(grass, new Vector3f(x, y,z), 0, random.nextFloat()*360,
                    0,random.nextFloat()*0.1f+0.8f));
            }
            if(i%4==0) {
                float x,y,z;
                x=random.nextFloat() * 800 - 400;
                z=random.nextFloat() * -600;
                y=terrains.get(0).getHieghtofTerrain(x,z);
                entities.add(new Entity(fern, new Vector3f(x, y,z), 0, random.nextFloat()*360, 0, 0.9f));
            }
            if(i%7==0){
                float x,y,z;
                x=random.nextFloat() * 800 - 400;
                z=random.nextFloat() * -600;
                y=terrains.get(0).getHieghtofTerrain(x,z);
                entities.add(new Entity(flower, new Vector3f(x, y,z), 0, random.nextFloat()*360, 0, random.nextFloat()*0.1f+0.9f));

            }
            if(i%3==0){
                float x,y,z;
                x=random.nextFloat() * 800 - 400;
                z=random.nextFloat() * -600;
                y=terrains.get(0).getHieghtofTerrain(x,z);
                entities.add(new Entity(staticModel, new Vector3f(x, y,z), 0, 0, 0, random.nextFloat()*0.1f+0.8f));
            }
        }
        List<Light> lights =new ArrayList<>();
        lights.add(new Light(new Vector3f(10000,10000,-10000),new Vector3f(1.3f,1.3f,1.3f)));
        lights.add(new Light(new Vector3f(185,10,-293),new Vector3f(2,0,0),new Vector3f(1,0.01f,0.002f)));
        lights.add(new Light(new Vector3f(370,17,-300),new Vector3f(0,2,2),new Vector3f(1,0.01f,0.002f)));
        lights.add(new Light(new Vector3f(293,7,-305),new Vector3f(2,2,0),new Vector3f(1,0.01f,0.002f)));

        TexturedModel lamp =new TexturedModel(OBJLoader.loadObjModel("lamp", loader),new ModelTexture(loader.loadTexture("lamp")));
        lamp.getTexture().setUsingFakeLighting(true);

        float x,y,z;
        x=185;
        z=-293;
        y=terrains.get(0).getHieghtofTerrain(x,z);
        entities.add(new Entity(lamp,new Vector3f(x,y,z),0,0,0,1));
        x=370;
        z=-300;
        y=terrains.get(0).getHieghtofTerrain(x,z);
        entities.add(new Entity(lamp,new Vector3f(x,y,z),0,0,0,1));
        x=293;
        z=-305;
        y=terrains.get(0).getHieghtofTerrain(x,z);
        entities.add(new Entity(lamp,new Vector3f(x,y,z),0,0,0,1));

        MasterRenderer renderer = new MasterRenderer(loader);

        TexturedModel stanfordBunny= new TexturedModel(OBJLoader.loadObjModel("person",loader),new ModelTexture(loader.loadTexture("playerTexture")));

        Player player =new Player(stanfordBunny,new Vector3f(100,0,-50),0,200,0,1);

        Camera camera = new Camera(player);

        WaterFrameBuffers buffers=new WaterFrameBuffers();
        WaterShader waterShader=new WaterShader();
        WaterRenderer waterRenderer =new WaterRenderer(loader,waterShader,renderer.getProjectionMatrix(),buffers);
        List<WaterTile> waters=new ArrayList<>();
        x=0;
        z=60;
        waters.add(new WaterTile(x,z,0));
        x=100;
        z=60;
        waters.add(new WaterTile(x,z,0));
        x=200;
        z=60;
        waters.add(new WaterTile(x,z,0));

        while(!Display.isCloseRequested()){
            player.move(terrains.get(0));
            camera.move();
            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
            for (WaterTile water:waters) {
                //render reflection texture
                buffers.bindReflectionFrameBuffer();
                renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, 1, 0, -waters.get(0).getHeight()));
                //render refraction texture
                buffers.bindRefractionFrameBuffer();
                renderer.renderScene(entities, terrains, lights, camera, new Vector4f(0, -1, 0, waters.get(0).getHeight()));
            }
            //render to screen
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            buffers.unbindCurrentFrameBuffer();
            renderer.processEntity(player);
            renderer.renderScene(entities,terrains,lights,camera,new Vector4f(0,-1,0,15));
            waterRenderer.render(waters,camera);
            DisplayManager.updateDisplay();
        }
        buffers.cleanUp();
        waterShader.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();

    }


}