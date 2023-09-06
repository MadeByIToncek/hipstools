import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class GenerateIndexFile {
    public static void main(String[] args) throws IOException {
        File file = new File("./public");

        if(!file.exists()) {
            System.out.println("./public nonexistent, 404ing");
            System.exit(404);
        }
        List<File> dirs = new ArrayList<>();
        for (File f : Objects.requireNonNull(file.listFiles())) {
            if(f.isDirectory()){
                dirs.add(f);
            }
        }

        File file1 = new File("./public/index.html");
        File file2 = new File("./public/hipslist");
        if(file1.exists()) file1.delete();
        if(file2.exists()) file2.delete();

        file1.createNewFile();
        file2.createNewFile();

        try (FileWriter indexWriter = new FileWriter(file1);
             FileWriter hipsWriter = new FileWriter(file2)) {
            indexWriter.write("<head>\n" +
                    "    <title>HiPS.itoncek.space</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <h1>HiPS.itoncek.space</h1>\n" +
                    "    <h3>You can find more info at <a href=\"https://aladin.cds.unistra.fr/hips/\">Aladin</a> site</h3>");
            for (File dir : dirs) {
                indexWriter.write(String.format("    <a href=\"%s\">%s</a> <br>\n", "./" + dir.getName() + "/", "/" + dir.getName()));
                hipsWriter.write(String.format("hips_service_url = %s\n", "https://data.itoncek.space/" + dir.getName()));
                hipsWriter.write(String.format("hips_release_date = %s\n", formatDate()));
                hipsWriter.write("hips_status = public master clonableOnce\n");
                int i = 0;
                try(Scanner sc = new Scanner(new File(dir.toPath() +"/index.html"));
                    FileWriter indexModifier = new FileWriter(dir.toPath() +"/index2.html")) {
                    while (sc.hasNextLine()){
                        indexModifier.write(sc.nextLine()+"\n");
                        i++;
                        if(i==3){
                            indexModifier.write("\t<script async src=\"https://arc.io/widget.min.js#QHP4ZUrw\"></script>\n");
                        }
                    }
                }
                new File(dir.toPath() +"/index.html").delete();
                Files.move(new File(dir.toPath() +"/index2.html").toPath(),new File(dir.toPath() +"/index.html").toPath(),REPLACE_EXISTING);
            }
            indexWriter.write("</body>");
        }
    }

    private static String formatDate() {
        LocalDateTime now = LocalDateTime.now();
        return String.format("%04d-%02d-%02dT%02d:%02dZ",
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth(),
                now.getHour(),
                now.getMinute());
    }
}
