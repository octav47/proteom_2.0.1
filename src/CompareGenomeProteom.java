import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.util.ParserUtils;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;
import support.SubFuntions;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashSet;

/**
 * Created by Kir on 25.02.14.
 */
public class CompareGenomeProteom {
    public static double result;

    public static void main(String[] args) {
        result = -1;
        String typeCompare = "wuf";
        String file1;
        String file2;
        String a;
        String b;
        switch (args.length) {
            case 3:
                a = args[0];
                b = args[1];
                file1 = "GenomTreesNewicks/" + args[0] + ".txt";
                file2 = "ProteomTreesNewicks/" + args[1] + ".txt";
                typeCompare = args[2];
                break;
            default:
//                file1 = "GenomTreeNewicks/BD_D1.txt";
//                file2 = "ProteomTreesNewicks/679.txt";
                a = "";
                b = "";
                file1 = "tmp/out.4";
                file2 = "tmp/out.1";
                break;
        }
        try {
            PhylogenyParser parser = ParserUtils.createParserDependingOnFileType(new File(file1), true);
            Phylogeny phy1 = PhylogenyMethods.readPhylogenies(parser, new File(file1))[0]; //genome
            parser = ParserUtils.createParserDependingOnFileType(new File(file2), true);
            Phylogeny phy2 = PhylogenyMethods.readPhylogenies(parser, new File(file2))[0]; //proteom
            LinkedHashSet<PhylogenyNode> intersectedNodes;

            phy2 = SubFuntions.normalizeTree(phy2);

            System.out.println("genome.getNodeCount() = " + phy1.getNodeCount());
            System.out.println("proteom.getNodeCount() = " + phy2.getNodeCount());
            //итератор по протеомному дереву, потому что у него больше веток
            LinkedHashSet<PhylogenyNode> phy1_nodes = new LinkedHashSet<>();
            LinkedHashSet<PhylogenyNode> phy2_nodes = new LinkedHashSet<>();
            for (PhylogenyNodeIterator it = phy2.iteratorPostorder(); it.hasNext(); ) {
                PhylogenyNode n = it.next();
//                try {
//                    String tmp = phy1.getNode(n.toString()).toString();
//                    intersectedNodes.add(phy1.getNode(n.toString()));
//                } catch (Exception ignored) {
//                }
                if (!phy2_nodes.contains(n)) {
                    phy2_nodes.add(n);
                }
            }

            for (PhylogenyNodeIterator it = phy1.iteratorPostorder(); it.hasNext(); ) {
                PhylogenyNode n = it.next();
//                try {
//                    intersectedNodes.add(phy2.getNode(n.toString()));
//                } catch (Exception ignored) {
//                }
                if (!phy1_nodes.contains(n)) {
                    phy1_nodes.add(n);
                }
            }

            intersectedNodes = SubFuntions.intersectLHS(phy1_nodes, phy2_nodes);

//            System.out.println("first run $ intersectedNodes.size() = " + intersectedNodes.size());
//            for (PhylogenyNodeIterator it = phy1.iteratorPostorder(); it.hasNext(); ) {
//                PhylogenyNode n = it.next();
//                try {
//                    String tmp = phy2.getNode(n.toString()).toString();
//                    intersectedNodes.add(phy2.getNode(n.toString()));
//                } catch (Exception ignored) {
//                }
//            }
//            System.out.println("second run $ intersectedNodes.size() = " + intersectedNodes.size());
            for (PhylogenyNodeIterator it = phy1.iteratorPostorder(); it.hasNext(); ) {
                PhylogenyNode n = it.next();
                if (!intersectedNodes.contains(n)) {
                    phy1.deleteSubtree(n, false);
                }
            }
            int k1 = phy1.getNodeCount();
            for (PhylogenyNodeIterator it = phy2.iteratorPostorder(); it.hasNext(); ) {
                PhylogenyNode n = it.next();
                if (!intersectedNodes.contains(n)) {
                    phy2.deleteSubtree(n, false);
                }
            }
            int k2 = phy2.getNodeCount();

            PrintWriter pw = new PrintWriter("tmp30/_tmpCompareGenomeProteom.1");
            pw.println(phy1.toString());
            pw.close();
            pw = new PrintWriter("tmp30/_tmpCompareGenomeProteom.2");
            pw.print(phy2.toString());
            pw.close();
            pw = null;
            if (typeCompare.equalsIgnoreCase("wuf")) {
                WeightedUniFracCompare.main(new String[]{a, b, "-g"});
                result = WeightedUniFracCompare.result;
            } else {
                UnWeightedUniFracCompare.main(new String[]{a, b, "-g"});
                result = UnWeightedUniFracCompare.result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
