package fpgrowth;

import java.util.*;
import java.util.stream.Stream;
import java.util.function.Supplier;

public class FPTree {

    public class Node {
        int id = -1;
        int count = 0;
        Node parent = null;
        Node next = null;
        HashMap<Integer, Node> children = null;

        public Node() {
        }

        public Node(int id, int support, Node parent) {
            this.id = id;
            this.count = support;
            this.parent = parent;
        }

        public void add(int index, int end, int[] itemset, int support) {
            if (children == null) {
                children = new HashMap<>();
            }

            Node child = children.get(itemset[index]);
            if (child != null) {
                child.count += support;
                if (++index < end)
                    child.add(index, end, itemset, support);

                else
                    append(index, end, itemset, support);
            }
        }

        public void append(int index, int end, int[] itemset, int support) {
            if (children == null) {
                children = new HashMap<>();
            }

            if (index >= maxItemSetSize) {
                maxItemSetSize = index + 1;
            }

            int item = itemset[index];
            Node child = new Node(item, support, id < 0 ? null : this);
            child.addToHeaderTable();
            children.put(item, child);
            if (++index < end)
                child.append(index, end, itemset, support);
        }

        public void addToHeaderTable() {
            next = headerTable[order[id]].node;
            headerTable[order[id]].node = this;
        }
    }

    public static class HeaderTableItem implements Comparable<HeaderTableItem> {
        int id;
        int count = 0;
        Node node = null;

        public HeaderTableItem(int id) {
            this.id = id;
        }

        public int compareTo(HeaderTableItem o) {
            return Integer.compare(o.count, count);
        }
    }

    int numTransactions = 0;
    int minSupport;
    Node root = new Node();
    int[] itemSupport;
    HeaderTableItem[] headerTable;
    int numItems = 0;
    int numFreqItems = 0;
    int maxItemSetSize = -1;
    int[] order;

    public FPTree(int minSupport, int[] itemSupport) {
        this.itemSupport = itemSupport;
        this.minSupport = minSupport;
        init();
    }

    public FPTree(int minSupport, Stream<int[]> itemsets) {
        this.itemSupport = freq(itemsets);
        this.minSupport = minSupport;
        init();
    }

    public FPTree(double minSupport, Stream<int[]> itemsets) {
        this.itemSupport = freq(itemsets);
        this.minSupport = (int) Math.round(minSupport * numTransactions);
        init();
    }

    private void init() {
        numItems = itemSupport.length;
        for (int f : itemSupport)
            if (f >= minSupport)
                numFreqItems++;

        headerTable = new HeaderTableItem[numFreqItems];
        for (int i = 0, j = 0; i < numItems; i++) {
            if (itemSupport[i] >= minSupport) {
                HeaderTableItem header = new HeaderTableItem(i);
                header.count = itemSupport[i];
                headerTable[j++] = header;
            }
        }

        Arrays.sort(headerTable);
        order = new int[numItems];
        Arrays.fill(order, numItems);
        for (int i = 0; i < numFreqItems; i++)
            order[headerTable[i].id] = i;
    }

    private int[] freq(Stream<int[]> itemsets) {
        int n = Integer.parseInt(System.getProperty("smile.arm.items", "65536"));
        int[] f = new int[n];
        itemsets.forEach(itemset -> {
            numTransactions++;
            for (int i : itemset) f[i]++;
        });
        while (f[--n] == 0);
        return Arrays.copyOf(f, n+1);
    }

    public static FPTree of(int minSupport, Supplier<Stream<int[]>> supplier) {
        FPTree tree = new FPTree(minSupport, supplier.get());
        tree.add(supplier.get());
        return tree;
    }

    public static FPTree of(double minSupport, Supplier<Stream<int[]>> supplier) {
        FPTree tree = new FPTree(minSupport, supplier.get());
        tree.add(supplier.get());
        return tree;
    }

    public static FPTree of(int minSupport, int[][] itemsets) {
        FPTree tree = new FPTree(minSupport, Arrays.stream(itemsets));
        tree.add(Arrays.stream(itemsets));
        return tree;
    }

    public static FPTree of(double minSupport, int[][] itemsets) {
        FPTree tree = new FPTree(minSupport, Arrays.stream(itemsets));
        tree.add(Arrays.stream(itemsets));
        return tree;
    }

    public int size() {
        return numTransactions;
    }

    public int minSupport() {
        return minSupport;
    }

    private void add(Stream<int[]> itemsets) {
        itemsets.forEach(this::add);
    }

    private void add(int[] itemset) {
        int m = 0;
        int t = itemset.length;
        int[] o = new int[t];
        for (int i = 0; i < t; i++) {
            int item = itemset[i];
            o[i] = order[item];
            if (itemSupport[item] >= minSupport) {
                m++;
            }
        }

        if (m > 0) {
            QuickSort.sort(o, itemset, t);

            for (int i = 1; i < m; i++) {
                if (itemset[i] == itemset[i-1]) {
                    m--;
                    for (int j = i; j < m; j++)
                        itemset[j] = itemset[j+1];
                }
            }

            root.add(0, m, itemset, 1);
        }
    }

    public void add(int index, int end, int[] itemset, int support) {
        root.add(index, end, itemset, support);
    }
}
