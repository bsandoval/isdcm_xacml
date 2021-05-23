package xacml;

import com.sun.xacml.Indenter;
import com.sun.xacml.ParsingException;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.ctx.ResponseCtx;

import java.io.IOException;

/**
 * This is a simple, command-line XACML PDP.
 */
public class Authorizer {

    public static void main(String[] args) throws ParsingException, IOException, UnknownIdentifierException {
        if (args.length < 2) {
            System.out.println("Usage: -config <request>");
            System.out.println("   or    <request> <policy> [policies]");
            System.out.println("Example: java -cp sunxacml.jar:isdcm_xacml.jar Authorizer ../support/requests/XACMLRequest1.xml ../support/policy/XACMLPolicy1.xml");
            System.exit(1);
        }

        PolicyDecisionPoint myPDP;
        String requestFile;

        if (args[0].equals("-config")) {
            requestFile = args[1];
            myPDP = new PolicyDecisionPoint();
        } else {
            requestFile = args[0];
            String [] policyFiles = new String[args.length - 1];

            System.arraycopy(args, 1, policyFiles, 0, args.length - 1);

            myPDP = new PolicyDecisionPoint(policyFiles);
        }

        // evaluate the request
        ResponseCtx response = myPDP.evaluate(requestFile);

        // for this sample program, we'll just print out the response
        response.encode(System.out, new Indenter());

    }
}
