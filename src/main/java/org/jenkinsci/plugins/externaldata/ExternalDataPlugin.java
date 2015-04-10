package org.jenkinsci.plugins.externaldata;

import hudson.Extension;
import hudson.ExtensionList;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Sample {@link Builder}.
 *
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link ExternalDataPlugin} is created. The created
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
@Extension
public class ExternalDataPlugin extends GlobalConfiguration {

    private ExternalDataProvider provider;
    public static final String LOG_PREFIX = "[NOSQL]";
    
    public ExternalDataPlugin() {
        load();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        save();
        return true;
    }
   
    public ExternalDataProvider getProvider() {
        return provider;
    }
    
    public <T extends ExternalDataProvider> T getProvider(Class<T> t) {
        return t.cast(provider);
    } 

    public void setProvider(ExternalDataProvider provider) {
        this.provider = provider;
    }

    public ExtensionList<ExternalDataProvider.NoSQLDescriptor> getAllProviders() {
        return ExternalDataProvider.NoSQLDescriptor.all();
    }
}

