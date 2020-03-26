package fr.adh.common;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Serializable
@NoArgsConstructor
@AllArgsConstructor
public class WelcomeMessage extends AbstractMessage {

	@Getter
	private String playerName;

	@Getter
	private String message;

}
