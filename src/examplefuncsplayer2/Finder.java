package examplefuncsplayer2;

import battlecode.common.*;

// To keep track of the map
public class Finder {
    // sizes of map ranges between 20x20 and 60x60 inclusive
    final static int MAX_SIZE = 60;
    final static int BITS = 32;
    final static int ARRAY_SIZE = MAX_SIZE * MAX_SIZE / BITS;

    // keep track of visited squares
    static int[] visited = new int[ARRAY_SIZE];

    Finder() {
    }

    void reset () {
        visited = new int[ARRAY_SIZE];
    }

    void add(MapLocation loc) {
        int index = loc.x * MAX_SIZE + loc.y;
        int arrayIndex = index / BITS;
        int bitIndex = index % BITS;
        visited[arrayIndex] |= (1 << bitIndex);
    }

    boolean check(MapLocation loc) {
        int index = loc.x * MAX_SIZE + loc.y;
        int arrayIndex = index / BITS;
        int bitIndex = index % BITS;
        return (visited[arrayIndex] & (1 << bitIndex)) != 0;
    }

}
