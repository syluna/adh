package fr.adh.client.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.ActionButton;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.CallMethodAction;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.PasswordField;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.BorderLayout;
import com.simsilica.lemur.component.BorderLayout.Position;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.event.PopupState;
import com.simsilica.lemur.text.DocumentModel;
import com.simsilica.lemur.text.DocumentModelFilter;
import com.simsilica.lemur.text.TextFilters;

import fr.adh.client.AdhClient;
import fr.adh.client.I18n;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class LoginState extends BaseAppState {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginState.class);

    private static final IconComponent EYE_ON_ICON = new IconComponent("Interface/Icon/EyeOn.png");
    private static final IconComponent EYE_OFF_ICON = new IconComponent("Interface/Icon/EyeOff.png");

    private boolean initialized = false;

    private Container window;

    private PasswordField passwordField;
    private ActionButton eyeButton;
    private Checkbox saveCredentials;
    private ActionButton connectButton;

    private VersionedReference<DocumentModel> loginFieldRef;
    private VersionedReference<DocumentModel> passwordFieldRef;

    @Override
    protected void initialize(final Application application) {
        window = new Container();
        Label titleLabel = window.addChild(new Label(I18n.get("login.title")));
        titleLabel.setInsets(new Insets3f(10, 5, 5, 5));

        Container inputBox = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Even, FillMode.Last));
        inputBox.setInsets(new Insets3f(5, 5, 0, 5));

        // Login line
        Label loginLabel = inputBox.addChild(new Label(I18n.get("login.label")));
        loginLabel.setInsets(new Insets3f(5, 5, 5, 5));
        DocumentModelFilter doc = new DocumentModelFilter();
        doc.setInputTransform(TextFilters.alpha());
        TextField loginField = inputBox.addChild(new TextField(doc), 1);
        loginField.setPreferredWidth(200);
        loginField.setTextVAlignment(VAlignment.Center);
        loginField.setInsets(new Insets3f(3, 3, 3, 3));
        loginField.setText("Jérôme");
        loginFieldRef = loginField.getDocumentModel().createReference();
        window.addChild(inputBox);

        // Password line
        Container passwordBox = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Even, FillMode.Last));
        passwordBox.setInsets(new Insets3f(5, 5, 0, 5));
        Label passwordLabel = passwordBox.addChild(new Label(I18n.get("password.label")));
        passwordLabel.setInsets(new Insets3f(5, 5, 5, 5));
        passwordField = passwordBox.addChild(new PasswordField(""), 1);
        passwordField.setPreferredWidth(loginField.getPreferredWidth() - 24f);
        passwordField.setTextVAlignment(VAlignment.Center);
        passwordField.setInsets(new Insets3f(3, 3, 3, 0));
        passwordFieldRef = passwordField.getDocumentModel().createReference();

        eyeButton = passwordBox.addChild(new ActionButton(new CallMethodAction("", this, "switchPasswordVisibility")),
                2);
        eyeButton.setTextVAlignment(VAlignment.Center);
        eyeButton.setInsets(new Insets3f(3, 0, 3, 3));
        eyeButton.setIcon(EYE_ON_ICON);
        eyeButton.setPreferredSize(new Vector3f(24f, 24f, 0));
        window.addChild(passwordBox);

        saveCredentials = window.addChild(new Checkbox(I18n.get("rememberme.label")));
        saveCredentials.setInsets(new Insets3f(10, 5, 0, 5));

        Vector3f labelSize = loginLabel.getPreferredSize().getX() > passwordLabel.getPreferredSize().getX()
                ? loginLabel.getPreferredSize()
                : passwordLabel.getPreferredSize();
        loginLabel.setPreferredSize(labelSize);
        passwordLabel.setPreferredSize(labelSize);

        // Buttons containers transparent
        Container buttonBox = new Container(new BorderLayout());
        buttonBox.setInsets(new Insets3f(15, 5, 5, 5));
        buttonBox.setAlpha(0f);

        connectButton = buttonBox.addChild(
                new ActionButton(new CallMethodAction(I18n.get("login.button"), this, "login")), Position.East);
        connectButton.setTextHAlignment(HAlignment.Center);
        connectButton.setTextVAlignment(VAlignment.Center);
        ((TbtQuadBackgroundComponent) connectButton.getBackground()).setMargin(10, 5);
        connectButton.setEnabled(false);

        ActionButton cancelButton = buttonBox.addChild(
                new ActionButton(new CallMethodAction(I18n.get("cancel.button"), application, "stop")), Position.West);
        cancelButton.setTextHAlignment(HAlignment.Center);
        cancelButton.setTextVAlignment(VAlignment.Center);
        ((TbtQuadBackgroundComponent) cancelButton.getBackground()).setMargin(10, 5);
        window.addChild(buttonBox);

        WindowUtils.toCenter(getApplication().getCamera(), window);

        initialized = true;
    }

    @Override
    public void update(final float tpf) {
        super.update(tpf);
        if (!initialized || !isEnabled()) {
            return;
        }
        if (loginFieldRef.update() || passwordFieldRef.update()) {
            connectButton.setEnabled(
                    !(loginFieldRef.get().getText().isBlank() || passwordFieldRef.get().getText().isBlank()));
        }
    }

    @Override
    protected void cleanup(final Application application) {
        // Do nothing
    }

    @Override
    protected void onEnable() {
        if (!initialized) {
            return;
        }
        getState(PopupState.class).showModalPopup(window);
    }

    @Override
    protected void onDisable() {
        if (!initialized) {
            return;
        }
        window.removeFromParent();
    }

    protected void login() {
        final String login = loginFieldRef.get().getText();
        final String password = passwordFieldRef.get().getText();
        if (login.isBlank() || password.isBlank()) {
            return;
        }
        LOGGER.info("Login request with credentials [{}/{}] and need save [{}].", login, password,
                saveCredentials.isChecked());
        AdhClient.getInstance().createConnection(login, password);
    }

    protected void switchPasswordVisibility() {
        if (eyeButton.getIcon().equals(EYE_ON_ICON)) {
            eyeButton.setIcon(EYE_OFF_ICON);
            passwordField.setOutputCharacter(null);
        } else {
            eyeButton.setIcon(EYE_ON_ICON);
            passwordField.setOutputCharacter('*');
        }
    }

}
