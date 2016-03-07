import org.openstreetmap.gui.jmapviewer.tilesources.*;

public class ModifiedOsmTileSource{
	
	// Positron
    public static class Positron extends AbstractOsmTileSource{
        private static final String PATTERN = "https://cartodb-basemaps-%s.global.ssl.fastly.net/light_all";
        private static final String[] SERVER = {"a", "b", "c"};
        private int serverNum = 0;

        /**
         * Constructs a new {@code "CARTODB"} tile source.
         */
        public Positron(){
            super("Positron", PATTERN, "POSITRON");
        }

        @Override
        public String getBaseUrl(){
            String url = String.format(this.baseUrl, new Object[] {SERVER[serverNum]});
            serverNum = (serverNum + 1) % SERVER.length;
            return url;
        }
    }
    
    // Dark Matter
    public static class DarkMatter extends AbstractOsmTileSource{

        private static final String PATTERN = "https://cartodb-basemaps-%s.global.ssl.fastly.net/dark_all";
        private static final String[] SERVER = {"a", "b", "c"};
        private int serverNum = 0;

        /**
         * Constructs a new {@code "CARTODB"} tile source.
         */
        public DarkMatter(){
            super("Dark Matter", PATTERN, "DARKMATTER");
        }

        @Override
        public String getBaseUrl(){
            String url = String.format(this.baseUrl, new Object[] {SERVER[serverNum]});
            serverNum = (serverNum + 1) % SERVER.length;
            return url;
        }
    }
    
    // Positron Lite
    public static class PositronLite extends AbstractOsmTileSource{
        private static final String PATTERN = "https://cartodb-basemaps-%s.global.ssl.fastly.net/light_nolabels";
        private static final String[] SERVER = {"a", "b", "c"};
        private int serverNum = 0;

        /**
         * Constructs a new {@code "CARTODB"} tile source.
         */
        public PositronLite(){
            super("Positron Lite", PATTERN, "POSITRONLITE");
        }

        @Override
        public String getBaseUrl(){
            String url = String.format(this.baseUrl, new Object[] {SERVER[serverNum]});
            serverNum = (serverNum + 1) % SERVER.length;
            return url;
        }
    }
   
    // Dark Matter Lite
    public static class DarkMatterLite extends AbstractOsmTileSource{
        private static final String PATTERN = "https://cartodb-basemaps-%s.global.ssl.fastly.net/dark_nolabels";
        private static final String[] SERVER = {"a", "b", "c"};
        private int serverNum = 0;

        /**
         * Constructs a new {@code "CARTODB"} tile source.
         */
        public DarkMatterLite(){
            super("Dark Matter Lite", PATTERN, "DARKMATTERLITE");
        }

        @Override
        public String getBaseUrl(){
            String url = String.format(this.baseUrl, new Object[] {SERVER[serverNum]});
            serverNum = (serverNum + 1) % SERVER.length;
            return url;
        }
    }
    
    // Antique
    public static class Antique extends AbstractOsmTileSource{
        private static final String PATTERN = "https://cartocdn_%s.global.ssl.fastly.net/base-antique";
        private static final String[] SERVER = {"a", "b", "c"};
        private int serverNum = 0;

        /**
         * Constructs a new {@code "Stamen"} tile source.
         */
        public Antique(){
            super("Antique", PATTERN, "ANTIQUE");
        }

        @Override
        public String getBaseUrl(){
            String url = String.format(this.baseUrl, new Object[] {SERVER[serverNum]});
            serverNum = (serverNum + 1) % SERVER.length;
            return url;
        }
    }
    //Water Color
    public static class WaterColor extends AbstractOsmTileSource{
        private static final String PATTERN = "http://tile.stamen.com/watercolor";
        private static final String[] SERVER = {"a", "b", "c"};
        private int serverNum = 0;

        /**
         * Constructs a new {@code "Stamen"} tile source.
         */
        public WaterColor(){
            super("Water Color", PATTERN, "WATERCOLOR");
        }

        @Override
        public String getBaseUrl(){
            String url = String.format(this.baseUrl, new Object[] {SERVER[serverNum]});
            serverNum = (serverNum + 1) % SERVER.length;
            return url;
        }
    }
}
