package com.acc.server.openstack.adapter.out;

import com.acc.server.openstack.domain.model.Network;
import com.acc.server.openstack.domain.model.Subnet;
import com.acc.server.openstack.domain.model.Port;
import com.acc.server.openstack.domain.port.NeutronPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class NeutronRestAdapter implements NeutronPort {
    private final WebClient neutronWebClient;

    private <T> List<T> extractList(String field, JsonNode root, Function<JsonNode, T> mapper) {
        List<T> list = new ArrayList<>();
        root.get(field).forEach(node -> list.add(mapper.apply(node)));
        return list;
    }

    @Override
    public List<Network> getNetworks(String token) {
        return neutronWebClient.get()
                .uri("/v2.0/networks")
                .header("X-Auth-Token", token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(root -> extractList("networks", root, node ->
                        new Network(
                                node.get("id").asText(),
                                node.get("name").asText(),
                                node.get("status").asText()
                        )))
                .block();
    }

    @Override
    public List<Subnet> getSubnets(String token) {
        return neutronWebClient.get()
                .uri("/v2.0/subnets")
                .header("X-Auth-Token", token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(root -> extractList("subnets", root, node ->
                        new Subnet(
                                node.get("id").asText(),
                                node.get("name").asText(),
                                node.get("network_id").asText(),
                                node.get("cidr").asText()
                        )))
                .block();
    }

    @Override
    public List<Port> getPorts(String token) {
        return neutronWebClient.get()
                .uri("/v2.0/ports")
                .header("X-Auth-Token", token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(root -> extractList("ports", root, node ->
                        new Port(
                                node.get("id").asText(),
                                node.get("name").asText(),
                                node.get("network_id").asText(),
                                node.get("mac_address").asText()
                        )))
                .block();
    }
}
