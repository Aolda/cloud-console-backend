# External OpenStack DTO 정리

이 문서는 external 계층에서 OpenStack 서비스(Keystone/Nova/Neutron/Cinder/Glance)와 연동할 때 사용하는 DTO의 전송 구조와 필드를 요약합니다. 실제 전송 JSON의 컨테이너 키, 중첩 구조, 확장 네임스페이스 키(OS-DCF, provider, binding 등)를 그대로 반영했습니다.

## Keystone
- CreateKeystoneProjectRequest
  - projectName(String), projectDescription(String)
- KeystoneProject (조회/매핑용)
  - id(String), name(String), description(String), domainId(String), parentId(String), enabled(Boolean), isDomain(Boolean), tags(List<String>)

## Nova
- CreateServerRequest
  - 루트: `server(Server)`
  - Server
    - name, imageRef, flavorRef, adminPass, key_name
    - security_groups(List<SecurityGroup>) → SecurityGroup.name
    - networks(List<Network>) → Network.uuid, port, fixed_ip, tag
    - metadata(Map<String,String)), user_data, availability_zone, config_drive(Boolean)
    - `OS-DCF:diskConfig`(String), accessIPv4, accessIPv6, hostname, tags
    - hypervisor_hostname, minCount(Integer), maxCount(Integer)
    - block_device_mapping_v2(List<BlockDeviceMappingV2>) → 부트 볼륨/디스크 매핑

- 서버 액션(대표)
  - StartServerRequest → 루트 키 `os-start: null`
  - RebuildServerRequest.rebuild → imageRef, name, adminPass, metadata, `OS-DCF:diskConfig`
  - ResizeServerRequest.resize → flavorRef, `OS-DCF:diskConfig`
  - CreateImageRequest.createImage → name, metadata
  - Pause/Unpause/Stop/Resume/Suspend/Lock/Unlock 등: 각 액션별 루트 키 사용

- CreateConsoleRequest
  - remote_console → type, protocol

- AttachVolumeRequest
  - volumeAttachment(List<VolumeAttachment>) → volumeId, device, delete_on_termination(Boolean)

- CreateKeyPairRequest
  - keypair → name, type

- CreateFlavorRequest
  - flavor → name, ram, vcpus, disk, id(null 허용), `os-flavor-access:is_public`(Boolean), description

- UpdateNovaQuotaRequest
  - 프로젝트 쿼터 업데이트(필드는 해당 파일 참조)

## Neutron
- CreateNetworkRequest
  - 루트: `network(Network)`
  - Network
    - name, description, mtu(Integer), admin_state_up(Boolean), shared(Boolean)
    - tenant_id, project_id
    - `provider:network_type`, `provider:physical_network`, `provider:segmentation_id`(Integer)
    - segments(List<Segment>) → `provider:network_type/physical_network/segmentation_id`
    - port_security_enabled(Boolean), qos_policy_id, vlan_transparent(Boolean), is_default(Boolean)
    - availability_zone_hints(List<String>), availability_zones(List<String>), tags(List<String>)

- CreateSubnetRequest
  - 루트: `subnet(Subnet)`
  - Subnet
    - network_id, name, description, ip_version(Integer), cidr, gateway_ip
    - allocation_pools(List<AllocationPool>) → start, end
    - dns_nameservers(List<String>), host_routes(List<HostRoute>) → destination, nexthop
    - enable_dhcp(Boolean), project_id, tags(List<String>)

- CreatePortRequest
  - 루트: `port(Port)`
  - Port
    - network_id, name, description, admin_state_up(Boolean), mac_address
    - fixed_ips(List<FixedIp>) → subnet_id, ip_address
    - project_id, device_id, device_owner, qos_policy_id
    - security_groups(List<String>)
    - allowed_address_pairs(List<AllowedAddressPair>) → ip_address, mac_address
    - extra_dhcp_opts(List<ExtraDhcpOpt>) → opt_name, opt_value, ip_version(Integer)
    - `binding:vnic_type`, `binding:host_id`, dns_name, tags(List<String>), port_security_enabled(Boolean)

- CreateRouterRequest
  - 루트: `router(Router)`
  - Router
    - name, description, admin_state_up(Boolean), project_id
    - distributed(Boolean), ha(Boolean), availability_zone_hints(List<String>)
    - external_gateway_info(ExternalGatewayInfo), tags(List<String>)

- CreateFloatingIpRequest
  - 루트: `floatingip(FloatingIp)`
  - FloatingIp
    - floating_network_id, port_id, fixed_ip_address, floating_ip_address, subnet_id
    - description, project_id, tags(List<String>)

- CreateSecurityGroupRequest
  - 루트: `security_group(SecurityGroup)`
  - SecurityGroup → name, description, project_id, stateful(Boolean), tags(List<String>)

