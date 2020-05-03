package fr.adh.client.gui;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.ActionButton;
import com.simsilica.lemur.CallMethodAction;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.RangedValueModel;
import com.simsilica.lemur.TabbedPanel;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.event.ConsumingMouseListener;
import com.simsilica.lemur.event.CursorEventControl;
import com.simsilica.lemur.event.DragHandler;
import com.simsilica.lemur.event.MouseEventControl;

import fr.adh.client.AdhClient;
import fr.adh.client.I18n;

public class ChatState extends BaseAppState {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatState.class);

    private boolean initialized = false;

    private boolean autoscroll = false;

    private Container window;
    private ListBox<String> listBoxField;
    private VersionedReference<List<String>> listBoxRef;

    @Override
    protected void initialize(final Application application) {
        window = new Container();
        window.addChild(new Label("Tchat de Jérôme qui fait des tests"));

        CursorEventControl.addListenersToSpatial(window, new DragHandler());
        MouseEventControl.addListenersToSpatial(window, ConsumingMouseListener.INSTANCE);

        TabbedPanel tabs = window.addChild(new TabbedPanel());
        tabs.setInsets(new Insets3f(5, 5, 5, 5));

        String mainChat = I18n.get("chat.tab.main.title");
        tabs.addTab(mainChat, createTabContents(mainChat));
        String systemChat = I18n.get("chat.tab.system.title");
        tabs.addTab(systemChat, createTabContents2(systemChat));

        // Centering window
        applyStandardTransform(window);

        initialized = true;
    }

    @Override
    protected void cleanup(final Application application) {

    }

    @Override
    protected void onEnable() {
        if (!initialized) {
            return;
        }
        ((AdhClient) getApplication()).getGuiNode().attachChild(window);
        // GuiGlobals.getInstance().requestFocus(window);
    }

    @Override
    protected void onDisable() {
        if (!initialized) {
            return;
        }
        window.removeFromParent();
    }

    @Override
    public void update(final float tpf) {
        super.update(tpf);

        final RangedValueModel rangedValueModel = listBoxField.getSlider().getModel();
        if (listBoxRef.update() && !autoscroll && rangedValueModel.getValue() <= 0) {
            autoscroll = true;
        } else if (!listBoxRef.update() && autoscroll) {
            rangedValueModel.setValue(0.0);
            autoscroll = false;
        }
    }

    private Container createTabContents2(String name) {
        Container contents = new Container();

        contents.addChild(new Label(name));
        return contents;
    }

    private Container createTabContents(String name) {
        Container contents = new Container();

        listBoxField = contents.addChild(new ListBox<String>());
        listBoxRef = listBoxField.getModel().createReference();
        listBoxField.setPreferredSize(new Vector3f(300f, 200f, 0f));
        listBoxField.setVisibleItems(9);

        listBoxField.getSlider().getModel().setValue(0.0);

        // TODO Add input text field and send button
        contents.addChild(new ActionButton(new CallMethodAction(">", this, "addItem")));
        return contents;
    }

    protected void addItem() {
        addMessage("Item " + listBoxRef.get().size());
    }

    public void addMessage(final String message) {
        listBoxRef.get().add(message);
    }

    private void applyStandardTransform(final Container container) {
        int width = getApplication().getCamera().getWidth();
        int height = getApplication().getCamera().getHeight();

        // Apply standard scale
        float standardScale = 1.2f * (height / 720f);
        container.setLocalScale(standardScale);
        Vector3f size = new Vector3f(container.getPreferredSize()).multLocal(standardScale);
        // Centering window
        container.setLocalTranslation((width - size.x) / 2, (height + size.y) / 2, 0);
    }
}
