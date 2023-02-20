package analyses;

//constructeur qui ajoute les dependance, prend en constructeur un url d'un fichier, 
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class reader {
    public static String read(String filePath) {
        String code = "";
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                code += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return code;
    }
}
