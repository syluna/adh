package com.jayfella.jme.plotters.test;

import com.jayfella.jme.plotters.circleplotter.Circle;
import com.jayfella.jme.plotters.circleplotter.CirclePlotter;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture2D;

import java.util.List;
import java.util.Random;

public class TestCirclePlotter extends SimpleApplication {

    public static void main(String... args) {

        TestCirclePlotter testCirclePlotter = new TestCirclePlotter();

        AppSettings appSettings = new AppSettings(true);
        appSettings.setResolution(1280, 720);
        appSettings.setFrameRate(120);
        appSettings.setTitle("Test :: Vegetation Plotter");

        testCirclePlotter.setSettings(appSettings);
        testCirclePlotter.setShowSettings(false);
        testCirclePlotter.setPauseOnLostFocus(false);

        testCirclePlotter.start();
    }


    @Override
    public void simpleInitApp() {

        int sizeX = 128;
        int sizeY = 128;

        for (int x = 0; x < sizeX * 9; x += sizeX) {
            for (int y = 0; y < sizeY * 5; y += sizeY) {

                Random random = new Random(x + y);

                int treeCount = FastMath.rand.nextInt(2);
                int bushCount = FastMath.rand.nextInt(3);
                int flowerCount = FastMath.rand.nextInt(30);

                CirclePlotter circlePlotter = new CirclePlotter(random.nextInt(), new Vector2f(sizeX, sizeY));

                // start big and work our way down in size. This avoids the little ones stealing all the space.

                List<Circle> bigTrees = circlePlotter.addPoints(treeCount, 10, 30, 15, 1000, ColorRGBA.Red);
                List<Circle> bushes = circlePlotter.addPoints(bushCount, 5, 6, 8, 1000, ColorRGBA.Green);
                List<Circle> flowers = circlePlotter.addPoints(flowerCount, 1, 2, 3, 1000, ColorRGBA.Yellow);

                System.out.println("Big Trees -> Tried: " + treeCount + " - Managed: " + bigTrees.size());
                System.out.println("Bushes -> Tried: " + bushCount + " - Managed: " + bushes.size());
                System.out.println("Flowers -> Tried: " + flowerCount + " - Managed: " + flowers.size());

                Geometry plane = new Geometry("Plane", new Quad(sizeX, sizeY));
                // plane.setLocalScale(5);
                plane.setMaterial(new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"));
                guiNode.attachChild(plane);

                Texture2D texture = circlePlotter.generateTexture();
                plane.setLocalTranslation(x, y, 1);
                plane.getMaterial().setTexture("ColorMap", texture);

            }
        }



    }

}
