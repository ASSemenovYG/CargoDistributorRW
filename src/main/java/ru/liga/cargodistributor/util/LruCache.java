package ru.liga.cargodistributor.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Класс представляет собой реализацию кэша, который удаляет наименее недавно использованные элементы,
 * когда достигнута его максимальная ёмкость.
 * The Least Recently Used (LRU)
 */
public class LruCache<K, T> {
    private final Map<K, T> cache;
    private final int capacity;
    private final LinkedList<K> usageHistory;

    /**
     * Конструктор для создания кэша с определённой ёмкостью.
     *
     * @param capacity Максимальная ёмкость кэша.
     */
    public LruCache(/*@Value("${cache.capacity}")*/ int capacity) {
        this.cache = new HashMap<>(capacity);
        this.capacity = capacity;
        this.usageHistory = new LinkedList<>();
    }

    /**
     * Получает значение по ключу из кэша.
     *
     * @param key Ключ, по которому нужно получить значение.
     * @return Значение, соответствующее ключу, или {@code null}, если ключ не найден.
     */
    public T get(K key) {
        if (!cache.containsKey(key)) {
            return null;
        }
        this.moveKeyToTop(key);
        return cache.get(key);
    }

    /**
     * Добавляет пару ключ-значение в кэш. Если кэш достигает своей максимальной ёмкости,
     * наименее недавно использованный элемент удаляется.
     *
     * @param key   Ключ для добавляемого значения.
     * @param value Значение, которое нужно добавить.
     */
    public void put(K key, T value) {
        cache.put(key, value);
        if (usageHistory.contains(key)) {
            this.moveKeyToTop(key);
        } else {
            this.addNewKeyAndRemoveLeastUsed(key);
        }
    }

    /**
     * Перемещает ключ использованного элемента наверх в usageHistory
     *
     * @param key Ключ для добавляемого/извлекаемого значения.
     */
    private void moveKeyToTop(K key) {
        usageHistory.remove(key);
        usageHistory.addFirst(key);
    }

    /**
     * Добавляет новый ключ в начало usageHistory и удаляет из кеша самый старый использованный элемент при переполнении ёмкости
     *
     * @param key Ключ для добавляемого значения.
     */
    private void addNewKeyAndRemoveLeastUsed(K key) {
        if (capacity - usageHistory.size() > 0) usageHistory.addFirst(key);
        else {
            cache.remove(usageHistory.removeLast());
            usageHistory.addFirst(key);
        }
    }
}
