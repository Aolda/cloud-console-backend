package com.acc.local.service.modules.router;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RouterModule {

    @Value("${openstack.neutron.endpoint}")
    private String neutronEndpoint;

    private final WebClient webClient;

    public String getRouterName(String routerId, String token) {
        try {
            Map<String, Object> response = webClient.get()
                .uri(neutronEndpoint + "/v2.0/routers")
                .header("X-Auth-Token", token)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            List<Map<String, Object>> routers = (List<Map<String, Object>>)
                    ((Map<String, Object>) response).get("routers");

            for (Map<String, Object> router : routers) {
                if (router.get("id").equals(routerId)) {
                    return (String) router.get("name");
                }
            }
        } catch (Exception e) {
            return "(unknown)";
        }
        return "(not found)";
    }
}

// STEP 3-5: 연결 대상 이름 조회 기능 추가
// 목적: 포트에 연결된 인스턴스/라우터 이름을 PortResponse 에 포함시킴
// 방법:
// - device_owner 가 "compute:nova"인 경우 Nova의 서버 목록에서 ID 매칭하여 인스턴스 이름 조회
// - device_owner 가 "network:router_interface" 등인 경우 Neutron 라우터 목록에서 매칭

// PortAdapter 내부 로직 예시
// for (PortResponse port : ports) {
//   if (port.getDeviceOwner().startsWith("compute")) {
//     port.setDeviceName(novaModule.getInstanceName(port.getDeviceId(), token));
//   } else if (port.getDeviceOwner().contains("router")) {
//     port.setDeviceName(routerModule.getRouterName(port.getDeviceId(), token));
//   }
// }
// 이처럼 각 포트 응답에 연결 대상 이름을 포함시켜 반환함
