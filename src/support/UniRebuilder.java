package support;

import java.io.*;
import java.util.LinkedHashSet;

public class UniRebuilder {
    public static void main(String[] args) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
            double[][] a = new double[1000][1000];
            String curBf;
            int maxi = 0;
            int maxj = 0;
            LinkedHashSet<Integer> exps = new LinkedHashSet<>();
            while ((curBf = bufferedReader.readLine()) != null) {
                String[] ch = curBf.split("\t");
                int i = Integer.parseInt(ch[0]);
                int j = Integer.parseInt(ch[1]);
                exps.add(i);
                exps.add(j);
                a[i][j] = Double.parseDouble(ch[2]);
                if (i > maxi) {
                    maxi = i;
                }
                if (j > maxj) {
                    maxj = j;
                }
            }
            System.out.println(maxi);
            System.out.println(maxj);
            PrintWriter pw = new PrintWriter(args[0] + "_res.txt");
            pw.print("name ");
            for (Integer i : exps) {
                pw.print("distTo_" + i + " ");
            }
            pw.println();
            for (Integer i : exps) {
                //            if (exps.contains(i)) {
                pw.print(i + " ");
                for (Integer j : exps) {
                    //                    if (exps.contains(j))
                    pw.print(a[i][j] + " ");
                }
                pw.println();
                //            }
            }
            pw.close();
        } catch (IOException e) {
            System.out.println(e.toString());
            System.exit(0);
        }
    }
}