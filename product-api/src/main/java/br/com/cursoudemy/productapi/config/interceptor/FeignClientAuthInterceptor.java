package br.com.cursoudemy.productapi.config.interceptor;

import br.com.cursoudemy.productapi.config.RequestUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
@Component
public class FeignClientAuthInterceptor implements RequestInterceptor {

    private static final String AUTHORIZATION = "Authorization";
    private static final String TRANSACTION_ID = "transactionid";

    @Override
    public void apply(RequestTemplate template) {
        var currentRequest = RequestUtil.getCurrentRequest();

        template.header(this.AUTHORIZATION, currentRequest.getHeader(this.AUTHORIZATION))
                .header(this.TRANSACTION_ID, currentRequest.getHeader(this.TRANSACTION_ID));
    }
}