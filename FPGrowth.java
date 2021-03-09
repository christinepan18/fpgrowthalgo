package fpgrowth;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import fpgrowth.FPTree.HeaderTableItem;
import fpgrowth.FPTree.Node;

public class FPGrowth implements Iterable<ItemSet> {
    private final int minSupport;
    private final FPTree T0;
    private final Queue<ItemSet> buffer = new LinkedList<>();

    public FPGrowth(FPTree tree) {
        this.minSupport = tree.minSupport;
        T0 = tree;
    }

    public int size() {
        return T0.size();
    }

    public Iterator<ItemSet> iterator() {
        return new Iterator<ItemSet>() {
            final int[] prefixItemset = new int[T0.maxItemSetSize];
            final int[] localItemSupport = new int[T0.numItems];
            int i = T0.headerTable.length;

            public boolean hasNext() {
                if (buffer.isEmpty())
                    if (i-- > 0)
                        grow(T0.headerTable[i], null, localItemSupport, prefixItemset);
                return !buffer.isEmpty();
            }

            public ItemSet next() {
                return buffer.poll();
            }
        };
    }

    public static Stream<ItemSet> apply(FPTree tree) {
        FPGrowth growth = new FPGrowth(tree);
        return StreamSupport.stream(growth.spliterator(), false);
    }

    private void grow(FPTree fptree, int[] itemset, int[] localItemSupport, int[] prefixItemset) {
        for (int i = fptree.headerTable.length; i-- > 0;)
            grow(fptree.headerTable[i], itemset, localItemSupport, prefixItemset);
    }

    private void collect(int[] itemset, int support) {
        buffer.offer(new ItemSet(itemset, support));
    }


    private void grow(FPTree.Node node, int[] itemset, int support) {
        int height = 0;
        for (FPTree.Node currentNode = node; currentNode != null; currentNode = currentNode.parent)
            height ++;

        if (height > 0) {
            int[] items = new int[height];
            int i = 0;
            for (FPTree.Node currentNode = node; currentNode != null; currentNode = currentNode.parent)
                items[i ++] = currentNode.id;

            int[] itemIndexStack = new int[height];
            int itemIndexStackPos = 0;
            itemset = insert(itemset, items[itemIndexStack[itemIndexStackPos]]);
            collect(itemset, support);

            while (itemIndexStack[0] < height - 1) {
                if (itemIndexStack[itemIndexStackPos] < height - 1) {
                    itemIndexStackPos ++;
                    itemIndexStack[itemIndexStackPos] = itemIndexStack[itemIndexStackPos - 1] + 1;
                    itemset = insert(itemset, items[itemIndexStack[itemIndexStackPos]]);
                    collect(itemset, support);
                }

                else {
                    itemset = drop(itemset);
                    if (itemset != null) {
                        itemIndexStackPos --;
                        itemIndexStack[itemIndexStackPos] = itemIndexStack[itemIndexStackPos] + 1;
                        itemset[0] = items[itemIndexStack[itemIndexStackPos]];
                        collect(itemset, support);
                    }
                }
            }
        }
    }

    private void grow(HeaderTableItem header, int[] itemset, int[] localItemSupport, int[] prefixItemset) {
        int support = header.count;
        int item = header.id;
        itemset = insert(itemset, item);

        collect(itemset, support);

        if (header.node.next == null) {
            FPTree.Node node = header.node;
            grow(node.parent, itemset, support);
        }
        else {
            if (getLocalItemSupport(header.node, localItemSupport)) {
                FPTree fptree = getLocalFPTree(header.node, localItemSupport, prefixItemset);
                grow(fptree, itemset, localItemSupport, prefixItemset);
            }
        }
    }

    private boolean getLocalItemSupport(FPTree.Node node, int[] localItemSupport) {
        boolean end = true;
        Arrays.fill(localItemSupport, 0);

        while (node != null) {
            int support = node.count;
            Node parent = node.parent;

            while (parent != null) {
                localItemSupport[parent.id] += support;
                parent = parent.parent;
                end = false;
            }
            node = node.next;
        }

        return !end;
    }

    private FPTree getLocalFPTree(FPTree.Node node, int[] localItemSupport, int[] prefixItemset) {
        FPTree tree = new FPTree(minSupport, localItemSupport);

        while (node != null) {
            Node parent = node.parent;
            int i = prefixItemset.length;
            while (parent != null) {
                if (localItemSupport[parent.id] >= minSupport) {
                    prefixItemset[--i] = parent.id;
                }
                parent = parent.parent;
            }

            if (i < prefixItemset.length) {
                tree.add(i, prefixItemset.length, prefixItemset, node.count);
            }

            node = node.next;
        }

        return tree;
    }

    public static int[] insert(int[] itemset, int item) {
        if (itemset == null) {
            return new int[]{item};

        } else {
            int n = itemset.length + 1;
            int[] newItemset = new int[n];

            newItemset[0] = item;
            System.arraycopy(itemset, 0, newItemset, 1, n - 1);

            return newItemset;
        }
    }

    private static int[] drop(int[] itemset) {
        if (itemset.length >= 1) {
            int n = itemset.length - 1;
            int[] newItemset = new int[n];

            System.arraycopy(itemset, 1, newItemset, 0, n);

            return newItemset;
        }
        else
            return null;
    }
}