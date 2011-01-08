package de.cosmocode.palava.memcache;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.ConnectionFactory;
import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.HashAlgorithm;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.MemcachedClientIF;
import net.spy.memcached.transcoders.BaseSerializingTranscoder;
import net.spy.memcached.transcoders.Transcoder;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;

/**
 * Provider for {@link MemcachedClientIF}.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class MemcacheClientProvider implements Provider<MemcachedClientIF>, Initializable {

    private final List<InetSocketAddress> addresses;
    
    private boolean binary;
    private int compressionThreshold = -1;
    private HashAlgorithm hashAlgorithm = HashAlgorithm.NATIVE_HASH;
    
    private ConnectionFactory factory;
    
    @Inject
    public MemcacheClientProvider(@Named(MemcacheClientConfig.ADRESSES) String addresses) {
        Preconditions.checkNotNull(addresses, "Addresses");
        this.addresses = AddrUtil.getAddresses(addresses);
    }

    @Inject(optional = true)
    void setBinary(@Named(MemcacheClientConfig.BINARY) boolean binary) {
        this.binary = binary;
    }
    
    @Inject(optional = true)
    void setCompressionThreshold(@Named(MemcacheClientConfig.COMPRESSION_THRESHOLD) int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }
    
    @Inject(optional = true)
    void setHashAlgorithm(@Named(MemcacheClientConfig.HASH_ALGORITHM) HashAlgorithm hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }
        
    @Override
    public void initialize() throws LifecycleException {
        if (binary) {
            factory = new BinaryConnectionFactory(
                BinaryConnectionFactory.DEFAULT_OP_QUEUE_LEN,
                BinaryConnectionFactory.DEFAULT_READ_BUFFER_SIZE,
                hashAlgorithm
            );
        } else {
            factory = new DefaultConnectionFactory(
                DefaultConnectionFactory.DEFAULT_OP_QUEUE_LEN,
                DefaultConnectionFactory.DEFAULT_READ_BUFFER_SIZE,
                hashAlgorithm
            );
        }
    }
    
    @Override
    public MemcachedClientIF get() {
        try {
            final MemcachedClient client = new MemcachedClient(factory, addresses);

            if (compressionThreshold >= 0) {
                final Transcoder<Object> transcoder = client.getTranscoder();
                if (transcoder instanceof BaseSerializingTranscoder) {
                    BaseSerializingTranscoder.class.cast(transcoder).setCompressionThreshold(compressionThreshold);
                } else {
                    throw new UnsupportedOperationException("Unable set compression threshold");
                }
            }

            return new DestroyableMemcachedClient(client);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
}
