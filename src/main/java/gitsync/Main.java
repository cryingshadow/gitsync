package gitsync;

import java.io.*;
import java.util.concurrent.*;

import javax.swing.*;

public class Main {

    public static void main(final String[] args) throws IOException {
        try {
            final File directory = new File(System.getProperty("user.dir"));
            final File resetFile = directory.toPath().resolve("reset.log").toFile();
            final File cleanFile = directory.toPath().resolve("clean.log").toFile();
            final File pullFile = directory.toPath().resolve("pull.log").toFile();
            Process process = new ProcessBuilder(
                "git",
                "reset",
                "--hard"
            ).inheritIO().directory(directory).redirectOutput(resetFile).redirectError(resetFile).start();
            process.waitFor(60, TimeUnit.SECONDS);
            process = new ProcessBuilder(
                "git",
                "clean",
                "-f",
                "-d"
            ).inheritIO().directory(directory).redirectOutput(cleanFile).redirectError(cleanFile).start();
            process.waitFor(60, TimeUnit.SECONDS);
            process = new ProcessBuilder(
                "git",
                "pull",
                "--rebase"
            ).inheritIO().directory(directory).redirectOutput(pullFile).redirectError(pullFile).start();
            process.waitFor(60, TimeUnit.SECONDS);
            try (BufferedReader reader = new BufferedReader(new FileReader(resetFile))) {
                final String line = reader.readLine();
                if (!line.startsWith("HEAD is now")) {
                    JOptionPane.showMessageDialog(null, "Fehler beim Reset: " + line);
                    return;
                }
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(cleanFile))) {
                final String line = reader.readLine();
                if (line != null) {
                    JOptionPane.showMessageDialog(null, "Fehler beim Clean: " + line);
                    return;
                }
            }
            try (BufferedReader reader = new BufferedReader(new FileReader(pullFile))) {
                final String line = reader.readLine();
                if (line.startsWith("error")) {
                    JOptionPane.showMessageDialog(null, "Fehler beim Pull: " + line);
                    return;
                }
            }
            JOptionPane.showMessageDialog(null, "Erfolgreich aktualisiert!");
        } catch (IOException | InterruptedException e1) {
            JOptionPane.showMessageDialog(null, e1);
        }
    }

}
