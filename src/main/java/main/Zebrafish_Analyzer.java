package main;




import GUI.MainMenuGUI;
import ij.ImageJ;
import ij.plugin.PlugIn;


/**
 *
 * @author Jan-Lukas Foerde
 */
public class Zebrafish_Analyzer implements PlugIn {
    
    public static void main(String... args) {
        Class<?> clazz = Zebrafish_Analyzer.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        int lastIdx = url.lastIndexOf('/');
        String pluginsDir = url.substring(5, lastIdx);
        pluginsDir = pluginsDir.substring(0, pluginsDir.lastIndexOf('/'));
        System.out.println(pluginsDir);
        System.setProperty("plugins.dir", pluginsDir);
        new ImageJ();  
    }

    @Override
    public void run(String string) { 
            MainMenuGUI cellFinder = new MainMenuGUI();
            cellFinder.setVisible(true);
    }
}
