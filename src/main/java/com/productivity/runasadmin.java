package com.productivity;

import java.io.File;
import java.io.FileWriter;

public class runasadmin {
    public static void main(String[] args) {
        String test = "C:\\Users\\Billy1301\\Music\\Test.TXT@# Copyright (c) 1993-2009 Microsoft Corp.*#*# This is a sample HOSTS file used by Microsoft TCP/IP for Windows.*#*# This file contains the mappings of IP addresses to host names. Each*# entry should be kept on an individual line. The IP address should*# be placed in the first column followed by the corresponding host name.*# The IP address and the host name should be separated by at least one*# space.*#*# Additionally, comments (such as these) may be inserted on individual*# lines or following the machine name denoted by a '#' symbol.*#*# For example:*#*#      102.54.94.97     rhino.acme.com          # source server*#       38.25.63.10             x.acme.com              # x client host**# localhost name resolution is handled within DNS itself.*#    127.0.0.1       localhost*#     ::1             localhost*127.0.0.1    www.youtube.com*127.0.0.1    www.facebook.com";
        if (args.length == 2) {
            File file = new File(args[0]);
            String[] data = args[1].split("\\r?\\n");
            writeData(data, file);
        } 
        else if (args.length == 1) {
            String[] input = args[0].split("@");
            File file = new File(input[0]);
            String[] data = input[1].split("\\*");
            writeData(data, file);
            for (int i = 0; i < data.length; i++) System.out.println(data[i]);
        }
        //String[] input = test.split("@");
        //String[] data = input[1].split("\\*");
        //writeData(data, new File(input[0]));
    }
    
    public static void writeData(String data, File file) {
        try  {
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void writeData(String[] dataArr, File file) {
        String data = "";
        for (int i = 0; i < dataArr.length; i++) {
            data += (dataArr[i] + "\n");
        }
        writeData(data, file);
    }
}
