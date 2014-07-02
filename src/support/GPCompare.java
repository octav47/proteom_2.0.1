package support;

import javafx.util.Pair;
import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.util.ParserUtils;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Kir on 21.05.14.
 */
public class GPCompare {
    private static HashSet<PhylogenyNode> vertRes = null;
    private static HashSet<Pair<PhylogenyNode, PhylogenyNode>> edgeRes = null;

    public static void main(String[] args) throws IOException {
        vertRes = new HashSet<>();
        edgeRes = new HashSet<>();
        File pFileTree = new File("tmp30/gpcompare/1.txt");
        File gFileTree = new File("tmp30/gpcompare/2.txt");
        //proteom
        PhylogenyParser parser = ParserUtils.createParserDependingOnFileType(pFileTree, true);
        Phylogeny pPhy = PhylogenyMethods.readPhylogenies(parser, pFileTree)[0];
        //genome
        parser = ParserUtils.createParserDependingOnFileType(gFileTree, true);
        Phylogeny gPhy = PhylogenyMethods.readPhylogenies(parser, gFileTree)[0];
        //deleting null verts in genome-tree
        for (PhylogenyNodeIterator it = gPhy.iteratorPostorder(); it.hasNext(); ) {
            PhylogenyNode n = it.next();
            if (n.getName().equals("")) {
                gPhy.deleteSubtree(n, true);
            }
        }
//        dfs(gPhy, gPhy.getRoot());
        getIntersectedSet(pPhy, gPhy);
        for (PhylogenyNodeIterator it = pPhy.iteratorPreorder(); it.hasNext(); ) {
//            System.out.println(pPhy.toString());
            PhylogenyNode n = it.next();
            System.out.println(n.getName());
            if (!n.getName().equalsIgnoreCase("root")) {
                if (!(vertRes.contains(n) && edgeRes.contains(new Pair<>(n, n.getParent())))) {
//                    pPhy.deleteSubtree(n, false);
                }
            }
        }
        System.out.println(pPhy.toString());
    }

    private static void getIntersectedSet(Phylogeny phy1, Phylogeny phy2) {
        HashSet<PhylogenyNode> vert1 = new HashSet<>();
        HashSet<Pair<PhylogenyNode, PhylogenyNode>> edge1 = new HashSet<>();
        HashSet<PhylogenyNode> vert2 = new HashSet<>();
        HashSet<Pair<PhylogenyNode, PhylogenyNode>> edge2 = new HashSet<>();
        for (PhylogenyNodeIterator it = phy1.iteratorPreorder(); it.hasNext(); ) {
            PhylogenyNode n = it.next();
            vert1.add(n);
            Pair<PhylogenyNode, PhylogenyNode> pair = new Pair<>(n, n.getParent());
            edge1.add(pair);
        }
        for (PhylogenyNodeIterator it = phy2.iteratorPreorder(); it.hasNext(); ) {
            PhylogenyNode n = it.next();
            vert2.add(n);
            Pair<PhylogenyNode, PhylogenyNode> pair = new Pair<>(n, n.getParent());
            edge2.add(pair);
        }
        vertRes = intersectSet(vert1, vert2);
        edgeRes = intersectSet2(edge1, edge2);
        for (PhylogenyNode n : vertRes) {
            System.out.print(n.getName() + " ");
        }
        System.out.println("\n=====");
        for (Pair<PhylogenyNode, PhylogenyNode> pair : edgeRes) {
            System.out.println(pair.getKey() + " -> " + pair.getValue());
        }
//        return null;
    }

    private static void dfs(Phylogeny phy, PhylogenyNode node) {
        System.out.println(node.getName());
        List<PhylogenyNode> a = node.getAllDescendants();
        if (a == null) {
            return;
        }
        for (PhylogenyNode n : a) {
            dfs(phy, n);
        }
    }

    private static HashSet<PhylogenyNode> intersectSet(HashSet<PhylogenyNode> set1, HashSet<PhylogenyNode> set2) {
        HashSet<PhylogenyNode> result = new HashSet<>();
        for (PhylogenyNode n : set1) {
            if (set2.contains(n)) {
                result.add(n);
            }
        }
        return result;
    }

    private static HashSet<Pair<PhylogenyNode, PhylogenyNode>> intersectSet2(HashSet<Pair<PhylogenyNode, PhylogenyNode>> set1, HashSet<Pair<PhylogenyNode, PhylogenyNode>> set2) {
        HashSet<Pair<PhylogenyNode, PhylogenyNode>> result = new HashSet<>();
        for (Pair<PhylogenyNode, PhylogenyNode> pair : set1) {
            if (set2.contains(pair)) {
                result.add(pair);
            }
        }
        return result;
    }
}
