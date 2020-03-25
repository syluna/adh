package fr.adh.client.gui.chat;

import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.tools.SizeValue;
import javax.annotation.Nonnull;

public class AdhChatBuilder extends ControlBuilder {

    public AdhChatBuilder(final int lines) {
        super("adh-chat");
        lines(lines);
    }

    public AdhChatBuilder(@Nonnull final String id, final int lines) {
        super(id, "adh-chat");
        lines(lines);
    }

    public AdhChatBuilder lines(final int lines) {
        set("lines", String.valueOf(lines));
        return this;
    }

    public AdhChatBuilder sendLabel(@Nonnull final String sendLabel) {
        set("sendLabel", sendLabel);
        return this;
    }

    public AdhChatBuilder chatLineHeight(@Nonnull final SizeValue value) {
        set("chatLineHeight", value.getValueAsString());
        return this;
    }
}
