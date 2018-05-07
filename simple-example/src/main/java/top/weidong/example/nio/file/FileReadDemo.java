package top.weidong.example.nio.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * Description: nio中的文件读取
 *
 * @author dongwei
 * @date 2018/05/05
 * Time: 17:12
 */
public class FileReadDemo {

    private static void dirs(){
        Path dir = Paths.get("/Users/dongwei");
        try(DirectoryStream<Path> stream = Files.newDirectoryStream(dir)){
            for(Path e : stream){
                System.out.println(e.getFileName());
            }
        }catch(IOException e){

        }
    }

    private static void read(){
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get("/Users/dongwei/package-lock.json"), StandardCharsets.UTF_8);
            String str = null;
            while((str = reader.readLine()) != null){
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        dirs();
        read();
    }
}
