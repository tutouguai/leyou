package top.leyou.config;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
 

@Configuration
public class WebCorsConfiguration {
 
    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
 
        //配置跨域
        corsConfiguration.addAllowedHeader("*");     //允许任意请求头跨域
        corsConfiguration.addAllowedMethod("OPTIONS");     //允许任何请求方式跨域
        corsConfiguration.addAllowedMethod("HEAD");     //允许任何请求方式跨域
        corsConfiguration.addAllowedMethod("GET");     //允许任何请求方式跨域
        corsConfiguration.addAllowedMethod("PUT");     //允许任何请求方式跨域
        corsConfiguration.addAllowedMethod("POST");     //允许任何请求方式跨域
        corsConfiguration.addAllowedMethod("DELETE");     //允许任何请求方式跨域
        corsConfiguration.addAllowedMethod("PATCH");     //允许任何请求方式跨域
        corsConfiguration.addAllowedOrigin("http://manage.leyou.com");     //允许任意请求来源跨域
        corsConfiguration.addAllowedOrigin("http://www.leyou.com");     //允许任意请求来源跨域
        corsConfiguration.setAllowCredentials(true); //允许携带cookie信息跨域
        corsConfiguration.setMaxAge(3600L);          //准备响应前的缓存持续的最大时间（以秒为单位）
 
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsWebFilter(urlBasedCorsConfigurationSource);
    }
}