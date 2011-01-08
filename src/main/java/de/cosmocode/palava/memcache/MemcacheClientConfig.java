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

/**
 * Static constant holder class for memcache config key names.
 * 
 * @author Tobias Sarnowski
 */
final class MemcacheClientConfig {

    public static final String PREFIX = "memcache.";

    /**
     * Format:  "host1:port1 host2:port2".
     */
    public static final String ADRESSES = PREFIX + "addresses";

    public static final String BINARY = PREFIX + "binary";

    public static final String COMPRESSION_THRESHOLD = PREFIX + "compressionThreshold";

    public static final String HASH_ALGORITHM = PREFIX + "hashAlgorithm";
    
    private MemcacheClientConfig() {
        
    }
    
}
