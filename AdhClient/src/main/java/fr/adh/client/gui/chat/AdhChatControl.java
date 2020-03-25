package fr.adh.client.gui.chat;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.AbstractController;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.Parameters;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.input.NiftyStandardInputEvent;
import de.lessvoid.nifty.screen.KeyInputHandler;
import de.lessvoid.nifty.screen.Screen;
import fr.adh.client.gui.chat.event.AdhChatSendTextEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor
public class AdhChatControl extends AbstractController implements KeyInputHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdhChatControl.class);

    private static final String ADH_CHAT_BOX = "#adh-chat-box";
    private static final String ADH_CHAT_TEXT_INPUT = "#adh-chat-text-input";

    @Nullable
    private TextField textControl;
    @Nonnull
    private Nifty nifty;
    @Nonnull
    private final List<AdhChatEntry> linesBuffer = new ArrayList<AdhChatEntry>();

    @Override
    public boolean keyEvent(final NiftyInputEvent inputEvent) {
        if (inputEvent == NiftyStandardInputEvent.SubmitText) {
            sendText();
            return true;
        }
        return false;
    }

    @Override
    public boolean inputEvent(final NiftyInputEvent inputEvent) {
        return keyEvent(inputEvent);
    }

    @Override
    public void bind(@Nonnull final Nifty niftyParam, @Nonnull final Screen screenParam, @Nonnull final Element newElement, @Nonnull final Parameters properties) {
        super.bind(newElement);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("binding AdhChatControl");
        }
        nifty = niftyParam;
        final ListBox<AdhChatEntry> chatBox = getListBox(ADH_CHAT_BOX);
        if (chatBox == null) {
            LOGGER.error("Element for chat box [{}] not found. AdhChatControl will not work.", ADH_CHAT_BOX);
        } else {
            while (!linesBuffer.isEmpty()) {
                AdhChatEntry line = linesBuffer.remove(0);
                LOGGER.info("adding message {}", (chatBox.itemCount() + 1));
                chatBox.addItem(line);
                chatBox.showItemByIndex(chatBox.itemCount() - 1);
            }
        }
    }

    @Override
    public void onStartScreen() {
        Element element = getElement();
        if (element != null) {
            textControl = element.findNiftyControl(ADH_CHAT_TEXT_INPUT, TextField.class);
            if (textControl == null) {
                LOGGER.error("Text input field for chat box was not found!");
            } else {
                Element textControlElement = textControl.getElement();
                if (textControlElement != null) {
                    textControlElement.addInputHandler(this);
                }
            }
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private ListBox<AdhChatEntry> getListBox(@Nonnull final String name) {
        Element element = getElement();
        return element == null ? null : element.findNiftyControl(name, ListBox.class);
    }

    @Nonnull
    public List<AdhChatEntry> getLines() {
        final ListBox<AdhChatEntry> chatBox = getListBox(ADH_CHAT_BOX);
        return chatBox == null ? Collections.emptyList() : chatBox.getItems();
    }

    @Override
    public void onFocus(final boolean arg0) {
        if (textControl != null) {
            textControl.setFocus();
        }
    }

    public final void sendText() {
        final StringBuilder text = new StringBuilder("");
        if (textControl != null) {
            text.append(textControl.getRealText());
            textControl.setText("");
        }
        if(text.length() == 0){
            return;
        }
        final String id = getId();
        if (id != null) {
            nifty.publishEvent(id, new AdhChatSendTextEvent(this, "player", text.toString()));
        }
    }
    
    public void receivedChatLine(@Nonnull String actor, @Nonnull String text) {
        receivedChatLine(actor, text, null);
    }

    public void receivedChatLine(@Nonnull String actor, @Nonnull String text, @Nullable String style) {
        if (linesBuffer.isEmpty()) {
            final ListBox<AdhChatEntry> chatBox = getListBox(ADH_CHAT_BOX);
            if (chatBox != null) {
                LOGGER.debug("adding message {}", (chatBox.itemCount() + 1));
                final AdhChatEntry item = new AdhChatEntry(actor, text, style);
                chatBox.addItem(item);
                chatBox.showItemByIndex(chatBox.itemCount() - 1);
            } else {
                linesBuffer.add(new AdhChatEntry(actor, text, style));
            }
        } else {
            linesBuffer.add(new AdhChatEntry(actor, text, style));
        }
    }
}
