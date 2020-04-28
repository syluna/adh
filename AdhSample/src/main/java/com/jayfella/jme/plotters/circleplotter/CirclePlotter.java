package com.jayfella.jme.plotters.circleplotter;

import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.texture.image.ColorSpace;
import com.jme3.texture.image.ImageRaster;
import com.jme3.util.BufferUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CirclePlotter {

    private final int seed;
    private final Vector2f region;
    private final Random random;

    private final List<Circle> positions = new ArrayList<>();

    public CirclePlotter(int seed, Vector2f region) {
        this.seed = seed;
        this.region = region;
        this.random = new Random(seed);
    }

    public int getSeed() {
        return seed;
    }

    public Vector2f getRegion() {
        return region;
    }

    public List<Circle> getPositions() {
        return positions;
    }

    public List<Circle> addPoints(int amount, float minRadius, float maxRadius, float minSpaceBetween, int maxAttempts) {
        return addPoints(amount, minRadius, maxRadius, minSpaceBetween, maxAttempts, ColorRGBA.Red);
    }

    public List<Circle> addPoints(int amount, float minRadius, float maxRadius, float minSpaceBetween, int maxAttempts, ColorRGBA color) {

        List<Circle> points = new ArrayList<>();

        int attempts = 0;

        while (points.size() < amount && attempts < maxAttempts) {

            Circle point = new Circle(
                    new Vector2f(random.nextFloat() * (int)region.x, random.nextFloat() * (int)region.y),
                    minRadius + ( (maxRadius - minRadius) * random.nextFloat() ),
                    color
            );


            // don't let our point go outside the "zone".
            if (point.getPosition().x - point.getRadius() < 0 ||
                    point.getPosition().x + point.getRadius() > (int)region.x ||
                    point.getPosition().y - point.getRadius() < 0 ||
                    point.getPosition().y + point.getRadius() > (int)region.y) {

                continue;
            }

            boolean intersects = false;

            // check if we're intersecting with existing points
            for (Circle p : positions) {
                if (p.intersects(point, minSpaceBetween)) {
                    intersects = true;
                    break;
                }
            }

            // if we are, back out now.
            if (intersects) {
                attempts++;
                continue;
            }

            // check if we're intersecting with our current points.
            for (Circle p : points) {
                if (p.intersects(point, minSpaceBetween)) {
                    intersects = true;
                    break;
                }
            }

            // if we are, back out now.
            if (intersects) {
                attempts++;
                continue;
            }

            // everything seems fine, plot the position.
            points.add(point);
        }

        // we won't always get what we wanted. We might not have been able to fit the amount we wanted due to space.
        // System.out.println("Tried: " + amount + " - Managed: " + points.size());

        positions.addAll(points);
        return points;
    }

    public Texture2D generateTexture() {

        ByteBuffer buffer = BufferUtils.createByteBuffer((int)region.x * (int)region.y * 4);
        Image result = new Image(Image.Format.RGB8, (int)region.x, (int)region.y, buffer, ColorSpace.sRGB);
        ImageRaster imageRaster = ImageRaster.create(result);

        for (int x = 0; x < (int)region.x; x++) {
            for (int y = 0; y < (int)region.y; y++) {
                imageRaster.setPixel(x, y, ColorRGBA.DarkGray);
            }
        }

        for (Circle p : positions) {

            for (int i = 0; i < 360; i++) {
                int x = (int) (p.getPosition().x + p.getRadius() * FastMath.cos(i * FastMath.DEG_TO_RAD));
                int y = (int) (p.getPosition().y + p.getRadius() * FastMath.sin(i * FastMath.DEG_TO_RAD));

                if ( x >= 0 && x < (int)region.x && y >= 0 && y < (int)region.y) {
                    imageRaster.setPixel(x, y, p.getColor());
                }
            }
        }

        Texture2D texture = new Texture2D(result);
        texture.setMagFilter(Texture.MagFilter.Nearest);

        return texture;
    }

}
