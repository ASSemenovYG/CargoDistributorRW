package ru.liga.cargodistributor.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LruCacheTest {
    private LruCache<String, Integer> lruCache;

    @BeforeEach
    public void setUp() {
        lruCache = new LruCache<>(3); // установим ёмкость кэша равной 3
    }

    @Test
    public void testCachePutAndGet() {
        lruCache.put("key1", 100);
        lruCache.put("key2", 200);
        assertThat(lruCache.get("key1")).isEqualTo(100);
        assertThat(lruCache.get("key2")).isEqualTo(200);
    }

    @Test
    public void testCacheEvictionPolicy() {
        lruCache.put("key1", 100);
        lruCache.put("key2", 200);
        lruCache.put("key3", 300);
        lruCache.put("key4", 400); // должно привести к удалению "key1"

        assertThat(lruCache.get("key1")).isNull();
        assertThat(lruCache.get("key4")).isEqualTo(400);
        assertThat(lruCache.get("key2")).isEqualTo(200);
        assertThat(lruCache.get("key3")).isEqualTo(300);
    }

    @Test
    public void testCacheOrderAfterAccess() {
        lruCache.put("key1", 100);
        lruCache.put("key2", 200);
        lruCache.put("key3", 300);

        lruCache.get("key1"); // обращение к "key1", он становится последним использованным
        lruCache.put("key4", 400); // должно привести к удалению "key2"

        assertThat(lruCache.get("key2")).isNull();
        assertThat(lruCache.get("key1")).isEqualTo(100);
    }

    @Test
    public void testCacheUpdateValue() {
        lruCache.put("key1", 100);
        lruCache.put("key1", 101);

        assertThat(lruCache.get("key1")).isEqualTo(101);
    }

    @Test
    public void testCacheNullKey() {
        lruCache.put(null, 500);
        assertThat(lruCache.get(null)).isEqualTo(500);
    }

    @Test
    public void testCacheNullValue() {
        lruCache.put("key5", null);
        assertThat(lruCache.get("key5")).isNull();
    }
}