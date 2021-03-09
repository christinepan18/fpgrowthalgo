package fpgrowth;

import java.util.*;

public class ItemSet {
    public final int[] items;
    public final int support;

    public ItemSet(int[] items, int support) {
        this.items = items;
        this.support = support;
    }

    public boolean equals(Object o) {
        if (o instanceof ItemSet) {
            ItemSet a = (ItemSet) o;

            if (support != a.support)
                return false;

            if (items.length != a.items.length)
                return false;

            for (int i = 0; i < items.length; i++)
                if (items[i] != a.items[i])
                    return false;

            return true;
        }
        return false;
    }

    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Arrays.hashCode(this.items);
        hash = 23 * hash + this.support;
        return hash;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int item : items) {
            sb.append(item);
            sb.append(' ');
        }

        sb.append('(');
        sb.append(support);
        sb.append(')');
        return sb.toString();
    }
}