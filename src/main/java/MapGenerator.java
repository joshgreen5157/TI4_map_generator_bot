import generator.GenerateMap;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.io.File;

public class MapGenerator {

    public static void main(String[] args)
            throws LoginException {

        JDA jda = JDABuilder.createDefault(args[0]).build();
        jda.addEventListener(new MessageListener());



//        ResourceHelper resourceHelper = new ResourceHelper();
//        File setupFile = resourceHelper.getResource("6player_setup.png");
//        GenerateMap instance = new GenerateMap(setupFile);
//
//
//        File nebula = resourceHelper.getResource("planets/nebula.png");
//        File saudor = resourceHelper.getResource("planets/saudor.png");
//        File wellon = resourceHelper.getResource("planets/wellon.png");
//        instance.addTile(setupFile, "0");
//        instance.addTile(nebula, "3a");
//        instance.addTile(saudor, "3r");
//        instance.addTile(wellon, "3b");
//        instance.addTile(nebula, "2a");
//
//        instance.saveImage();
    }
}
