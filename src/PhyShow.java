import org.forester.archaeopteryx.*;
import org.forester.io.parsers.PhylogenyParser;
import org.forester.io.parsers.util.ParserUtils;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.BranchColor;
import org.forester.phylogeny.data.NodeVisualization;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;
import support.SubFuntions;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class PhyShow {

    private static double EPS = 0.000000001;
    private static Color emptyBranchesColor = Color.BLUE;
    private static String fileInput = "output.txt";
    private static ArrayList<Integer> gradient;
    private static boolean isTest = false;

    public static void main(String[] args) {
        try {
            boolean isPNGs = false;
            boolean isOverlay = false;
            gradient = SubFuntions.getGradient();
            File treeFile = null;
            switch (args.length) {
                case 1:
                    if (args[0].equalsIgnoreCase("-ct")) {
                        treeFile = new File("treeToCompare/compareResult.txt");
                    }
                    break;
                case 2:
                    switch (args[1].toLowerCase()) {
                        case "-arch":
                            treeFile = new File(args[0]);
                            break;
                        case "-comp":
                            treeFile = new File(args[0]);
                            break;
                        case "-png":
                            treeFile = new File("ProteomTreesNewicks/" + args[0] + ".txt");
                            isPNGs = true;
                            break;
                        case "-pngtest":
                            treeFile = new File("_tests/png_test_without_nulls.txt");
                            isPNGs = true;
                            isTest = true;
                            break;
                        case "-overlay":
                            treeFile = new File("Overlay/" + args[0] + ".txt");
                            isOverlay = true;
                            break;
                    }
                    break;
                default:
                    treeFile = new File(fileInput);
            }
//            if (args.length == 1) {
//                if (args[0].equalsIgnoreCase("-ct")) {
//                    treeFile = new File("treeToCompare/compareResult.txt");
//                }
//            } else {
//                if (args.length == 2) {
//                    if (args[1].equalsIgnoreCase("-arch")) {
//                        treeFile = new File(args[0]);
//                    }
//                } else {
//                    treeFile = new File(fileInput);
//                }
//            }
            // Reading-in of a tree from a file.

            PhylogenyParser parser = ParserUtils.createParserDependingOnFileType(treeFile, true);
            Phylogeny phy = PhylogenyMethods.readPhylogenies(parser, treeFile)[0];

//            System.out.println("post order");
//            for (PhylogenyNodeIterator it = phy.iteratorPostorder(); it.hasNext(); )
//            {
//                System.out.println(it.next().toString());
//            }
//            System.out.println("level order");
//            for (PhylogenyNodeIterator it = phy.iteratorLevelOrder(); it.hasNext(); )
//            {
//                System.out.println(it.next().toString());
//            }
//            System.out.println("external forward");
//            for (PhylogenyNodeIterator it = phy.iteratorExternalForward(); it.hasNext(); )
//            {
//                System.out.println(it.next().toString());
//            }
//            System.out.println("pre order");
//            for (PhylogenyNodeIterator it = phy.iteratorPreorder(); it.hasNext(); )
//            {
//                System.out.println(it.next().toString());
//            }


            // Creating a node name -> color map.
            double minWeight = Double.MAX_VALUE;
            double maxWeight = -1 * Double.MAX_VALUE;
//            Median median = new Median();
            double medianDouble;
            ArrayList<Double> weights = new ArrayList<>();
            for (PhylogenyNodeIterator it = phy.iteratorPostorder(); it.hasNext(); ) {
                PhylogenyNode n = it.next();
                double curWeight = n.getDistanceToParent();
                if (curWeight < minWeight) {
                    minWeight = curWeight;
                }
                if (curWeight > maxWeight) {
                    maxWeight = curWeight;
                }
                weights.add(curWeight);
            }


            double middleWeight = (maxWeight + minWeight) / 2;
//            System.out.println(maxWeight);
//            System.out.println(middleWeight);
//            System.out.println(minWeight);
//            System.out.println(calcColor(middleWeight, minWeight, maxWeight));
//            medianDouble = median.evaluate(toDoubleArray(weights));
//            medianDouble = (medianDouble * 255) / maxWeight;

            for (final PhylogenyNodeIterator it = phy.iteratorPostorder(); it.hasNext(); ) {
                final PhylogenyNode n = it.next();
                double curWeight = n.getDistanceToParent();
                Color curColor;
                if (Math.abs(curWeight - minWeight) < EPS) {
                    curColor = emptyBranchesColor;
                } else {
                    if (Math.abs(curWeight - maxWeight) < EPS * 3) {
                        curColor = Color.RED;
                    } else {
//                    curWeight = (curWeight * 255) / maxWeight;
//                        System.out.println(curWeight);
//                        if (curWeight>0)
//                        {
//                            System.out.println("here!");
//                        }
                        curColor = calcColor(curWeight, minWeight, maxWeight);
                    }
                }
                n.getBranchData().setBranchColor(new BranchColor(curColor));
                // To make colored subtrees thicker:
//                n.getBranchData().setBranchWidth(new BranchWidth(10));
            }
//            PhylogenyWriter phwr = new PhylogenyWriter();
//            File f = new File(args[0]);
//            phwr.toPhyloXML(f, phy, 0);

            phy = deleteNullBranches(phy);

            if (!isPNGs) {
                Archaeopteryx.createApplication(new Phylogeny[]{phy}, "lib/_aptx_configuration_file", "title");
            } else {
                Configuration config = setConfig();
                AptxUtil.writePhylogenyToGraphicsFile(phy,
                        new File("_png/" + args[0] + ".png"),
                        1300,
                        1300,
                        AptxUtil.GraphicsExportType.PNG,
                        config);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        isTest = false;
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

    private static double[] toDoubleArray(ArrayList<Double> a) {
        int k = a.size();
        double[] r = new double[k];
        for (int i = 0; i < k; i++) {
            r[i] = a.get(i);
        }
        return r;
    }

    private static Color calcColor(double curWeight, double minWeight, double maxWeight) {
        int size = gradient.size();
        double curColor = ((maxWeight - curWeight) * gradient.size()) / (maxWeight - minWeight);
//        if (curWeight > middleWeight) {
//            return new Color((int) curColor, 255, (int) curColor);
//        } else {
//            return new Color((int) curColor, (int) curColor, 255);
//        }

//        if (curColor > minWeight && curColor < (maxWeight - minWeight) / 4) {
//            return new Color()
//        }
        if ((int) curColor == gradient.size()) {
            return new Color(gradient.get((int) curColor - 1));
        } else {
            return new Color(gradient.get((int) curColor));
        }
    }

    private static Phylogeny deleteNullBranches(Phylogeny phy) {
        java.util.List<PhylogenyNode> extNodes = phy.getExternalNodes();
        PriorityQueue<PhylogenyNode> queue = new PriorityQueue<>();
        for (PhylogenyNode n : extNodes) {
            queue.add(n);
        }
        LinkedHashSet<PhylogenyNode> excludedNodes = new LinkedHashSet<>();
        while (queue.size() != 0) {
            PhylogenyNode n = queue.poll();
            if (n.getDistanceToParent() != 0) {
                java.util.List<PhylogenyNode> list = getAllNodesInPathToRoot(n);
                for (PhylogenyNode n1 : list) {
                    excludedNodes.add(n1);
                }
            } else {
                PhylogenyNode parent = n.getParent();
                if (!excludedNodes.contains(parent)) {
                    queue.add(n.getParent());
                }
            }
        }
        for (PhylogenyNodeIterator it = phy.iteratorPreorder(); it.hasNext(); ) {
            PhylogenyNode n = it.next();
            if (!excludedNodes.contains(n)) {
                try {
                    phy.deleteSubtree(n, false);
                } catch (Exception ignored) {
                }
            }
        }
//        System.out.println(phy.toString());
//        PrintWriter pw = new PrintWriter("test_without_nulls.txt");
//        pw.println(phy.toString());
//        pw.close();
        return phy;
    }

    private static java.util.List<PhylogenyNode> getAllNodesInPathToRoot(PhylogenyNode node) {
        java.util.List<PhylogenyNode> pathNodes = new LinkedList<>();
        rec(node, pathNodes);
        return pathNodes;
    }

    private static void rec(PhylogenyNode node, java.util.List<PhylogenyNode> list) {
        list.add(node);
        PhylogenyNode parent = node.getParent();
        if (parent != null) {
            rec(parent, list);
        }
    }

}