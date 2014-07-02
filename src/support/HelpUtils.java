package support;

import java.util.LinkedHashSet;

/**
 * Created by Kir on 29.04.14.
 */
public class HelpUtils {
    private static String prefix = "[help] ";
    private static LinkedHashSet<String> commands = new LinkedHashSet<>();

    public static void showGlobalHelp() {
        if (commands.isEmpty()) fillCommands();
        System.out.println(prefix + "List of available commands:");
        for (String s : commands) {
            System.out.println(s);
        }
        System.out.println(prefix + "You can also use \"help <command>\" for more information");
    }

    private static void fillCommands() {
        commands.add("help");
        commands.add("exit");
        commands.add("calc");
        commands.add("build");
        commands.add("list");
        commands.add("print");
        commands.add("arch");
        commands.add("compare");
        commands.add("info");
        commands.add("delete");
        commands.add("png");
        commands.add("overlay");
    }

    public static void Manual(String command) {
        switch (command.toLowerCase()) {
            case "help": {
                System.out.println(prefix + "Shows list of commands and manuals for it");
                System.out.println(prefix + "Syntax: help | help <command>");
                break;
            }
            case "exit": {
                System.out.println(prefix + "Exit from " + Configuration.getHeader());
                System.out.println(prefix + "Syntax: exit");
                break;
            }
            case "calc": {
                System.out.println(prefix + "Starts GUI Archaeopteryx Applet for experiment #N");
                System.out.println(prefix + "Syntax: calc <#N>");
                break;
            }
            case "build": {
                System.out.println(prefix + "Builds a special-type-tree: unique, allranks and score for experiment #N");
                System.out.println(prefix + "Syntax: build <unique|allranks|score> #N");
                break;
            }
            case "list": {
                System.out.println(prefix + "Shows a list of built or available experiments");
                System.out.println(prefix + "Syntax: list <built|data>");
                break;
            }
            default:
                System.out.println(prefix + "wrong command");
        }
    }
}
