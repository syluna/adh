package fr.adh.client.gui;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.lemur.Container;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WindowUtils {

    public static void toCenter(@NonNull final Camera camera, @NonNull final Container container) {
        int width = camera.getWidth();
        int height = camera.getHeight();

        // Apply standard scale
        float standardScale = 1.2f * (height / 720f);
        container.setLocalScale(standardScale);
        Vector3f size = new Vector3f(container.getPreferredSize()).multLocal(standardScale);
        // Centering window
        container.setLocalTranslation((width - size.x) / 2, (height + size.y) / 2, 0);
    }

    public static void toBottomLeft(@NonNull final Camera camera, @NonNull final Container container) {
        int height = camera.getHeight();
        // Apply standard scale
        float standardScale = 1.2f * (height / 720f);
        container.setLocalScale(standardScale);
        Vector3f size = new Vector3f(container.getPreferredSize()).multLocal(standardScale);
        // Left bottom window
        container.setLocalTranslation(0, size.y, 0);
    }

}
