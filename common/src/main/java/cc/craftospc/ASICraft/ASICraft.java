package cc.craftospc.ASICraft;

import cc.craftospc.ASICraft.util.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class ASICraft {
    public static final String MOD_ID = "asicraft";

    public static final Logger LOG = LogManager.getLogger(MOD_ID);
    
    public static void init() {
        Registry.register();

    }
}
