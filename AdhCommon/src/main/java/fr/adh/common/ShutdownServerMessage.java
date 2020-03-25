package fr.adh.common;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Serializable
@NoArgsConstructor
@AllArgsConstructor
public class ShutdownServerMessage extends AbstractMessage {

    @Getter
    private String hello;

}
