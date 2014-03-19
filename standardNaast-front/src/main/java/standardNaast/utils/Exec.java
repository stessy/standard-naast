// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:22
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Exec.java
package standardNaast.utils;

import java.io.*;

public class Exec {

    public static void setVerbose(boolean verboseFlag) {
        verbose = verboseFlag;
    }

    public static boolean getVerbose() {
        return verbose;
    }

    public static boolean exec(String command) {
        return exec(command, false, false);
    }

    public static boolean execWait(String command) {
        return exec(command, false, true);
    }

    public static boolean execPrint(String command) {
        return exec(command, true, false);
    }

    public static boolean exec(String command, boolean printResults, boolean wait) {
        if (verbose) {
            printSeparator();
            System.out.println((new StringBuilder()).append("Executing '").append(command).append("'.").toString());
        }
        try {
            Process p = Runtime.getRuntime().exec(command);
            if (printResults) {
                BufferedInputStream buffer = new BufferedInputStream(p.getInputStream());
                BufferedReader buf = new BufferedReader(new InputStreamReader(buffer));
                String s = null;
                try {
                    while ((s = buf.readLine()) != null) {
                        System.out.println((new StringBuilder()).append("Output: ").append(s).toString());
                    }
                    buf.close();
                    if (p.exitValue() != 0) {
                        if (verbose) {
                            printError((new StringBuilder()).append(command).append(" -- p.exitValue() != 0").toString());
                        }
                        boolean flag = false;
                        return flag;
                    }
                } catch (Exception e) {
                }
            } else if (wait) {
                try {
                    System.out.println(" ");
                    int returnVal = p.waitFor();
                    if (returnVal != 0) {
                        if (verbose) {
                            printError(command);
                        }
                        boolean flag1 = false;
                        return flag1;
                    }
                } catch (Exception e) {
                    if (verbose) {
                        printError(command, e);
                    }
                    boolean flag2 = false;
                    return flag2;
                }
            }
        } catch (Exception e) {
            if (verbose) {
                printError(command, e);
            }
            boolean flag3 = false;
            return flag3;
        }
        return true;
    }

    private static void printError(String command, Exception e) {
        System.out.println((new StringBuilder()).append("Error doing exec(").append(command).append("): ").append(e.getMessage()).toString());
        System.out.println("Did you specify the full pathname?");
    }

    private static void printError(String command) {
        System.out.println((new StringBuilder()).append("Error executing '").append(command).append("'.").toString());
    }

    private static void printSeparator() {
        System.out.println("==============================================");
    }

    public Exec() {
    }
    private static boolean verbose = true;
}