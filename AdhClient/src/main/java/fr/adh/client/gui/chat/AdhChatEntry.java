package fr.adh.client.gui.chat;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class AdhChatEntry {

    private final String actor;
    private final String message;
    private final String style;

    public AdhChatEntry(final String actor, final String message) {
        this(actor, message, null);
    }

}
