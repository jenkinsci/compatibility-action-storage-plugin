package org.jenkinsci.plugins.nosqldb;

import hudson.ExtensionList;
import hudson.Plugin;
import hudson.model.Descriptor;
import java.io.IOException;
import javax.servlet.ServletException;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link NoSQLDatabasePlugin} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #name})
 * to remember the configuration.
 *
 * <p>
 * When a build is performed, the {@link #perform(AbstractBuild, Launcher, BuildListener)}
 * method will be invoked. 
 *
 * @author Kohsuke Kawaguchi
 */
public class NoSQLDatabasePlugin extends Plugin {

    private NoSQLProvider provider;
            
    @Override
    public void configure(StaplerRequest req, JSONObject formData) throws IOException, ServletException, Descriptor.FormException {
        super.configure(req, formData); //To change body of generated methods, choose Tools | Templates.        
        provider = req.bindJSON(NoSQLProvider.class, formData.getJSONObject("provider"));
        save();
    }

    @Override
    public void start() throws Exception {
        super.start(); //To change body of generated methods, choose Tools | Templates.
        load();
    }
    public NoSQLProvider getProvider() {
        return provider;
    }

    public void setProvider(NoSQLProvider provider) {
        this.provider = provider;
    }

    public ExtensionList<NoSQLProvider.NoSQLDescriptor> getAllProviders() {
        return NoSQLProvider.NoSQLDescriptor.all();
    }
}

