package fr.adh.client.gui.chat;

import de.lessvoid.nifty.controls.ListBox.ListBoxViewConverter;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor
public class AdhChatViewConverter implements ListBoxViewConverter<AdhChatEntry> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdhChatViewConverter.class);

    private static final String ADH_CHAT_LINE_TEXT = "#adh-chat-line-text";

    @Override
    public void display(Element element, AdhChatEntry adhChatEntry) {
        final Element text = element.findElementById(ADH_CHAT_LINE_TEXT);
        if (text == null) {
            LOGGER.error("Failed to locate text part of chat line! Can't display entry.");
            return;
        }
        final TextRenderer textRenderer = text.getRenderer(TextRenderer.class);
        if (textRenderer == null) {
            LOGGER.error("Text entry of the chat line does not contain the required text renderer.");
            return;
        }
        if ("".equals(adhChatEntry.getStyle())) {
            text.setStyle(adhChatEntry.getStyle());
        } else {
            text.setStyle("default");
        }
        textRenderer.setText(formatChatMessage(adhChatEntry));
    }

    @Override
    public int getWidth(Element element, AdhChatEntry adhChatEntry) {
        final Element text = element.findElementById(ADH_CHAT_LINE_TEXT);
        if (text == null) {
            LOGGER.error("Failed to locate text part of chat line! Can't display entry.");
            return 0;
        }
        final TextRenderer textRenderer = text.getRenderer(TextRenderer.class);
        if (textRenderer == null) {
            LOGGER.error("Text entry of the chat line does not contain the required text renderer.");
            return 0;
        }
        return textRenderer.getFont() == null ? 0 : textRenderer.getFont().getWidth(formatChatMessage(adhChatEntry));
    }
    
    private String formatChatMessage(AdhChatEntry entry){
        return new StringBuilder("[").append(entry.getActor()).append("] ").append(entry.getMessage()).toString();
    }

}
