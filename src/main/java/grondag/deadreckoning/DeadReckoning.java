/*******************************************************************************
 * Copyright 2019 grondag
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package grondag.deadreckoning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.loader.api.FabricLoader;

public class DeadReckoning {
	public static Logger LOG = LogManager.getLogger("Dead Reckoning");


	static {
		final File configDir = FabricLoader.getInstance().getConfigDirectory();
		if (!configDir.exists()) {
			LOG.warn("[Darkness] Could not access configuration directory: " + configDir.getAbsolutePath());
		}

		final File configFile = new File(configDir, "darkness.properties");
		final Properties properties = new Properties();
		if (configFile.exists()) {
			try (FileInputStream stream = new FileInputStream(configFile)) {
				properties.load(stream);
			} catch (final IOException e) {
				LOG.warn("[Darkness] Could not read property file '" + configFile.getAbsolutePath() + "'", e);
			}
		}

		//		darkOverworld = properties.computeIfAbsent("dark_overworld", (a) -> "true").equals("true");
		//		darkDefault = properties.computeIfAbsent("dark_default", (a) -> "true").equals("true");
		//		darkNether = properties.computeIfAbsent("dark_nether", (a) -> "true").equals("true");
		//		darkEnd = properties.computeIfAbsent("dark_end", (a) -> "true").equals("true");
		//		darkSkyless = properties.computeIfAbsent("dark_skyless", (a) -> "true").equals("true");


		try (FileOutputStream stream = new FileOutputStream(configFile)) {
			properties.store(stream, "Darkness properties file");
		} catch (final IOException e) {
			LOG.warn("[Indigo] Could not store property file '" + configFile.getAbsolutePath() + "'", e);
		}
	}

}
