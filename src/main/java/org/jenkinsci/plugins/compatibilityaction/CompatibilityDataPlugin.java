package org.jenkinsci.plugins.compatibilityaction;

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
 * and a new {@link CompatibilityDataPlugin} is created. The created
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
public class CompatibilityDataPlugin extends GlobalConfiguration {

    private CompatibilityDataProvider provider;
    public static final String LOG_PREFIX = "[COMPATIBILITY]";
    
    public CompatibilityDataPlugin() {
        load();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
        req.bindJSON(this, json);
        save();
        return true;
    }
   
    public CompatibilityDataProvider getProvider() {
        return provider;
    }
    
    public <T extends CompatibilityDataProvider> T getProvider(Class<T> t) {
        return t.cast(provider);
    } 

    public void setProvider(CompatibilityDataProvider provider) {
        this.provider = provider;
    }

    public ExtensionList<CompatibilityDataProvider.CompatabilityDataProviderDescriptor> getAllProviders() {
        return CompatibilityDataProvider.CompatabilityDataProviderDescriptor.all();
    }
}

