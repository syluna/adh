package fr.adh.common;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Serializable
@NoArgsConstructor
@AllArgsConstructor
public class SpawnEntityMessage extends AbstractMessage {

	@Getter
	private Integer[] ids;
	@Getter
	private boolean spawnOrDie;

	public SpawnEntityMessage(final int id, final boolean spawnOrDie) {
		this(new Integer[] { id }, spawnOrDie);
	}

}
