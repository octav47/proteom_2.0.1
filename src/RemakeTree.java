import org.apache.commons.io.FileUtils;
import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.util.ParserUtils;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Kir on 28.02.14.
 */
public class RemakeTree {
    private static Phylogeny phy;
    private static LinkedHashMap<PhylogenyNode, PhylogenyNode> pairs;

    public static void main(String[] args) throws IOException {
        String file1;
        String f = "";
        if (args.length == 2) {
            f = (Boolean.parseBoolean(args[1])) ? "GenomTreesNewicks/" : "ProteomTreesNewicks/";
            file1 = f + args[0] + ".txt";
        } else {
            file1 = "tmp/out.4";
        }
        PhylogenyParser parser = ParserUtils.createParserDependingOnFileType(new File(file1), true);
        phy = PhylogenyMethods.readPhylogenies(parser, new File(file1))[0];
//        for (PhylogenyNodeIterator it = phy.iteratorPostorder(); it.hasNext(); ) {
//            PhylogenyNode n = it.next();
//            System.out.print(n.toString() + " ");
//        }
//        dfs(phy.getRoot());
        List<PhylogenyNode> extNodes = phy.getExternalNodes();
        LinkedHashSet<PhylogenyNode> specialNodes = new LinkedHashSet<>();
        for (PhylogenyNode n : extNodes) {
            if (n.getDistanceToParent() != 0) {
                List<PhylogenyNode> path = getPathToRoot(n, phy.getRoot());
                for (PhylogenyNode node : path) {
                    specialNodes.add(node);
                    System.out.print(node.toString() + " ");
                }
                System.out.println();
            }
        }
        dfs1(phy.getRoot(), specialNodes);
        String result = phy.toString();
        if (args.length == 2) {
            try {
                FileUtils.forceDelete(new File(f + args[0] + ".txt"));
            } catch (IOException ignored) {
            }
        }
//        System.out.println(phy.toString());
        PrintWriter pw = new PrintWriter(f + args[0] + ".txt");
        pw.println(result);
        pw.close();
        phy = null;
    }

    private static void dfs(PhylogenyNode node) {
//        System.out.println("current node = " + node.toString());
        List<PhylogenyNode> list = node.getDescendants();
        if (list.size() != 0 && list.size() != 1) {
            double sum = 0.0;
            for (PhylogenyNode n : list) {
//                System.out.print(n.toString() + " ");
                sum += n.getDistanceToParent();
            }

            double x = node.getDistanceToParent() / sum;
            boolean isCorrect = true;
            if (Double.isNaN(x) || Double.isInfinite(x)) {
                isCorrect = false;
                System.out.println("warning");
                x = node.getDistanceToParent() / node.getDescendants().size();
            }
            node.setDistanceToParent(0.0);
//            System.out.println();
            for (PhylogenyNode n : list) {
                double tmp = (isCorrect) ? n.getDistanceToParent() + x * n.getDistanceToParent() : x;
                n.setDistanceToParent(tmp);
                dfs(n);
            }
        } else {
            if (list.size() == 1) {
                double x = node.getDistanceToParent();
                node.setDistanceToParent(0.0);
                for (PhylogenyNode n : list) {
                    double tmp = n.getDistanceToParent() + x;
                    n.setDistanceToParent(tmp);
                    dfs(n);
                }
            }
        }
    }

    private static void dfs1(PhylogenyNode node, LinkedHashSet<PhylogenyNode> specialNodes) {
        System.out.println("current node = " + node.toString());
        List<PhylogenyNode> list = node.getDescendants();
        if (list.size() != 0) {
            double sum = 0.0;
            for (PhylogenyNode n : list) {
//                System.out.print(n.toString() + " ");
                if (specialNodes.contains(n)) {
                    sum += n.getDistanceToParent();
                }
            }

            double x = node.getDistanceToParent() / sum;
            boolean isCorrect = true;
            if (Double.isNaN(x) || Double.isInfinite(x)) {
                isCorrect = false;
                System.out.println("warning");
                x = node.getDistanceToParent() / node.getDescendants().size();
            }
            node.setDistanceToParent(0.0);
//            System.out.println();
            for (PhylogenyNode n : list) {
                if (n.getDistanceToParent() != 0 || specialNodes.contains(n)) {
                    double tmp = (isCorrect) ? n.getDistanceToParent() + x * n.getDistanceToParent() : x;
                    if (Double.isNaN(tmp))
                    {
                        System.out.println();
                    }
                    n.setDistanceToParent(tmp);
                    dfs1(n, specialNodes);
                }
            }
        }
    }

//    private static Phylogeny deleteUselessBranches(Phylogeny phy)
//    {
//        List<PhylogenyNode> extNodes = phy.getExternalNodes();
//    }

    private static List<PhylogenyNode> getPathToRoot(PhylogenyNode node, PhylogenyNode root) {
        List<PhylogenyNode> list = new LinkedList<>();
        PhylogenyNode curNode = node;
        list.add(curNode);
        while (curNode != root) {
            curNode = curNode.getParent();
            list.add(curNode);
        }
        return list;
    }
}
