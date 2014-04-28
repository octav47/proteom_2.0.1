package support;

import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;

/**
 * Created by Kir on 04.02.14.
 */
public class SubFuntions {
    public static ArrayList<Integer> getGradient() throws IOException {
        ArrayList<Integer> res = new ArrayList<>();
        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream("lib/thn.bin")));
        String curString;
        while ((curString = bf.readLine()) != null) {
            curString = curString.replace(" ", "");
            int curRGB = Integer.parseInt(curString);
            res.add(curRGB);
        }
        return res;
    }

    public static String[] getGenomExperiments() {
        String[] res = new String[13];
        res[0] = "BD_A4";
        res[1] = "BD_BD";
        res[2] = "BD_ChK";
        res[3] = "BD_ChP";
        res[4] = "BD_CK";
        res[5] = "BD_CL";
        res[6] = "BD_D1";
        res[7] = "BD_D2";
        res[8] = "BD_M1";
        res[9] = "BD_M2";
        res[10] = "BD_PK";
        res[11] = "BD_RK";
        res[12] = "BD_RP";
        return res;
    }

    public static Phylogeny normalizeTree(Phylogeny phy) {
        double sum = 0.0;
        for (PhylogenyNodeIterator it = phy.iteratorPreorder(); it.hasNext(); ) {
            PhylogenyNode n = it.next();
            sum += n.getDistanceToParent();
        }
        for (PhylogenyNodeIterator it = phy.iteratorPreorder(); it.hasNext(); ) {
            PhylogenyNode n = it.next();
            double curWeight = n.getDistanceToParent();
            double normalizeWeight = (curWeight * 100) / sum;
            n.setDistanceToParent(normalizeWeight);
        }
        return phy;
    }

    public static LinkedHashSet<PhylogenyNode> intersectLHS(LinkedHashSet<PhylogenyNode> a, LinkedHashSet<PhylogenyNode> b) {
        LinkedHashSet<PhylogenyNode> c = new LinkedHashSet<>();
        for (PhylogenyNode n : a) {
            if (b.contains(n)) {
                c.add(n);
            }
        }
        for (PhylogenyNode n : b) {
            if (a.contains(n)) {
                c.add(n);
            }
        }
        return c;
    }

    public static void error(int code) {
        switch (code) {
            case 1:
                System.out.println("can't delete tree node");
                break;
            case 2:
                System.out.println("can't find tree node");
            default:
                System.out.println("unknown error");
                break;
        }
    }
}
