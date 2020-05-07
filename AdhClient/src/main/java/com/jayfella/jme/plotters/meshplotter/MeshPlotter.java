package com.jayfella.jme.plotters.meshplotter;

import com.jayfella.jme.plotters.meshplotter.tri.Triangle;
import com.jayfella.jme.plotters.meshplotter.tri.TriangleProcessor;
import com.jayfella.jme.plotters.meshplotter.tri.TriangleUtils;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Paul Speed, James Khan
 */
public class MeshPlotter implements TriangleProcessor {

    private final List<MeshPlotPosition> positions = new ArrayList<>();

    private Vector3f min;
    private Vector3f max;
    private Vector3f world;

    private final MeshPlotterSettings settings;

    private final MeshPlotterRule[] rules;

    public MeshPlotter(MeshPlotterSettings settings, MeshPlotterRule... rules) {
        this.settings = settings;

        // this will never be null and our iterations will still occur.
        this.rules = rules != null ? rules : new MeshPlotterRule[0];
    }

    public void processMesh(Mesh mesh) {
        TriangleUtils.processTriangles(mesh, this);
    }

    public void setWorld(Vector3f world) {
        this.world = world;
    }

    public void setMin(Vector3f min) {
        this.min = min;
    }

    public void setMax(Vector3f max) {
        this.max = max;
    }

    public boolean hasPositions() {
        return !positions.isEmpty();
    }

    public List<MeshPlotPosition> getPositions() {
        return positions;
    }

    private boolean isInCell(Vector3f v) {

        if (v.x < min.x || v.y < min.y || v.z < min.z) {
            return false;
        }
        return !(v.x > max.x) && !(v.y > max.y) && !(v.z > max.z);
    }

    private boolean isInsideSpecifiedHeights(Vector3f v) {

        float deviationMin = ( (FastMath.nextRandomFloat() * 2.0f) - 1.0f ) * settings.getMinWorldHeightDeviation();
        float deviationMax = ( (FastMath.nextRandomFloat() * 2.0f) - 1.0f ) * settings.getMaxWorldHeightDeviation();

        float minY = settings.getMinWorldHeight() + deviationMin;
        float maxY = settings.getMaxWorldHeight() + deviationMax;

        if (v.y < minY) {
            return false;
        }
        return (v.y < maxY);
    }

    public void processTriangle(Mesh mesh, int index, Triangle tri) {
        //Vector3f[] verts = tri.verts;

        // does this triangle exceed the cell boundaries?
        if (!isInCell(tri.verts[0]) || !isInCell(tri.verts[1]) || !isInCell(tri.verts[2])) {
            return;
        }

        if (!isInsideSpecifiedHeights(tri.verts[0]) || !isInsideSpecifiedHeights(tri.verts[1]) || !isInsideSpecifiedHeights(tri.verts[2])) {
            return;
        }

        for (MeshPlotterRule rule : rules) {
            if (!rule.validate(world, mesh, index, tri)) {
                if (log.isDebugEnabled()) {
                    log.debug("Plotter rejected by rule: " + rule.getClass());
                }
                return;
            }
        }

        // all checks have passed, so let's generate a position.
        rasterize(tri);
    }

    private float getMin(float a, float b, float c) {
        if (a < b) {
            if (a < c) {
                return a;
            } else {
                return c;
            }
        } else {
            if (b < c) {
                return b;
            } else {
                return c;
            }
        }
    }

    /// <summary>
    /// Get the maximum value from 3 input values.
    /// </summary>
    /// <param name="a">the first value</param>
    /// <param name="b">the second value </param>
    /// <param name="c">the third value</param>
    /// <returns>the highest value of the 3 input values</returns>
    private float getMax(float a, float b, float c) {
        if (a > b) {
            if (a > c) {
                return a;
            } else {
                return c;
            }
        } else {
            if (b > c) {
                return b;
            } else {
                return c;
            }
        }
    }

    private static final Logger log = LoggerFactory.getLogger(MeshPlotter.class);

