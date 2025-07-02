package com.acc.server.openstack.domain.port;

import com.acc.server.openstack.domain.model.Network;
import com.acc.server.openstack.domain.model.Subnet;
import com.acc.server.openstack.domain.model.Port;

import java.util.List;

public interface NeutronPort {
    List<Network> getNetworks(String token);
    List<Subnet>  getSubnets(String token);
    List<Port>    getPorts(String token);
}
