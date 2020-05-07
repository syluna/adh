package fr.adh.client.world.tree;

import java.util.List;
import java.util.Random;

import com.jayfella.fastnoise.LayeredNoise;
import com.jayfella.fastnoise.NoiseLayer;
import com.jayfella.jme.plotters.circleplotter.Circle;
import com.jayfella.jme.plotters.circleplotter.CirclePlotter;
import com.jayfella.jme.worldpager.core.GridPos2i;
import com.jayfella.jme.worldpager.core.GridSettings;
import com.jayfella.jme.worldpager.core.NoiseEvaluator;
import com.jayfella.jme.worldpager.grid.ModelGrid;
import com.jayfella.jme.worldpager.world.World;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import jme3tools.optimize.GeometryBatchFactory;

public class TreesGrid extends ModelGrid {

	NoiseEvaluator noiseEvaluator;
	private PlottedModel[] trees;

	public TreesGrid(World world, GridSettings gridSettings, PlottedModel... trees) {
		super(world, gridSettings);

		this.trees = trees;

		LayeredNoise layeredNoise = new LayeredNoise();

		NoiseLayer nLayer_1 = new NoiseLayer("Tree Noise", (int) world.getWorldSettings().getSeed() + 10);
		layeredNoise.addLayer(nLayer_1);

		noiseEvaluator = new NoiseEvaluator() {
			@Override
			public float evaluate(Vector2f loc) {
				return nLayer_1.evaluate(loc);
			}
		};
	}

	@Override
	public Object[] buildCell(GridPos2i gridPos) {
		// Use a repeatable seed. This will always be the same, so we will always get the same result.
		int seed = gridPos.hashCode();
		Random random = new Random(seed);

		CirclePlotter circlePlotter = new CirclePlotter(random.nextInt(),
				new Vector2f(getGridSettings().getCellSize().getSize(), getGridSettings().getCellSize().getSize()));

		Node node = new Node("Trees");
		for (int i = 0; i < trees.length; i++) {
			PlottedModel model = trees[i];
			if (random.nextFloat() > model.getLikelihood()) {
				continue;
			}
			// use noise to determine the amount so that we get a natural flow of trees.
			int amount = (int) ((noiseEvaluator.evaluate(new Vector2f(gridPos.getWorldTranslationX(), gridPos.getWorldTranslationZ()))
					* (model.getMaxAmount() - model.getMinAmount())) + model.getMinAmount());
			List<Circle> positions = circlePlotter.addPoints(amount,
					model.getMinRadius(),
					model.getMaxRadius(),
					model.getMinSpaceBetween(),
					model.getMaxAttempts());

			for (Circle circle : positions) {
				Vector2f pos = circle.getPosition().add(new Vector2f(gridPos.getWorldTranslationX(), gridPos.getWorldTranslationZ()));
				float height = getWorld().getWorldNoise().evaluate(pos);

				if (height > model.getMinHeight() && height < model.getMaxHeight()) {
					Spatial tree = model.getTreeModel().clone();
					float scale = (random.nextFloat() * (model.getMaxScale() - model.getMinScale())) + model.getMinScale();
					tree.setLocalScale(scale);

					// rotate the tree so they don't all point in the same direction.
					float rotateY = random.nextFloat() * FastMath.TWO_PI;
					tree.setLocalRotation(new Quaternion().fromAngles(new float[] { 0, rotateY, 0 }));

					tree.setLocalTranslation(circle.getPosition().x, height, circle.getPosition().y);
					node.attachChild(tree);
				}
			}
		}
		Spatial batched = GeometryBatchFactory.optimize(node);
		return new Object[] { batched };
	}

	public PlottedModel[] getTrees() {
		return trees;
	}
}
