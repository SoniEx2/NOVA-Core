/*
 * Copyright (c) 2015 NOVA, All rights reserved.
 * This library is free software, licensed under GNU Lesser General Public License version 3
 *
 * This file is part of NOVA.
 *
 * NOVA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NOVA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NOVA.  If not, see <http://www.gnu.org/licenses/>.
 */

package nova.core.wrapper.mc.forge.v17.launcher;

import nova.core.wrapper.mc.forge.v17.depmodules.ClientModule;
import nova.core.wrapper.mc.forge.v17.depmodules.GameInfoModule;
import nova.core.wrapper.mc.forge.v17.depmodules.KeyModule;
import nova.core.wrapper.mc.forge.v17.depmodules.LanguageModule;
import nova.core.wrapper.mc.forge.v17.depmodules.RenderModule;
import nova.core.wrapper.mc.forge.v17.depmodules.SaveModule;
import nova.core.wrapper.mc.forge.v17.depmodules.TickerModule;
import nova.internal.core.Game;
import nova.internal.core.bootstrap.DependencyInjectionEntryPoint;
import nova.wrappertests.depmodules.FakeNetworkModule;
import org.junit.Test;
import se.jbee.inject.bootstrap.Bundle;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rx14
 */
public class NovaLauncherTest extends nova.wrappertests.NovaLauncherTest {

	@Override
	public List<Class<? extends Bundle>> getModules() {
		return Arrays.<Class<? extends Bundle>>asList(
			ClientModule.class,
			KeyModule.class,
			LanguageModule.class,
			FakeNetworkModule.class, //NetworkManager calls into FML code in the class instantiation, so we create a fake.
			RenderModule.class,
			SaveModule.class,
			TickerModule.class,
			GameInfoModule.class
		);
	}

	@Test
	public void testLaunching() {
		doLaunchAssert(createLauncher());
	}

	@Test
	public void testResolveGame() {
		DependencyInjectionEntryPoint diep = new DependencyInjectionEntryPoint();

		getModules().forEach(diep::install);

		Game game = diep.init();

		assertThat(game).isNotNull();
	}
}
