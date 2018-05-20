package cofh.cofhworld.parser.distribution.base;

import cofh.cofhworld.parser.GeneratorData;
import cofh.cofhworld.parser.IDistributionParser;
import cofh.cofhworld.parser.variables.NumberData;
import cofh.cofhworld.util.WeightedRandomBlock;
import cofh.cofhworld.util.exceptions.InvalidGeneratorException;
import cofh.cofhworld.util.numbers.INumberProvider;
import cofh.cofhworld.world.IConfigurableFeatureGenerator;
import cofh.cofhworld.world.distribution.Distribution;
import com.typesafe.config.Config;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.logging.log4j.Logger;

import java.util.List;

public abstract class AbstractDistParser implements IDistributionParser {

	private final String[] FIELDS = new String[] { "generator", "cluster-count" };

	protected final List<WeightedRandomBlock> defaultMaterial;

	protected AbstractDistParser() {

		defaultMaterial = generateDefaultMaterial();
	}

	@Override
	public String[] getRequiredFields() {

		return FIELDS;
	}

	protected abstract List<WeightedRandomBlock> generateDefaultMaterial();

	@Override
	public final Distribution getFeature(String featureName, Config genObject, IConfigurableFeatureGenerator.GenRestriction biomeRes, boolean retrogen, IConfigurableFeatureGenerator.GenRestriction dimRes, Logger log) {

		INumberProvider numClusters = NumberData.parseNumberValue(genObject.getValue("cluster-count"), 0, Long.MAX_VALUE);

		WorldGenerator generator;
		try {
			generator = GeneratorData.parseGenerator(getDefaultGenerator(), genObject, defaultMaterial);
		} catch (InvalidGeneratorException e) {
			log.warn("Invalid generator for '{}' on line {}!", featureName, e.origin().lineNumber());
			return null;
		}

		return getFeature(featureName, genObject, generator, numClusters, biomeRes, retrogen, dimRes, log);
	}

	protected abstract Distribution getFeature(String featureName, Config genObject, WorldGenerator gen, INumberProvider numClusters,
			IConfigurableFeatureGenerator.GenRestriction biomeRes, boolean retrogen, IConfigurableFeatureGenerator.GenRestriction dimRes, Logger log);

	protected String getDefaultGenerator() {

		return "cluster";
	}
}