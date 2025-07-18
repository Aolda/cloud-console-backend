package com.acc.local.service.adapters.subnet;

import com.acc.dto.subnet.*;
import com.acc.local.service.modules.subnet.SubnetModule;
import com.acc.local.service.ports.SubnetPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
@RequiredArgsConstructor
public class SubnetAdapter implements SubnetPort {

    private final SubnetModule subnetModule;

    @Override
    public SubnetResponse createSubnet(SubnetRequest request, String token) {
        return subnetModule.createSubnet(request, token);
    }

    @Override
    public List<SubnetResponse> listSubnets(String token) {
        return subnetModule.listSubnets(token);
    }

    @Override
    public SubnetResponse updateSubnet(String id, SubnetUpdateRequest request, String token) {
        return subnetModule.updateSubnet(id, request, token);
    }

    @Override
    public void deleteSubnet(String id, String token) {
        subnetModule.deleteSubnet(id, token);
    }

    @Override
    public List<SubnetResponse> searchSubnets(String keyword, String token) {
        return subnetModule.listSubnets(token).stream()
                .filter(subnet -> subnet.name() != null && subnet.name().contains(keyword))
                .toList();
    }
}
