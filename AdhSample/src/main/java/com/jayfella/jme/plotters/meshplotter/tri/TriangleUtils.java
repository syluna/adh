package com.jayfella.jme.plotters.meshplotter.tri;

import java.nio.FloatBuffer;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.mesh.IndexBuffer;

public class TriangleUtils {

    public static int processTriangles(Mesh mesh, TriangleProcessor proc) {

        if (mesh.getVertexCount() == 0) {
            return 0;
        }
        switch (mesh.getMode()) {
        case LineLoop:
        case Lines:
        case LineStrip:
        case Points:
            return 0; // no triangles in those
        case Hybrid:
        case TriangleFan:
        case TriangleStrip:
            throw new UnsupportedOperationException("Mesh type not yet supported:" + mesh.getMode());
        case Triangles:
            if (mesh.getIndexBuffer() != null) {
                return doIndexedTriangles(mesh, proc);
            } else {
                return doTriangles(mesh, proc);
            }
        default:
            return 0;
        }
    }

    protected static int doTriangles(Mesh mesh, TriangleProcessor proc) {
        throw new UnsupportedOperationException("Non-indexed triangles not yet supported.");
    }

    protected static int doIndexedTriangles(Mesh mesh, TriangleProcessor proc) {
        Triangle tri = new Triangle();

        VertexBuffer vbPos = mesh.getBuffer(VertexBuffer.Type.Position);
        int posSize = vbPos.getNumComponents();
        VertexBuffer vbNorms = mesh.getBuffer(VertexBuffer.Type.Normal);
        int normSize = 0;
        VertexBuffer vbTexes = mesh.getBuffer(VertexBuffer.Type.TexCoord);
        int texesSize = 0;

        FloatBuffer pos = ((FloatBuffer) vbPos.getData()).duplicate();
        FloatBuffer norms = null;
        if (vbNorms != null) {
            norms = ((FloatBuffer) vbNorms.getData()).duplicate();
            normSize = vbNorms.getNumComponents();
        }
        FloatBuffer texes = null;
        if (vbTexes != null) {
            texes = ((FloatBuffer) vbTexes.getData()).duplicate();
            texesSize = vbTexes.getNumComponents();
        }

        IndexBuffer ib = mesh.getIndexBuffer();
        int size = ib.size();
        int triangleIndex = 0;

        // log.info("Processing " + size + " triangles");

        for (int i = 0; i < size;) {
            for (int v = 0; v < 3; v++) {

                int index = ib.get(i++);
                tri.indexes[v] = index;

                pos.position(index * posSize);
                Vector3f vert = tri.verts[v];
                vert.x = pos.get();
                vert.y = pos.get();
                vert.z = pos.get();
                if (norms != null) {
                    norms.position(index * normSize);
                    Vector3f norm = tri.norms[v];
                    norm.x = norms.get();
                    norm.y = norms.get();
                    norm.z = norms.get();
                }
                if (texes != null) {
                    texes.position(index * texesSize);
                    Vector2f tex = tri.texes[v];
                    tex.x = texes.get();
                    tex.y = texes.get();
                }
            }

            proc.processTriangle(mesh, triangleIndex++, tri);

        }
        // log.info("Processed " + size + " triangles");

        return triangleIndex;
    }

}