    private void rasterize(Triangle tri) {

        Vector3f[] verts = tri.verts;
        Vector3f[] norms = tri.norms;

        Vector3f vf0 = verts[0].clone();
        Vector3f vf1 = verts[1].clone();
        Vector3f vf2 = verts[2].clone();

        vf0.y = 0;
        vf1.y = 0;
        vf2.y = 0;

        // create a "bounding box" from the triangle.
        float minX = getMin(verts[0].x, verts[1].x, verts[2].x);
        float minZ = getMin(verts[0].z, verts[1].z, verts[2].z);
        float maxX = getMax(verts[0].x, verts[1].x, verts[2].x);
        float maxZ = getMax(verts[0].z, verts[1].z, verts[2].z);

        // quantize the positions to even resolutions.
        minX -= minX % settings.getDensity();
        minZ -= minZ % settings.getDensity();
        maxX += maxX % settings.getDensity();
        maxZ += maxZ % settings.getDensity();

        // add a little noise to stop them all being the same size.
        minX += FastMath.nextRandomFloat() * 0.1f;
        minZ += FastMath.nextRandomFloat() * 0.1f;
        maxX -= FastMath.nextRandomFloat() * 0.1f;
        maxZ -= FastMath.nextRandomFloat() * 0.1f;

        Vector3f p = new Vector3f(minX, 0, minZ);

        // check the angle of the normal
        // float threshold = FastMath.cos(settings.getMaxAngle() * FastMath.DEG_TO_RAD);
        float threshold = FastMath.cos(settings.getMaxAngle() * FastMath.DEG_TO_RAD);

        Vector3f pOffset = new Vector3f();

        for (p.z = minZ; p.z < maxZ; p.z += settings.getDensity()) {
            for (p.x = minX; p.x < maxX; p.x += settings.getDensity()) {
                // add a small positional offset to the position to stop them all looking like perfect rows.
                // float plotOffset = noise.calculateNoise(new Vector3f( world.x + (p.x - min.x), 1, world.z + (p.z - min.z) )) * 0.5f;

                pOffset.set(p);
                // pOffset += new Vector3f(plotOffset, 0, plotOffset);
                //pOffset.addLocal(plotOffset, 0, plotOffset);

                // calculate the barycentric coordinates

                Vector3f v0 = vf2.subtract(vf0);
                Vector3f v1 = vf1.subtract(vf0);
                Vector3f v2 = pOffset.subtract(vf0);

                float dot00 = v0.dot(v0);
                float dot01 = v0.dot(v1);
                float dot02 = v0.dot(v2);
                float dot11 = v1.dot(v1);
                float dot12 = v1.dot(v2);

                float invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
                float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
                float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

                // make sure we're in the triangle
                if (u >= 0 && v >= 0 && u + v < 1) {
                    float y2 = verts[1].y - verts[0].y;
                    float y1 = verts[2].y - verts[0].y;
                    float y = verts[0].y + y1 * u + y2 * v;

                    Vector3f plot = pOffset.clone();
                    plot.y = y;
                    // plot.subtractLocal(min);

                    Vector3f n2 = norms[1].subtract(norms[0]);
                    Vector3f n1 = norms[2].subtract(norms[0]);
                    Vector3f newNorm = norms[0].add(n1.mult(u).add(n2.mult(v)));
                    newNorm.normalizeLocal();

                    if (newNorm.y >= threshold) {
                        float rndSize = settings.getMinSize() + ((FastMath.nextRandomFloat() * (settings.getMaxSize() - settings.getMinSize())));
                        positions.add(new MeshPlotPosition(plot, newNorm, rndSize));
                    }
                }
            }
        }

    }

    public Mesh createMesh() {

        int triCount = positions.size();
        FloatBuffer pb = BufferUtils.createVector3Buffer(triCount * 3);
        FloatBuffer nb = BufferUtils.createVector3Buffer(triCount * 3);
        FloatBuffer tb = BufferUtils.createVector2Buffer(triCount * 3);

        int texCoordSize = 2;

        for( int i = 0; i < triCount; i++ ) {
            MeshPlotPosition position = positions.get(i);
            Vector3f p1 = position.getPos();
            Vector3f normal = position.getDir();
            float size = position.getSize() * 1;

            // Shader billboarded triangles
            pb.put(p1.x).put(p1.y).put(p1.z);
            pb.put(p1.x).put(p1.y).put(p1.z);
            pb.put(p1.x).put(p1.y).put(p1.z);

            tb.put(i + 0.25f).put(size);
            tb.put(i + 0.5f).put(size);
            tb.put(i + 0f).put(size);

            nb.put(normal.x).put(normal.y).put(normal.z);
            nb.put(normal.x).put(normal.y).put(normal.z);
            nb.put(normal.x).put(normal.y).put(normal.z);

        }

        Mesh mesh = new Mesh();
        mesh.setBuffer(VertexBuffer.Type.Position, 3, pb);
        mesh.setBuffer(VertexBuffer.Type.Normal, 3, nb);
        mesh.setBuffer(VertexBuffer.Type.TexCoord, texCoordSize, tb);

        mesh.updateBound();

        return mesh;

    }

}
