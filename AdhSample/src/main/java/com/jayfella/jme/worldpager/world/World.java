package com.jayfella.jme.worldpager.world;

import java.util.List;
import java.util.concurrent.ExecutorService;

import com.jayfella.jme.worldpager.core.NoiseEvaluator;
import com.jayfella.jme.worldpager.grid.SceneGrid;
import com.jayfella.jme.worldpager.grid.collision.CollisionGrid;
import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public interface World {

    WorldSettings getWorldSettings();

    NoiseEvaluator getWorldNoise();

    void setWorldNoise(NoiseEvaluator noiseEvaluator);

    ExecutorService getThreadPool();

    List<SceneGrid> getSceneGrids();

    SceneGrid getSceneGrid(String name);

    void addSceneGrid(SceneGrid sceneGrid);

    List<CollisionGrid> getCollisionGrids();

    CollisionGrid getCollisionGrid(String name);

    void addCollisionGrid(CollisionGrid collisionGrid);

    Application getApplication();

    Node getWorldNode();

    Vector3f getFollower();

    void setFollower(Vector3f vector3f);

}
