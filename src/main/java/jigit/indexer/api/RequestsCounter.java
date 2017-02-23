package jigit.indexer.api;

public final class RequestsCounter {
    private long requestsQuantity = 0;

    public long value() {
        return requestsQuantity;
    }

    public void increase() {
        requestsQuantity++;
    }
}
