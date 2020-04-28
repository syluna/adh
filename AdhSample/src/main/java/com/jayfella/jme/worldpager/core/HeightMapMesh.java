package com.jayfella.jme.worldpager.core;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Generates a mesh from a heightmap.
 */
public class HeightMapMesh extends Mesh {

    private int hmapDim;
    private float[] heightmap;

    public HeightMapMesh(float[] heightmap) {

        super();

        this.heightmap = heightmap;
        this.hmapDim = (int) Math.sqrt(heightmap.length);

        int meshDim = hmapDim - 2;

        Vector3f[] vertArray = new Vector3f[heightmap.length];
        int[] triIndexes = new int[(vertArray.length - hmapDim) * 6];

        // calculate the positions and indexes twice.
        // once at the larger size, once at the smaller size.
        // get the normals at the larger size and transfer them to the smaller size.

        int idx = 0;

        for (int x = 0; x < hmapDim; x++) {
            for (int z = 0; z < hmapDim; z++) {

                // vertex
                float height = getHeightMapValue(x, z);
                vertArray[(z * hmapDim) + x] = new Vector3f(x, height, z);

                // indices
                if (x == 0 || z == 0) continue;

                triIndexes[idx++] = hmapDim * (x - 1) + z - 1; //Bottom left - First triangle
                triIndexes[idx++] = hmapDim * x + z - 1; //Bottom right
                triIndexes[idx++] = hmapDim * x + z; //Top right

                triIndexes[idx++] = hmapDim * x + z; //Top right - Second triangle
                triIndexes[idx++] = hmapDim * (x - 1) + z; //Top left
                triIndexes[idx++] = hmapDim * (x - 1) + z - 1; //Bottom left
            }
        }

        // use these to calculate the normals.
        Vector3f[] normArray = calcNormals(triIndexes, vertArray);

        // now re-construct them to the actual size.
        vertArray = new Vector3f[meshDim * meshDim];
        triIndexes = new int[(vertArray.length - meshDim) * 6];

        idx = 0;

        for (int x = 0; x < meshDim; x++) {
            for (int z = 0; z < meshDim; z++) {

                // vertex
                float height = getHeightMapValue(x + 1, z + 1);
                vertArray[(z * meshDim) + x] = new Vector3f(x, height, z);

                // indices
                if (x == 0 || z == 0) continue;

                triIndexes[idx++] = meshDim * (x - 1) + z - 1; //Bottom left - First triangle
                triIndexes[idx++] = meshDim * x + z - 1; //Bottom right
                triIndexes[idx++] = meshDim * x + z; //Top right

                triIndexes[idx++] = meshDim * x + z; //Top right - Second triangle
                triIndexes[idx++] = meshDim * (x - 1) + z; //Top left
                triIndexes[idx++] = meshDim * (x - 1) + z - 1; //Bottom left
            }
        }

        Vector2f[] texArray = new Vector2f[vertArray.length];

        //Give UV coords X,Z world coords
        for (int i = 0; i < vertArray.length; i++) {
            texArray[i] = new Vector2f(vertArray[i].x, vertArray[i].z);
        }

        // move the normals to their proper positions
        Vector3f[] newNorms = new Vector3f[vertArray.length];
        idx = 0;
        for (int z = 0; z < meshDim; z++) {
            for (int x = 0; x < meshDim; x++) {

                newNorms[idx++] = normArray[((z + 1) * hmapDim) + (x + 1)];
            }
        }

        FloatBuffer pb = BufferUtils.createFloatBuffer(vertArray);
        setBuffer(VertexBuffer.Type.Position, 3, pb);

        FloatBuffer nb = BufferUtils.createFloatBuffer(newNorms);
        setBuffer(VertexBuffer.Type.Normal, 3, nb);

        FloatBuffer tb = BufferUtils.createFloatBuffer(texArray);
        setBuffer(VertexBuffer.Type.TexCoord, 2, tb);

        IntBuffer ib = BufferUtils.createIntBuffer(triIndexes);
        setBuffer(VertexBuffer.Type.Index, 3, ib);

        updateBound();
    }


    private Vector3f[] calcNormals(int[] indices, Vector3f[] verts) {

        int numIndices = indices.length;

        Vector3f[] norms = new Vector3f[verts.length];

        for (int i = 0; i < norms.length; i++) {
            norms[i] = new Vector3f();
        }

        for (int t = 0; t < numIndices / 3; ++t)
        {
            int t1 = indices[t*3+0];
            int t2 = indices[t*3+1];
            int t3 = indices[t*3+2];
            Vector3f a = verts[t1];
            Vector3f b = verts[t2];
            Vector3f c = verts[t3];

            Vector3f u = b.subtract(a);
            Vector3f v = c.subtract(a);
            Vector3f n = u.cross(v);

            norms[t1].addLocal(n);
            norms[t2].addLocal(n);
            norms[t3].addLocal(n);

        }

        for (Vector3f norm : norms) {
            norm.normalizeLocal();
        }

        return norms;
    }

    private float getHeightMapValue(int x, int z) {
        return heightmap[(z * hmapDim) + x];
    }

    public float getHeight(int x, int z) {
        return getHeightMapValue(x + 1, z + 1);
    }

}
