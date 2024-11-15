package main.java.com.ardian.bouniversemanager.connection;

import java.nio.file.Paths;
import java.util.logging.Logger;

import com.crystaldecisions.sdk.exception.SDKException;
import com.crystaldecisions.sdk.framework.CrystalEnterprise;
import com.crystaldecisions.sdk.framework.IEnterpriseSession;
import com.crystaldecisions.sdk.framework.ISessionMgr;
import com.sap.sl.sdk.framework.SlContext;
import com.sap.sl.sdk.framework.cms.CmsSessionService;

import main.java.com.ardian.bouniversemanager.configuration.AppConfig;
import main.java.com.ardian.bouniversemanager.services.FileSystemCleaner;

public class BoConnectionManager {

    private SlContext context;
    private IEnterpriseSession enterpriseSession;
    private String username;
    private String password;
    private String server;
    private String authType;

    private static final Logger LOGGER = Logger.getLogger(BoConnectionManager.class.getName());

    public BoConnectionManager() {
        AppConfig config = AppConfig.getInstance();
        username = config.getProperty("bo.username");
        password = config.getProperty("bo.password");
        server = config.getProperty("bo.server");
        authType = config.getProperty("bo.auth_type");

        try {
            connect();
        }   catch (SDKException e) {
            LOGGER.severe("Error connecting to BO server: " + e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public void connect() throws SDKException {
        context = SlContext.create();
        ISessionMgr sessionMgr = CrystalEnterprise.getSessionMgr();
        enterpriseSession = sessionMgr.logon(this.username, password, server, authType);
        context.getService(CmsSessionService.class).setSession(enterpriseSession);
        LOGGER.info("Connected to BO server");
    }

    public IEnterpriseSession getSession() {
        return enterpriseSession;
    }

    public SlContext getContext() {
        return context;
    }

    public void disconnect() {
        if (context != null) {
            context.close();
            enterpriseSession.logoff();
        }

        FileSystemCleaner fileSystemCleaner = new FileSystemCleaner();
        fileSystemCleaner.deleteRetrievalFolders(Paths.get("universes"));
    }
}
