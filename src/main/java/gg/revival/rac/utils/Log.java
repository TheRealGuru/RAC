package gg.revival.rac.utils;

import gg.revival.rac.RAC;
import lombok.Getter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public class Log {

    @Getter private RAC rac;

    public Log(RAC rac) {
        this.rac = rac;
    }

    public void log(Level level, String message) {
        String logMessage = "[" + new SimpleDateFormat("hh:mm:ss z").format(new Date()) + "] " + message;
        System.out.println("[RAC][" + level.toString() + "] " + message);
        logToFile(logMessage);
    }

    public void log(String message) {
        log(Level.INFO, message);
    }

    private void logToFile(String message) {
        String logName = new SimpleDateFormat("M'_'d'_'y").format(new Date()).toLowerCase() + ".txt";

        try {
            File folder = new File(rac.getDataFolder() + File.separator + "logs" + File.separator);

            if(!folder.exists())
                folder.mkdirs();

            File log = new File(folder, logName);

            if(!log.exists())
                log.createNewFile();

            FileWriter fileWriter = new FileWriter(log, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.println(message);
            printWriter.flush();
            printWriter.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
