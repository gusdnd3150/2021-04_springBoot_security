package com.module.system.websocket;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Component
public class WebSocketConfig extends javax.websocket.server.ServerEndpointConfig.Configurator implements ApplicationContextAware {
	
	private static volatile BeanFactory context;

	@Bean
    public ServerEndpointExporter serverEndpointExporter() {
        /*
            2022.10.26[프뚜]:
            Spring에서 Bean은 싱글톤으로 관리되지만,
            @ServerEndpoint 클래스는 WebSocket이 생성될 때마다 인스턴스가 생성되고
            JWA에 의해 관리되기 때문에 Spring의 @Autowired가 설정된 멤버들이 초기화 되지 않습니다.
            연결해주고 초기화해주는 클래스가 필요합니다.
         */
        return new ServerEndpointExporter();
    }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		// TODO Auto-generated method stub
		WebSocketConfig.context = applicationContext;
		
	}
	
	@Override
	public <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
		// TODO Auto-generated method stub
		//return super.getEndpointInstance(clazz);
		return context.getBean(clazz);
	}
}
