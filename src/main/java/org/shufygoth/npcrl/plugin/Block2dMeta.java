package org.shufygoth.npcrl.plugin;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record Block2dMeta(int x, int z) implements Comparable<Block2dMeta> {
    @Override
    public int compareTo(@NotNull Block2dMeta o) {
        if (x != o.x)
            return Integer.compare(x, o.x);
        return Integer.compare(z, o.z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Block2dMeta that = (Block2dMeta) o;
        return x == that.x && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }
}
