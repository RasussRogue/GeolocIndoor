package com.arxit.geolocindoor.dpo;

public class GraphEdgeDpo {

    private final long startId;
    private final long endId;

    public GraphEdgeDpo(long startId, long endId) {
        this.startId = startId;
        this.endId = endId;
    }

    public long getStartId() {
        return startId;
    }

    public long getEndId() {
        return endId;
    }
}
