import org.apache.commons.io.FileUtils;
import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.util.ParserUtils;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Kir on 04.02.14.
 */

public class CompareTreesDiff {
    public static void main(String[] args) throws IOException {
        String a = args[0];
        String b = args[1];
        final File treefile1 = new File("ProteomTreesNewicks/" + a + ".txt");
        final File treefile2 = new File("ProteomTreesNewicks/" + b + ".txt");
        PhylogenyParser parser = ParserUtils.createParserDependingOnFileType(treefile1, true);
        final Phylogeny phy1 = PhylogenyMethods.readPhylogenies(parser, treefile1)[0];
        parser = ParserUtils.createParserDependingOnFileType(treefile2, true);
        final Phylogeny phy2 = PhylogenyMethods.readPhylogenies(parser, treefile2)[0];
        Phylogeny phyRes = diff(phy1, phy2);
        String compareResultFileName = "resultCompare/" + a + "_comp_" + b + ".txt";

        try {
            FileUtils.forceDelete(new File("resultCompare/" + a + "_comp_" + b + ".txt"));
        } catch (IOException ignored) {
        }

        printTree(compareResultFileName, phyRes);
        PhyShow.main(new String[]{compareResultFileName, "-comp"});

    }

    private static Phylogeny diff(Phylogeny phy1, Phylogeny phy2) {
        Phylogeny res = phy1.copy();
        for (PhylogenyNodeIterator it = phy1.iteratorPostorder(); it.hasNext(); ) {
            PhylogenyNode node1 = it.next();
            PhylogenyNode node2 = phy2.getNode(node1.toString());
            double diffWeight = node1.getDistanceToParent() - node2.getDistanceToParent();
            res.getNode(node1.toString()).setDistanceToParent(diffWeight);
        }
        return res;
    }

    private static void printTree(String fileName, Phylogeny phy) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(fileName);
        pw.print(phy.toNewHampshire());
        pw.close();
    }
}
