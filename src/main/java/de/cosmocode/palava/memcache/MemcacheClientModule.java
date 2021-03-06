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

import net.spy.memcached.MemcachedClientIF;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import de.cosmocode.palava.ipc.Current;
import de.cosmocode.palava.scope.UnitOfWork;

/**
 * Binds {@link MemcachedClientIF} to {@link MemcacheClientProvider}.
 * 
 * @author Tobias Sarnowski
 */
public final class MemcacheClientModule implements Module {

    @Override
    public void configure(Binder binder) {
        final Class<? extends Provider<MemcachedClientIF>> provider = MemcacheClientProvider.class;
        binder.bind(MemcacheClientProvider.class).in(Singleton.class);
        binder.bind(MemcachedClientIF.class).annotatedWith(Current.class).toProvider(provider).in(UnitOfWork.class);
    }
    
}
