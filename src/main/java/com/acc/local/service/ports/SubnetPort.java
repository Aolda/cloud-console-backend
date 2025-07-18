package com.acc.local.service.ports;

import com.acc.dto.subnet.*;
import java.util.List;

public interface SubnetPort {
    SubnetResponse createSubnet(SubnetRequest request, String token);
    List<SubnetResponse> listSubnets(String token);
    SubnetResponse updateSubnet(String id, SubnetUpdateRequest request, String token);
    void deleteSubnet(String id, String token);
    List<SubnetResponse> searchSubnets(String keyword, String token);
}