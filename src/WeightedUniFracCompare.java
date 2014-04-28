import org.apache.commons.io.FileUtils;
import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.util.ParserUtils;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;
import support.SubFuntions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Kir on 17.02.14.
 */
public class WeightedUniFracCompare {
    private static double EPS = 0.00000001;
    public static double result;

    public static void main(String[] args) throws IOException {
        result = -1;
        String a = args[0];
        String b = args[1];
        boolean genomeAnalysis = false;
        if (args.length == 3 && args[2].equalsIgnoreCase("-g")) {
            genomeAnalysis = true;
        }


        String path1 = (genomeAnalysis) ? "tmp30/_tmpCompareGenomeProteom.1" : "ProteomTreesNewicks/" + a + ".txt";
        String path2 = (genomeAnalysis) ? "tmp30/_tmpCompareGenomeProteom.2" : "ProteomTreesNewicks/" + b + ".txt";

        final File treefile1 = new File(path1);
        final File treefile2 = new File(path2);
//        final File treefile1 = new File("treeToCompare/" + "1" + ".txt");
//        final File treefile2 = new File("treeToCompare/" + "2" + ".txt");
        PhylogenyParser parser = ParserUtils.createParserDependingOnFileType(treefile1, true);
        final Phylogeny phy1 = PhylogenyMethods.readPhylogenies(parser, treefile1)[0];
        parser = ParserUtils.createParserDependingOnFileType(treefile2, true);
        final Phylogeny phy2 = PhylogenyMethods.readPhylogenies(parser, treefile2)[0];
        String compareResultFileName = (genomeAnalysis) ? "GPcompare/" + a + "_comp_" + b + ".txt" : "resultCompare/" + a + "_comp_" + b + ".txt";
        if (isNullTree(phy1) || isNullTree(phy2)) {
            System.out.println(a + " or " + b + " are null-trees");
            result = -1;
        } else {
            double ATotal = 0.0;
            double BTotal = 0.0;
            int s = 0;
            List<PhylogenyNode> leafs = phy1.getExternalNodes();
            double maxDepth = 0;
            for (PhylogenyNode node : leafs) {
                double curDepth = node.calculateDepth();
                if (curDepth > maxDepth) {
                    maxDepth = curDepth;
                }
            }
            System.out.println("maxDepth = " + maxDepth);
            double u = 0.0;
            double L = 0.0;
            for (PhylogenyNodeIterator it = phy1.iteratorPostorder(); it.hasNext(); ) {
                PhylogenyNode n1 = it.next();
                try {
                    PhylogenyNode n2 = phy2.getNode(n1.toString());
                    ATotal += n1.getDistanceToParent();
                    BTotal += n2.getDistanceToParent();
                    if (n1.getDistanceToParent() != 0) {
                        s++;
                        L += n1.calculateDistanceToRoot();
                    }
                    if (n2.getDistanceToParent() != 0) {
                        s++;
                        L += n2.calculateDistanceToRoot();
                    }
                } catch (Exception e) {
                    SubFuntions.error(2);
                }
            }
            System.out.println("ATotal = " + ATotal);
            System.out.println("BTotal = " + BTotal);
            for (PhylogenyNodeIterator it = phy1.iteratorPostorder(); it.hasNext(); ) {
                PhylogenyNode n1 = it.next();
                try {
                    PhylogenyNode n2 = phy2.getNode(n1.toString());
                    double d = maxDepth - n1.calculateDepth() + 1;
                    double Ai = n1.getDistanceToParent();
                    double Bi = n2.getDistanceToParent();
                    double A_T = Ai / ATotal;
                    double B_T = Bi / BTotal;
                    u += d * Math.abs(A_T - B_T);
                } catch (Exception e) {
                    SubFuntions.error(2);
                }
            }
            try {
                if (genomeAnalysis) {
                    FileUtils.forceDelete(new File("GPcompare/" + a + "_comp_" + b + ".txt"));
                } else {
                    FileUtils.forceDelete(new File("resultCompare/" + a + "_comp_" + b + ".txt"));
                }
            } catch (IOException ignored) {

            }
//        System.out.println(u);
            u /= L;
            System.out.println(u);
            result = u;
            try {
                printResult(compareResultFileName, u);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static void printResult(String fileName, double u) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(fileName);
        pw.print(u);
        pw.close();
    }

    private static boolean isNullTree(Phylogeny phy) {
        for (PhylogenyNodeIterator it = phy.iteratorPostorder(); it.hasNext(); ) {
            if (it.next().getDistanceToParent() > EPS) {
                return false;
            }
        }
        return true;
    }
}
