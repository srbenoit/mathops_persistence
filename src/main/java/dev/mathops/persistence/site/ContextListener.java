package dev.mathops.persistence.site;

import dev.mathops.commons.installation.Installation;
import dev.mathops.commons.installation.Installations;
import dev.mathops.commons.log.Log;
import dev.mathops.commons.log.LoggingSubsystem;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

import java.io.File;

/**
 * A listener to do installation configuration for the front controller context (rather than within the servlet
 * itself).
 */
public final class ContextListener implements ServletContextListener { // Public so Tomcat can see it...

    /** The installation. */
    private Installation installation = null;

    /**
     * Constructs a new {@code ContextListener}.
     */
    public ContextListener() { // Public so Tomcat can see it...

        // No action
    }

    /**
     * Called when the context is initialized.
     *
     * @param sce the servlet context event
     */
    @Override
    public void contextInitialized(final ServletContextEvent sce) {

        final ServletContext ctx = sce.getServletContext();

        // Gather enough information to configure logging before logging anything
        final String baseDir = ctx.getInitParameter("mathops-base-dir");
        final String cfgFile = ctx.getInitParameter("mathops-cfg-file");

        final File baseFile = baseDir == null ? null : new File(baseDir);
        this.installation = Installations.get().getInstallation(baseFile, cfgFile);
        LoggingSubsystem.setInstallation(this.installation);

        final String serverInfo = ctx.getServerInfo();
        Log.info("MathOps Servlet context initializing within ", serverInfo);

        Log.config("  Installation base dir: ", baseDir);
        Log.config("  Installation cfg file: ", cfgFile);

        ctx.setAttribute("Installation", this.installation);

        Log.info("MathOps Servlet context initialized");
    }

    /**
     * Called when the context is destroyed.
     *
     * @param sce the servlet context event
     */
    @Override
    public void contextDestroyed(final ServletContextEvent sce) {

        Log.info("MathOps Servlet context destroyed");
        this.installation = null;
    }
}
