package labs567;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class TlsConfigurator extends HttpsConfigurator {

    /**
     * Creates a Https configuration, with the given {@link SSLContext}.
     *
     * @param context the {@code SSLContext} to use for this configurator
     * @throws NullPointerException if no {@code SSLContext} supplied
     */
    public TlsConfigurator(SSLContext context) {
        super(context);
    }

    @Override
    public void configure(HttpsParameters params) {
        SSLContext sslContext = getSSLContext();
        SSLParameters sslParams = sslContext.getSupportedSSLParameters();
        params.setSSLParameters(sslParams);
        params.setNeedClientAuth(false);

        SSLEngine engine = sslContext.createSSLEngine();
        params.setCipherSuites(engine.getEnabledCipherSuites());
        params.setProtocols(engine.getEnabledProtocols());


    }
}
