/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.memcache;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import de.cosmocode.junit.UnitProvider;
import de.cosmocode.palava.core.DefaultRegistryModule;
import de.cosmocode.palava.core.Framework;
import de.cosmocode.palava.core.Palava;
import de.cosmocode.palava.core.lifecycle.LifecycleException;
import de.cosmocode.palava.core.lifecycle.LifecycleModule;
import de.cosmocode.palava.scope.SingletonUnitOfWorkScopeModule;
import net.spy.memcached.MemcachedClientIF;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Properties;

public class MemcacheClientProviderTest implements UnitProvider<MemcacheClientProvider> {

    private final Framework framework = Palava.newFramework(new AbstractModule() {

        @Override
        protected void configure() {
            install(new LifecycleModule());
            install(new DefaultRegistryModule());
            install(new SingletonUnitOfWorkScopeModule());

            final String address = "127.0.0.1";
            final int port = 11213;

            // configure local JVM server
            bindConstant().annotatedWith(Names.named("local.memcache.server.verbose")).to(true);
            bindConstant().annotatedWith(Names.named("local.memcache.server.address")).to(address);
            bindConstant().annotatedWith(Names.named("local.memcache.server.port")).to(port);
            install(new MemcacheLocalServerModule());

                    // configure memcache client
                    bindConstant().annotatedWith(Names.named("memcache.addresses")).to(address + ":" + port);
            install(new MemcacheClientModule());
        }

    }, new Properties());

    @Before
    public void start() throws LifecycleException {
        framework.start();
    }

    @After
    public void stop() throws LifecycleException {
        framework.stop();
    }

    @Override
    public MemcacheClientProvider unit() {
        return framework.getInstance(MemcacheClientProvider.class);
    }

    @Test
    public void getMemcachedClientIF() {
        final MemcachedClientIF client = unit().get();
        Assert.assertNotNull(client);
    }

    @Test
    public void set() {
        unit().get().set("test", 0, new HashMap<String, String>());
    }

    @Test
    public void get() {
        final MemcachedClientIF client = unit().get();
        client.set("test", 0, "somevalue");
        Assert.assertEquals("somevalue", client.get("test"));
    }

    @Test
    public void getAfterSleep() throws InterruptedException {
        final MemcachedClientIF client = unit().get();
        client.set("test", 5, "somevalue");
        Thread.sleep(1000);
        Assert.assertEquals("somevalue", client.get("test"));
    }

}
