package prgrms.neoike.common.cache;

public enum CacheType {
    DRAW("draws", 5, 10_000);

    private final String cacheName;
    private final int expiredMinuteAfterWrite;
    private final int maximumSize;

    CacheType(String cacheName, int minute, int maximumSize) {
        this.cacheName = cacheName;
        this.expiredMinuteAfterWrite = minute;
        this.maximumSize = maximumSize;
    }

    public String getCacheName() {
        return cacheName;
    }

    public int getExpiredMinuteAfterWrite() {
        return expiredMinuteAfterWrite;
    }

    public int getMaximumSize() {
        return maximumSize;
    }
}