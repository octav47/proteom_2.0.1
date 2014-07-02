package support;

import java.io.*;

public class ToDownload {
    public static void main(String[] args) throws IOException {
        BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(args[0])));
        String curBf;
        PrintWriter pw = null;
        boolean flag = false;
        while ((curBf = bf.readLine()) != null) {
            String[] ch = curBf.split("\t");
            ch[3] = ch[3].replace("/", "\\");
            String number = ch[0];
            if (!flag) {
                pw = new PrintWriter(number + ".txt");
                pw.println(number);
                flag = true;
            }
            ch = ch[3].split("\\\\");
            pw.println("\\" + ch[ch.length - 2] + "\\" + ch[ch.length - 1]);
        }
        if (pw != null) {
            pw.close();
        }
    }
}
