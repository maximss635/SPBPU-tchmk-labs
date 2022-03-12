package com.company;

import java.util.ArrayList;

public class SolveSLAE {
    public static Double[] solve(Double[][] a, Double[] b) {
        int N  = b.length;
        for (int p = 0; p < N; p++) {

            int max = p;
            for (int i = p + 1; i < N; i++) {
                if (Math.abs(a[i][p]) > Math.abs(a[max][p])) {
                    max = i;
                }
            }
            Double[] temp = a[p]; a[p] = a[max]; a[max] = temp;
            Double   t    = b[p]; b[p] = b[max]; b[max] = t;

            if (Math.abs(a[p][p]) <= 1e-10) {
                return null;
            }

            for (int i = p + 1; i < N; i++) {
                double alpha = a[i][p] / a[p][p];
                b[i] -= alpha * b[p];
                for (int j = p; j < N; j++) {
                    a[i][j] -= alpha * a[p][j];
                }
            }
        }

        // Обратный проход

        Double[] x = new Double[N];
        for (int i = N - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < N; j++) {
                sum += a[i][j] * x[j];
            }
            x[i] = (b[i] - sum) / a[i][i];
        }

        return x;
    }
}