- CreateSecurityGroupRuleRequest
  - 루트: `security_group_rule(SecurityGroupRule)`
  - SecurityGroupRule
    - security_group_id, direction(ingress|egress), protocol
    - port_range_min, port_range_max, ethertype(IPv4|IPv6)
    - remote_ip_prefix(CIDR), remote_group_id, description, project_id

- CreatePortForwardingRequest
  - 루트: `port_forwarding(PortForwarding)`
  - PortForwarding
    - internal_port_id, internal_ip_address, internal_port(Integer)
    - external_port(Integer), protocol

## Cinder
- CreateVolumeRequest
  - 루트: `volume(Volume)`, `OS-SCH-HNT:scheduler_hints(SchedulerHints)`
  - Volume
    - size(Integer GiB), availability_zone, source_volid, description, multiattach(Boolean)
    - snapshot_id, backup_id, name, imageRef, volume_type
    - metadata(Map<String,String>), consistencygroup_id
  - SchedulerHints
    - same_host(List<String>)

- UpdateVolumeRequest
  - 루트: `volume` → name, description, metadata(Map<String,String>)

- CreateSnapshotRequest
  - 루트: `snapshot(Snapshot)`
  - Snapshot → name, description(Optional), volume_id, force(Optional), metadata(Optional)

- CreateBackupRequest
  - 루트: `backup(Backup)`
  - Backup
    - volume_id, container(Optional), description(Optional), incremental(Optional), force(Optional)
    - name(Optional), snapshot_id(Optional), metadata(Optional), availability_zone(Optional)

## Glance
- CreateImageRequest
  - container_format, disk_format, id, min_disk(Integer), min_ram(Integer)
  - name, protected(Boolean deleteProtected), tags(List<String>), visibility
  - additionalProperties(Map<String,String>) with JsonAnySetter/Getter (임의 확장 키 허용)

- UpdateImageRequest (패치 배열)
  - Operation 요소: op(add|replace|remove), path, value(Object), from

- StageImageRequest
  - x-openstack-image-size(String), data(byte[])

- UploadImageFileRequest
  - X-Image-Meta-Store(String), x-openstack-image-size(String), data(byte[])

- ImportImageRequest
  - method(Method) → name, uri, glance_image_id, glance_region, glance_service_interface
  - all_stores(Boolean), all_stores_must_succeed(Boolean), stores(List<String>)

- FetchImagesRequestParam (조회 쿼리 파라미터)
  - limit, marker, name, visibility, status, owner, protected(deleteProtected)
  - tags(List), container_format, disk_format, id
  - size_min, size_max, created_at, updated_at
  - sort_key, sort_dir, sort
  - toQueryParams() → Map 변환

## 공통 메모
- 대부분 루트에 리소스 컨테이너 키(`server`, `network`, `subnet`, `port`, `router`, `security_group`, `snapshot` 등)를 둡니다.
- OpenStack 확장 네임스페이스 키 사용: `OS-DCF:*`, `provider:*`, `binding:*`, `OS-SCH-HNT:*`, `os-flavor-access:*` 등.
- `@JsonInclude(NON_NULL)`로 null 필드는 직렬화 제외되어, 선택 속성은 생략 시 API에 전송되지 않습니다.

## 서버 DTO ↔ OpenStack DTO 매핑

아래는 우리 서버 측 DTO(`src/main/java/com/acc/local/dto`)와 OpenStack external DTO(`src/main/java/com/acc/local/external/dto`)가 개념적으로 어떻게 매핑되는지에 대한 가이드입니다.

### Compute / Instance
- 인스턴스 생성
  - 우리: `InstanceCreateRequest`
    - `instanceName` → Nova `CreateServerRequest.server.name`
    - `typeId` → Nova `CreateServerRequest.server.flavorRef`
    - `imageId` → Nova `CreateServerRequest.server.imageRef`
    - `networkIds` → Nova `CreateServerRequest.server.networks[].uuid`
    - `interfaceIds` → Nova `CreateServerRequest.server.networks[].port`
    - `securityGroupIds` → Nova `CreateServerRequest.server.security_groups[].name` 또는 Port 보안그룹으로 설정
    - `diskSize`(>0) → Nova `block_device_mapping_v2[].volume_size` 등(부트볼륨 구성)
    - `password` → Nova `CreateServerRequest.server.adminPass`
    - `keypairId` → Nova `CreateServerRequest.server.key_name`
  - 주의: Nova는 `security_groups`에 “이름”을 기대합니다. 서버 DTO가 ID를 제공하면 서비스 계층에서 이름 해석 또는 Port 보안그룹으로 위임 필요.

