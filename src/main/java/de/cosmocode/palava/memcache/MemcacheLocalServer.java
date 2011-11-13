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

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.thimbleware.jmemcached.CacheImpl;
import com.thimbleware.jmemcached.Key;
import com.thimbleware.jmemcached.LocalCacheElement;
import com.thimbleware.jmemcached.MemCacheDaemon;
import com.thimbleware.jmemcached.storage.CacheStorage;
import com.thimbleware.jmemcached.storage.hash.ConcurrentLinkedHashMap;
import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;

import java.net.InetSocketAddress;

/**
 * A memcache server that runs in the JVM, provided by jmemcached.<br />
 * Google code page:
 * <a href="https://code.google.com/p/jmemcache-daemon/">https://code.google.com/p/jmemcache-daemon/</a>.
 *
 * @since 1.2
 */
final class MemcacheLocalServer implements Initializable, Disposable {

    private boolean binary;
    private String address = "127.0.0.1";
    private int port = 11211;
    private int idleTime = 10;
    private boolean verbose;
    private int maxItems = 10000;
    private long maxBytes = 1024 * 1024;
    private ConcurrentLinkedHashMap.EvictionPolicy evictionPolicy = ConcurrentLinkedHashMap.EvictionPolicy.LRU;

    private final MemCacheDaemon<LocalCacheElement> daemon = new MemCacheDaemon<LocalCacheElement>();

    @Inject(optional = true)
    public void setBinary(@Named("local.memcache.server.binary") boolean binary) {
        this.binary = binary;
    }

    @Inject(optional = true)
    public void setAddress(@Named("local.memcache.server.address") String address) {
        this.address = address;
    }

    @Inject(optional = true)
    public void setPort(@Named("local.memcache.server.port") int port) {
        this.port = port;
    }

    @Inject(optional = true)
    public void setIdleTime(@Named("local.memcache.server.idleTime") int idleTime) {
        this.idleTime = idleTime;
    }

    @Inject(optional = true)
    public void setVerbose(@Named("local.memcache.server.verbose") boolean verbose) {
        this.verbose = verbose;
    }

    @Inject(optional = true)
    public void setEvictionPolicy(@Named("local.memcache.server.evictionPolicy")
                                  ConcurrentLinkedHashMap.EvictionPolicy evictionPolicy) {
        this.evictionPolicy = evictionPolicy;
    }

    @Inject(optional = true)
    public void setMaxItems(@Named("local.memcache.server.maxItems") int maxItems) {
        this.maxItems = maxItems;
    }

    @Inject(optional = true)
    public void setMaxBytes(@Named("local.memcache.server.maxBytes") long maxBytes) {
        this.maxBytes = maxBytes;
    }

    @Override
    public void initialize() throws LifecycleException {
        // start daemon
        final CacheStorage<Key, LocalCacheElement> storage =
                ConcurrentLinkedHashMap.create(evictionPolicy, maxItems, maxBytes);
        daemon.setCache(new CacheImpl(storage));
        daemon.setBinary(binary);
        daemon.setAddr(new InetSocketAddress(address, port));
        daemon.setIdleTime(idleTime);
        daemon.setVerbose(verbose);
        daemon.start();
    }

    @Override
    public void dispose() throws LifecycleException {
        daemon.stop();
    }
}
