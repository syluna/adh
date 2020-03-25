package fr.adh.client.gui.chat.event;

import de.lessvoid.nifty.NiftyEvent;
import fr.adh.client.gui.chat.AdhChatControl;
import javax.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AdhChatSendTextEvent implements NiftyEvent {

    @Nonnull
    private final AdhChatControl adhChatControl;
    @Nonnull
    private final String from;
    @Nonnull
    private final String message;

}
