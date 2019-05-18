package entities;

import RenderEngine.DisplayManager;
import models.TexturedModel;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
import terrains.Terrain;

import java.util.Timer;
import java.util.TimerTask;

public class Player extends Entity {
private static final float RUN_SPEED =20;
private static final float TURN_SPEED=160;
private static final float GRAVITY=-50;
private static final float JUMP_POWER=30;
Terrain CurrentTerrain;
    private float currentSpeed=0;
    private float currentTurnSpeed=0;
    private float upwardsSpeed=0;
    private boolean isInAir=false;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }
    public void move(Terrain terrain){
        checkInputs();
        CurrentTerrain=terrain;
        super.increaseRotation(0,currentTurnSpeed* DisplayManager.getFrameTimeSeconds(),0);
        float distance=currentSpeed*DisplayManager.getFrameTimeSeconds();
        float dx=(float)(distance*Math.sin(Math.toRadians(super.getRotY())));
        float dz=(float)(distance*Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx,0,dz);
        upwardsSpeed+=GRAVITY*DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0,upwardsSpeed*DisplayManager.getFrameTimeSeconds(),0);
        float terrainHieght =CurrentTerrain.getHieghtofTerrain(super.getPosition().x,super.getPosition().z);
        if (super.getPosition().y<terrainHieght){
            upwardsSpeed=0;
            isInAir=false;
            super.getPosition().y=terrainHieght;
        }
    }
    private void jump(){
        if (!isInAir) {
            this.upwardsSpeed = JUMP_POWER;
            isInAir=true;
        }
    }
    private  void go_Forward(){
        this.currentSpeed=RUN_SPEED;
        super.increaseRotation(0,currentTurnSpeed* DisplayManager.getFrameTimeSeconds(),0);
        float distance=currentSpeed*DisplayManager.getFrameTimeSeconds();
        float dx=(float)(distance*Math.sin(Math.toRadians(super.getRotY())));
        float dz=(float)(distance*Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx,0,dz);
        upwardsSpeed+=GRAVITY*DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0,upwardsSpeed*DisplayManager.getFrameTimeSeconds(),0);
        float terrainHieght =CurrentTerrain.getHieghtofTerrain(super.getPosition().x,super.getPosition().z);
        if (super.getPosition().y<terrainHieght){
            upwardsSpeed=0;
            isInAir=false;
            super.getPosition().y=terrainHieght;
        }
    }
    private  void go_Right(){
        this.currentTurnSpeed=-TURN_SPEED;
        super.increaseRotation(0,currentTurnSpeed* DisplayManager.getFrameTimeSeconds(),0);
        float distance=currentSpeed*DisplayManager.getFrameTimeSeconds();
        float dx=(float)(distance*Math.sin(Math.toRadians(super.getRotY())));
        float dz=(float)(distance*Math.cos(Math.toRadians(super.getRotY())));
        super.increasePosition(dx,0,dz);
        upwardsSpeed+=GRAVITY*DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0,upwardsSpeed*DisplayManager.getFrameTimeSeconds(),0);
        float terrainHieght =CurrentTerrain.getHieghtofTerrain(super.getPosition().x,super.getPosition().z);
        if (super.getPosition().y<terrainHieght){
            upwardsSpeed=0;
            isInAir=false;
            super.getPosition().y=terrainHieght;
        }
    }
    private void checkInputs(){
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)){
            this.currentSpeed=RUN_SPEED;
        }else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
            this.currentSpeed=-RUN_SPEED;
        }
        else {
            this.currentSpeed=0;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
            this.currentTurnSpeed=-TURN_SPEED;
        }else if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
            this.currentTurnSpeed=TURN_SPEED;
        }
        else {
            this.currentTurnSpeed=0;
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
            jump();
        }
        if(Keyboard.isKeyDown(Keyboard.KEY_F)) {
           /* final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleWithFixedDelay(this::go_Forward, 1, 150, TimeUnit.MILLISECONDS);
            */Timer timer =new Timer();

                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        for (int i=0;i<5;i++) {
                            go_Forward();
                            go_Forward();
                            go_Forward();
                            go_Forward();
                            go_Forward();
                        }
                        go_Right();
                    }
                    }, 0, 500);


        }
    }
}
