package fpgrowth;

public interface Sort {
    public static void swap(int[] x, int i, int j) {
        int a = x[i];
        x[i] = x[j];
        x[j] = a;
    }

    public static void siftUp(int[] x, int k) {
        while (k > 1 && x[k/2] < x[k]) {
            swap(x, k, k/2);
            k = k/2;
        }
    }

    public static void siftDown(int[] x, int k, int n) {
        while (2*k <= n) {
            int j = 2 * k;
            if (j < n && x[j] < x[j + 1]) {
                j++;
            }
            if (x[k] >= x[j]) {
                break;
            }
            swap(x, k, j);
            k = j;
        }
    }
}