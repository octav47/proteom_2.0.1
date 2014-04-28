package support;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

class Paint {
    public static void main(String[] args) throws IOException {
        BufferedImage im = ImageIO.read(new File("C:\\Users\\Kir\\Desktop\\123.png"));
        int w = im.getWidth();
        int h = im.getHeight();
        int[][] pixel = new int[w][h];
        int maxColor = Integer.MIN_VALUE;
        int minColor = Integer.MAX_VALUE;
//        for (int i = 0; i < w; i++) {
//            for (int j = 0; j < h; j++) {
//                pixel[i][j] = im.getRGB(i, j);
//                if (pixel[i][j] > maxColor)
//                    maxColor = pixel[i][j];
//                if (pixel[i][j] < minColor)
//                    minColor = pixel[i][j];
//            }
//        }
        maxColor = -1;
        minColor = -16777216;
        double l = minColor / maxColor;
        System.out.println("maxColor = " + maxColor);
        System.out.println("minColor = " + minColor);
        System.out.println("l = " + l);
//        maxColor = -1
//        minColor = -16777216
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\Kir\\IdeaProjects\\PhyBuilder\\tmp20\\out.5")));
        double[][] a = new double[30][30];
        String curBf = bufferedReader.readLine();
        double max = Double.parseDouble(curBf.split("\t")[2]);
        double min = Double.parseDouble(curBf.split("\t")[2]);
        int k = 0;
        while ((curBf = bufferedReader.readLine()) != null) {
            String[] ch = curBf.split("\t");
            int i = Integer.parseInt(ch[0]);
            int j = Integer.parseInt(ch[1]);
            double d = Double.parseDouble(ch[2]);
            i -= 674;
            j -= 674;
            a[i][j] = d;
            if (d > max)
                max = d;
            if (d < min)
                min = d;
            k++;
        }
        System.out.println("k = " + k);
        System.out.println("max = " + max);
        System.out.println("min = " + min);
        int step = 5;
        for (int i = 0; i < 26; i++)
            for (int j = 0; j < 26; j++) {
                double curColor = (a[i][j] * 255) / max;
                int curColorInt = (int) curColor;
                Color c = new Color(curColorInt, curColorInt, curColorInt);
                int c1 = c.getRGB();
                im.setRGB(i, j, c1);
            }
        File output = new File("C:\\Users\\Kir\\Desktop\\result.png");
//        im.
//        ImageIO.write(im, "png", output);
    }

    private static void parseGradient() throws IOException {
        BufferedImage im = ImageIO.read(new File("C:\\Users\\Kir\\Desktop\\gradient2.png"));
        int w = im.getWidth();
        int h = im.getHeight();
        int[][] pixel = new int[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                pixel[i][j] = im.getRGB(i, j);
            }
        }
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                System.out.print(pixel[i][j] + " ");
            }
            System.out.println();
        }
    }
}