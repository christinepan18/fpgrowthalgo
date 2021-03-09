package fpgrowth;

import java.util.Comparator;

public class QuickSort {
    private QuickSort() {

    }

    private static final int M = 7;
    private static final int NSTACK = 64;

    public static void sort(int[] x, int[] y, int n) {
        int jstack = -1;
        int l = 0;
        int[] istack = new int[NSTACK];
        int ir = n - 1;

        int i, j, k, a, b;
        for (;;) {
            if (ir - l < M) {

                for (j = l + 1; j <= ir; j++) {
                    a = x[j];
                    b = y[j];

                    for (i = j - 1; i >= l; i--) {
                        if (x[i] <= a)
                            break;

                        x[i + 1] = x[i];
                        y[i + 1] = y[i];
                    }

                    x[i + 1] = a;
                    y[i + 1] = b;
                }

                if (jstack < 0)
                    break;

                ir = istack[jstack--];
                l = istack[jstack--];
            }
            else {
                k = (l + ir) >> 1;
                Sort.swap(x, k, l + 1);
                Sort.swap(y, k, l + 1);

                if (x[l] > x[ir]) {
                    Sort.swap(x, l, ir);
                    Sort.swap(y, l, ir);
                }

                if (x[l + 1] > x[ir]) {
                    Sort.swap(x, l + 1, ir);
                    Sort.swap(y, l + 1, ir);
                }
                if (x[l] > x[l + 1]) {
                    Sort.swap(x, l, l + 1);
                    Sort.swap(y, l, l + 1);
                }

                i = l + 1;
                j = ir;
                a = x[l + 1];
                b = y[l + 1];
                for (;;) {
                    do
                        i++;
                    while (x[i] < a);

                    do
                        j--;
                    while (x[j] > a);

                    if (j < i) {
                        break;
                    }
                    Sort.swap(x, i, j);
                    Sort.swap(y, i, j);
                }
                x[l + 1] = x[j];
                x[j] = a;
                y[l + 1] = y[j];
                y[j] = b;
                jstack += 2;

                if (jstack >= NSTACK) {
                    throw new IllegalStateException("NSTACK too small in sort.");
                }

                if (ir - i + 1 >= j - l) {
                    istack[jstack] = ir;
                    istack[jstack - 1] = i;
                    ir = j - 1;
                }

                else {
                    istack[jstack] = j - 1;
                    istack[jstack - 1] = l;
                    l = i;
                }
            }
        }
    }
}
