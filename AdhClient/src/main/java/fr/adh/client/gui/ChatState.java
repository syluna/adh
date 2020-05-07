package fr.adh.client.gui;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.input.KeyInput;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.RangedValueModel;
import com.simsilica.lemur.TabbedPanel;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.component.TextEntryComponent;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.event.ConsumingMouseListener;
import com.simsilica.lemur.event.CursorEventControl;
import com.simsilica.lemur.event.KeyAction;
import com.simsilica.lemur.event.MouseEventControl;
import com.simsilica.lemur.text.DocumentModelFilter;

import fr.adh.client.AdhClient;
import fr.adh.client.I18n;
import fr.adh.client.gui.handler.ClampDragHandler;

public class ChatState extends BaseAppState {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatState.class);

    private boolean initialized = false;

    private boolean autoscroll = false;

    private Container window;
    private Container windowTitle;

    private ListBox<String> listBoxField;
    private VersionedReference<List<String>> listBoxRef;

    ClampDragHandler eventHandler;

    @Override
    protected void initialize(final Application application) {
        window = new Container();
        windowTitle = window.addChild(new Container());
        windowTitle.addChild(new Label("Tchat de Jérôme qui fait des tests"));

        eventHandler = new ClampDragHandler(true);
        // CursorEventControl.addListenersToSpatial(window, new DragHandler());
        TabbedPanel tabs = window.addChild(new TabbedPanel());
        tabs.setInsets(new Insets3f(5, 5, 5, 5));

        tabs.addTab(I18n.get("chat.tab.main.title"), createTabContents());
        String systemChat = I18n.get("chat.tab.system.title");
        tabs.addTab(systemChat, createTabContents2(systemChat));

        // Centering window
        WindowUtils.toBottomLeft(getApplication().getCamera(), window);

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
        CursorEventControl.addListenersToSpatial(windowTitle, eventHandler);
        MouseEventControl.addListenersToSpatial(window, ConsumingMouseListener.INSTANCE);

        ((AdhClient) getApplication()).getGuiNode().attachChild(window);
    }

    @Override
    protected void onDisable() {
        if (!initialized) {
            return;
        }
        CursorEventControl.removeListenersFromSpatial(windowTitle, eventHandler);
        MouseEventControl.removeListenersFromSpatial(window, ConsumingMouseListener.INSTANCE);

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

    private Container createTabContents() {
        Container contents = new Container();

        listBoxField = contents.addChild(new ListBox<String>());
        listBoxRef = listBoxField.getModel().createReference();
        listBoxField.setPreferredSize(new Vector3f(300f, 110f, 0f));
        listBoxField.setVisibleItems(5);

        listBoxField.getSlider().getModel().setValue(0.0);

        Container inputContents = contents
                .addChild(new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.First, FillMode.First)));

        TextField textField = inputContents.addChild(new TextField(new DocumentModelFilter()), 0, 0);
        textField.setPreferredWidth(200);
        textField.setTextVAlignment(VAlignment.Center);
        textField.setInsets(new Insets3f(3, 3, 3, 3));
        textField.getActionMap().put(new KeyAction(KeyInput.KEY_RETURN), (TextEntryComponent source, KeyAction key) -> {
            String entry = source.getDocumentModel().getText().trim();
            if (!"".contentEquals(entry)) {
                AdhClient.getInstance().sendMessage(entry);
                source.getDocumentModel().setText("");
            }
        });

        return contents;
    }

    public void addMessage(final String message) {
        listBoxRef.get().add(message);
    }

}
