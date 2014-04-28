import org.forester.archaeopteryx.AptxUtil;
import org.forester.archaeopteryx.Configuration;
import org.forester.archaeopteryx.Options;
import org.forester.archaeopteryx.TreeColorSet;
import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.util.ParserUtils;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.BranchColor;
import org.forester.phylogeny.data.NodeVisualization;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Kir on 04.03.14.
 */
public class OverlayTrees {
    private static double EPS = 0.000000000000000001;
    public static double result;

    public static void main(String[] args) throws IOException {
        String a = args[0];
        String b = args[1];
        boolean genomeAnalysis = Boolean.parseBoolean(args[2]);
        String path1 = (genomeAnalysis) ? "tmp30/_tmpCompareGenomeProteom.1" : "ProteomTreesNewicks/" + a + ".txt";
        String path2 = (genomeAnalysis) ? "tmp30/_tmpCompareGenomeProteom.2" : "ProteomTreesNewicks/" + b + ".txt";

//        String a = "tmp/out.1";
//        String b = "tmp/out.5";
        final File treefile1 = new File(path1);
        final File treefile2 = new File(path2);
//        final File treefile1 = new File("treeToCompare/1.txt");
//        final File treefile2 = new File("treeToCompare/2.txt");
        PhylogenyParser parser = ParserUtils.createParserDependingOnFileType(treefile1, true);
        Phylogeny phy1 = PhylogenyMethods.readPhylogenies(parser, treefile1)[0];
        parser = ParserUtils.createParserDependingOnFileType(treefile2, true);
        Phylogeny phy2 = PhylogenyMethods.readPhylogenies(parser, treefile2)[0];

        for (PhylogenyNodeIterator it = phy1.iteratorPostorder(); it.hasNext(); ) {
            PhylogenyNode n1 = it.next();
            PhylogenyNode n2 = phy2.getNode(n1.toString());
            double w1 = n1.getDistanceToParent();
            double w2 = n2.getDistanceToParent();
            if (w1!=0 && w2!=0)
            {
                n1.getBranchData().setBranchColor(new BranchColor(Color.YELLOW));
            }
            if (w1!=0 && w2==0)
            {
                n1.getBranchData().setBranchColor(new BranchColor(Color.RED));
            }
            if (w1==0 && w2!=0)
            {
                n1.getBranchData().setBranchColor(new BranchColor(Color.GREEN));
            }
            if (w1==0 && w2==0)
            {
                n1.getBranchData().setBranchColor(new BranchColor(Color.GRAY));
            }
        }

        Configuration config = setConfig();
        AptxUtil.writePhylogenyToGraphicsFile(phy1,
                new File("_pngOverlay/" + "test" + ".png"),
                1300,
                1300,
                AptxUtil.GraphicsExportType.PNG,
                config);


    }
    private static Configuration setConfig() {
        final Configuration config = new Configuration();
        config.putDisplayColors(TreeColorSet.NODE_BOX, new Color(255, 0, 225));
        config.putDisplayColors(TreeColorSet.BACKGROUND, new Color(0, 0, 0));
//            config.putDisplayColors(TreeColorSet.BRANCH, new Color(0, 0, 255));
//            config.putDisplayColors(TreeColorSet.TAXONOMY, new Color(255, 255, 255));
        config.setPhylogenyGraphicsType(Options.PHYLOGENY_GRAPHICS_TYPE.CIRCULAR);
//        if (isTest) config.setPhylogenyGraphicsType(Options.PHYLOGENY_GRAPHICS_TYPE.RECTANGULAR);
        config.setDisplayAsPhylogram(false);
        config.setDynamicallyHideData(false);
        config.setColorLabelsSameAsParentBranch(true);
        config.setShowBranchLengthValues(true);
        config.setNodeLabelDirection(Options.NODE_LABEL_DIRECTION.RADIAL);
        config.setTaxonomyColorize(true);
        config.setColorizeBranches(true);
        config.setUseBranchesWidths(true);
        config.setDisplayTaxonomyCode(false);
        config.setBaseFontSize(12);
//        System.out.println(config.getFrameXSize());
        config.setShowDefaultNodeShapesExternal(true);
        config.setShowDefaultNodeShapesInternal(true);
        config.setDefaultNodeFill(NodeVisualization.NodeFill.SOLID);
        config.setDefaultNodeShape(NodeVisualization.NodeShape.CIRCLE);
        config.setDefaultNodeShapeSize((short) 5);
        config.setShowScale(true);
        return config;
    }
}
