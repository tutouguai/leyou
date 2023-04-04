package top.leyou.filters;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

@Component
public class RewriteRequestBodyGatewayFilterFactory extends AbstractGatewayFilterFactory<RewriteRequestBodyGatewayFilterFactory.Config> {
    public RewriteRequestBodyGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            HttpHeaders headers = exchange.getRequest().getHeaders();
            MediaType mediaType = headers.getContentType();
            if (mediaType != null && mediaType.equals(MediaType.MULTIPART_FORM_DATA)) {
                return chain.filter(exchange.mutate().request(new RewriteBodyServerHttpRequestDecorator(exchange.getRequest())).build());
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {
    }

    private static class RewriteBodyServerHttpRequestDecorator extends ServerHttpRequestDecorator {
        private final DataBufferFactory dataBufferFactory;
        private final ServerWebExchange exchange;
        private Flux<DataBuffer> cachedFlux;

        public RewriteBodyServerHttpRequestDecorator(ServerHttpRequest delegate) {
            super(delegate);
            this.dataBufferFactory = new DefaultDataBufferFactory();
            this.exchange = null;
        }

        public RewriteBodyServerHttpRequestDecorator(ServerHttpRequest delegate, ServerWebExchange exchange) {
            super(delegate);
            this.dataBufferFactory = new DefaultDataBufferFactory();
            this.exchange = exchange;
        }

        @Override
        public Flux<DataBuffer> getBody() {
            if (this.cachedFlux != null) {
                return this.cachedFlux;
            } else {
                Flux<DataBuffer> flux = super.getBody()
                        .map(buffer -> {
                            DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                            DataBuffer dataBuffer = dataBufferFactory.wrap(buffer.asByteBuffer());
                            if (dataBuffer.readableByteCount() > 0) {
                                return this.dataBufferFactory.wrap(dataBuffer.asByteBuffer());
                            }
                            dataBufferFactory = null;
                            return this.dataBufferFactory.wrap(new byte[0]);
                        });

                this.cachedFlux = flux.cache();
                return this.cachedFlux;
            }
        }
    }
}
