package xacml;

import com.sun.xacml.*;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.finder.AttributeFinder;
import com.sun.xacml.finder.AttributeFinderModule;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.impl.CurrentEnvModule;
import com.sun.xacml.finder.impl.FilePolicyModule;
import com.sun.xacml.finder.impl.SelectorModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class lets you evaluate requests against policies.
 */
public class PolicyDecisionPoint {
    // this is the actual PDP object we'll use for evaluation
    private PDP pdp = null;

    public PolicyDecisionPoint() throws ParsingException, UnknownIdentifierException {
        // load default config file
        File xacmlFile = new File("../support/config/config_rbac.xml");

        // load the configuration
        ConfigurationStore store = new ConfigurationStore(xacmlFile);

        // use the default factories from the configuration
        store.useDefaultFactories();

        // get the PDP configuration's and setup the PDP
        pdp = new PDP(store.getDefaultPDPConfig());
    }

    public PolicyDecisionPoint(String [] policyFiles) {
        // Create a PolicyFinderModule and initialize it
        FilePolicyModule filePolicyModule = new FilePolicyModule();
        for (String policyFile : policyFiles) filePolicyModule.addPolicy(policyFile);

        // Setup the PolicyFinder
        PolicyFinder policyFinder = new PolicyFinder();
        Set<FilePolicyModule> policyModules = new HashSet<>();
        policyModules.add(filePolicyModule);
        policyFinder.setModules(policyModules);

        // Setup AttributeFinderModules
        CurrentEnvModule envAttributeModule = new CurrentEnvModule();
        SelectorModule selectorAttributeModule = new SelectorModule();

        // Setup the AttributeFinder
        AttributeFinder attributeFinder = new AttributeFinder();
        List<AttributeFinderModule> attributeModules = new ArrayList<>();
        attributeModules.add(envAttributeModule);
        attributeModules.add(selectorAttributeModule);
        attributeFinder.setModules(attributeModules);

        // Initialize our pdp
        this.pdp = new PDP(new PDPConfig(attributeFinder, policyFinder, null));
    }

    public ResponseCtx evaluate(String requestFile) throws IOException, ParsingException {
        // setup the request based on the file
        RequestCtx request = RequestCtx.getInstance(new FileInputStream(requestFile));

        // evaluate the request
        return pdp.evaluate(request);
    }
}