- 인스턴스 액션
  - 우리: `InstanceActionRequest`
    - `action` → Nova 해당 액션 DTO 루트 키 매핑(예: `os-start`, `reboot`, `resize`, `rebuild`, `createImage`, `shelve` 등)
    - `rebootType` → Nova Reboot 요청의 타입
    - `flavorRef`/`diskConfig` → Nova `ResizeServerRequest.resize.*`
    - `imageName`/`metadata` → Nova `CreateImageRequest.createImage.*`
    - `imageRef`/`adminPassword`/`diskConfig` → Nova `RebuildServerRequest.rebuild.*`
    - 기타 필드(`lockedReason`, `rescueAdminPass`, `availabilityZone`, `host` 등)는 각 Nova 액션 DTO의 동명 필드에 매핑

- 쿼터 조회
  - 우리: `InstanceQuotaResponse`
    - `instance/core/ram/keypair` → Nova/Cinder/Keystone 쿼터 합성 결과를 서비스 계층에서 변환하여 채움

### Network
- 네트워크 생성(+서브넷)
  - 우리: `network.CreateNetworkRequest`
    - `networkName` → Neutron `CreateNetworkRequest.network.name`
    - `description` → `network.description`
    - `mtu` → `network.mtu`
    - `subnets[].cidr`/`subnetName` → Neutron `CreateSubnetRequest.subnet.cidr`/`name`
    - (프로젝트/공유/보안 등 추가 속성은 서비스 정책에 따라 `project_id`, `port_security_enabled` 등 채움)

- 라우터 생성
  - 우리: `network.CreateRouterRequest`
    - `routerName` → Neutron `CreateRouterRequest.router.name`
    - `description` → `router.description`
    - `isExternal` → `router.external_gateway_info` 존재 여부 및 외부 네트워크 지정으로 해석

- 인터페이스(포트) 생성
  - 우리: `network.CreateInterfaceRequest`
    - `interfaceName` → Neutron `CreatePortRequest.port.name`
    - `description` → `port.description`
    - `networkId` → `port.network_id`
    - `subnetId` → `port.fixed_ips[].subnet_id`
    - `securityGroupIds` → `port.security_groups[]`
    - `isExternal` → 외부 연결 정책에 따라 라우터 게이트웨이/플로팅IP 할당 등으로 처리(포트 자체의 외부/내부 여부와는 별개)

- 보안 그룹/규칙
  - 우리: `network.CreateSecurityGroupRequest`
    - `securityGroupName`/`description` → Neutron `CreateSecurityGroupRequest.security_group.name/description`
  - 우리: `network.CreateSecurityRuleRequest`
    - `protocol`/`direction`/`port`/`cidr`/`remoteSecurityGroupId` → Neutron `CreateSecurityGroupRuleRequest.security_group_rule.protocol/direction/port_range_min,max/remote_ip_prefix/remote_group_id`

### Volume / Snapshot
- 스냅샷 생성
  - 우리: `volume.snapshot.VolumeSnapshotRequest`
    - `sourceVolumeId` → Cinder `CreateSnapshotRequest.snapshot.volume_id`
    - `name` → `snapshot.name`
- (참고) 인스턴스 부트 볼륨
  - 우리: `InstanceCreateRequest.diskSize`와 `imageId`를 기반으로 Nova `block_device_mapping_v2` 구성(소스 image → 대상 volume, `volume_size` 지정, `delete_on_termination` 정책 등)

### Image
- 이미지 생성
  - 우리: `image.CreateImageRequestDto`
    - `name/diskFormat/containerFormat/visibility/minDisk/minRam/isProtected` → Glance `CreateImageRequest` 동명/동의 필드
- 이미지 업로드/스테이징/임포트
  - 우리: 서비스 흐름에서 파일 업로드는 Glance `StageImageRequest`/`UploadImageFileRequest`/`ImportImageRequest` 조합으로 변환

### Keypair
- 키페어 생성
  - 우리: `keypair.CreateKeypairRequest.keypairName` → Nova `CreateKeyPairRequest.keypair.name`

### Project
- 프로젝트 생성 요청/생성
  - 우리: `project.CreateProjectRequestRequest`/`CreateProjectRequest`
    - `projectName/projectDescription` → Keystone `CreateKeystoneProjectRequest`
    - `quota` → Nova/Cinder/Neutron 쿼터 업데이트 API로 분배 적용(서비스 계층에서 각 자원별 쿼터 DTO로 변환)
    - `projectOwnerId` → Keystone 역할/멤버십 할당 흐름으로 매핑

### 일반 매핑 규칙
- ID ↔ Name 변환: Nova `security_groups`는 이름 기준, Neutron 포트는 ID 기준 등 서비스별 차이가 있으므로 어댑터에서 변환.
- 선택/기본값: 서버 DTO에서 비필수 필드는 null 가능. external DTO는 `@JsonInclude(NON_NULL)`로 누락 필드를 자동 제외.
- 다중 호출 시퀀스: 한 서버 DTO가 여러 OpenStack API 호출(예: 네트워크 생성 → 서브넷 생성 → 라우터/게이트웨이 연결)로 분해될 수 있음.
