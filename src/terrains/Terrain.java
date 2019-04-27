package terrains;

import RenderEngine.Loader;
import models.RawModel;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import toolbox.Maths;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Terrain {
	
	private static final float SIZE = 800;
	private static final float MAX_HIEGHT =40;
	private static final float MAX_PIXEL_COLOUR =256*256*256;


	private float x;
	private float z;
	private RawModel model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;

	private float [][] hieghts;

	public Terrain(int gridX, int gridZ, Loader loader,
				   TerrainTexturePack texturePack,TerrainTexture blendMap,String heightMap){
		this.texturePack = texturePack;
		this.blendMap= blendMap;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.model = generateTerrain(loader,heightMap);
	}
	public float getHieghtofTerrain(float worldx,float worldz){
		float terrainX=worldx - this.x;
		float terrainZ=worldz - this.z;
		float gridSquareSize=SIZE/((float)hieghts.length-1);
		int gridX = (int)Math.floor(terrainX/gridSquareSize);
		int gridZ = (int)Math.floor(terrainZ/gridSquareSize);
		if(gridX>=hieghts.length-1||gridZ>=hieghts.length-1||gridX<0||gridZ<0){
			return 0;
		}
		float xCoord =(terrainX%gridSquareSize)/gridSquareSize;
		float zCoord =(terrainZ%gridSquareSize)/gridSquareSize;
		float answer;
		if (xCoord <=(1-zCoord)){
			answer= Maths.barryCentric(new Vector3f(0,hieghts[gridX][gridZ],0),new Vector3f(1,hieghts[gridX+1][gridZ],0),new Vector3f(0,hieghts[gridX][gridZ+1],1),new Vector2f(xCoord,zCoord));
		}else {
			answer= Maths.barryCentric(new Vector3f(1,hieghts[gridX+1][gridZ],0),
					new Vector3f(1,hieghts[gridX+1][gridZ+1],1),
					new Vector3f(0,hieghts[gridX][gridZ+1],1),
					new Vector2f(xCoord,zCoord));
		}
		return answer;

	}

    public float getX() {
		return x;
	}



	public float getZ() {
		return z;
	}



	public RawModel getModel() {
		return model;
	}



	public TerrainTexturePack getTexture() {
		return texturePack;
	}

	private RawModel generateTerrain(Loader loader,String hieghtMap){
		BufferedImage image =null;
		try {
			image= ImageIO.read(new File("res/"+hieghtMap+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int VERTEX_COUNT=image.getHeight();
		hieghts=new float[VERTEX_COUNT][VERTEX_COUNT];
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
				float hieght= getHieght(j,i,image);
				hieghts[j][i]=hieght;
				vertices[vertexPointer*3+1] = hieght;
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				Vector3f normal=calculateNormal(j,i,image);
				normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}

	public TerrainTexture getBlendMap() {
		return blendMap;
	}

	private Vector3f calculateNormal(int x ,int y,BufferedImage image){
		float heightL =getHieght(x-1,y,image);
		float heightR =getHieght(x+1,y,image);
		float heightD =getHieght(x,y-1,image);
		float heightU =getHieght(x,y+1,image);
		Vector3f normal=new Vector3f(heightL-heightR,2f,heightD-heightU);
		normal.normalise();
		return normal;

	}

	private float getHieght(int x,int y,BufferedImage image){
		if(x<0||x>=image.getHeight()||y<0||y>=image.getHeight()){
			return 0;
		}
		float hieght =image.getRGB(x, y);
		hieght+=MAX_PIXEL_COLOUR/2f;
		hieght/=MAX_PIXEL_COLOUR/2f;
		hieght*=MAX_HIEGHT;
		return hieght;
	}
}
