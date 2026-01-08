#!/usr/bin/env python3
"""
Auto Endpoint Caller
모든 Controller의 GET 엔드포인트를 자동으로 호출하여 응답을 dump하는 스크립트
"""
import requests
import time
import json
from typing import Dict, List, Optional
import sys
import argparse

class EndpointCaller:
    def __init__(self, base_url: str, auth_token: Optional[str] = None):
        self.base_url = base_url.rstrip('/')
        self.session = requests.Session()
        if auth_token:
            self.session.headers['Authorization'] = f'Bearer {auth_token}'

    def call_endpoint(self, method: str, path: str, data: Optional[Dict] = None):
        """단일 엔드포인트 호출"""
        url = f"{self.base_url}{path}"
        try:
            print(f"Calling {method} {url}")

            if method == 'GET':
                response = self.session.get(url, timeout=30)
            elif method == 'POST':
                response = self.session.post(url, json=data, timeout=30)
            else:
                print(f"Unsupported method: {method}")
                return

            print(f"  -> Status: {response.status_code}")

            # 성공적인 응답인 경우 응답 내용 요약 출력
            if 200 <= response.status_code < 400:
                try:
                    json_response = response.json()
                    if isinstance(json_response, dict):
                        keys = list(json_response.keys())[:3]  # 처음 3개 키만
                        print(f"  -> Response keys: {keys}")
                    elif isinstance(json_response, list) and json_response:
                        print(f"  -> Response: List with {len(json_response)} items")
                except:
                    print(f"  -> Response: Non-JSON content")

            time.sleep(0.5)  # API 부하 방지

        except Exception as e:
            print(f"  -> Error: {e}")

    def run_all_endpoints(self, sample_data: Dict):
        """모든 엔드포인트 호출"""

        # OpenStack 관련 엔드포인트들
        endpoints = [
            # Auth & Users
            ('GET', '/api/auth/me'),
            ('GET', '/api/users'),
            ('GET', '/api/roles'),

            # Projects
            ('GET', '/api/projects'),
            ('GET', '/api/admin/projects'),

            # Compute
            ('GET', '/api/instances'),
            ('GET', '/api/instance-types'),
            ('GET', '/api/images'),
            ('GET', '/api/keypairs'),

            # Networking
            ('GET', '/api/networks'),
            ('GET', '/api/interfaces'),
            ('GET', '/api/routers'),
            ('GET', '/api/security-groups'),
            ('GET', '/api/security-rules'),

            # Storage
            ('GET', '/api/volumes'),
            ('GET', '/api/volume-snapshots'),
            ('GET', '/api/snapshot-policies'),

            # Others
            ('GET', '/api/notices'),
            ('GET', '/api/quick-start'),
        ]

        print(f"Starting auto endpoint calling...")
        print(f"Base URL: {self.base_url}")
        print(f"Total endpoints: {len(endpoints)}")
        print("-" * 50)

        for method, path in endpoints:
            self.call_endpoint(method, path)

        print("-" * 50)
        print("Auto endpoint calling completed!")
        print("Check response-dumps/ directory for dumped responses.")

def get_auth_token(base_url: str, username: str, password: str) -> Optional[str]:
    """인증 토큰 획득"""
    try:
        url = f"{base_url}/api/auth/login"
        data = {
            "username": username,
            "password": password
        }

        response = requests.post(url, json=data, timeout=30)
        if response.status_code == 200:
            result = response.json()
            return result.get('token')
        else:
            print(f"Login failed: {response.status_code} - {response.text}")
            return None

    except Exception as e:
        print(f"Login error: {e}")
        return None

def main():
    parser = argparse.ArgumentParser(description='Auto endpoint caller for DTO dumping')
    parser.add_argument('--base-url', default='http://localhost:8080',
                       help='Base URL of the application')
    parser.add_argument('--username', help='Username for authentication')
    parser.add_argument('--password', help='Password for authentication')
    parser.add_argument('--token', help='Auth token (if you already have one)')
    parser.add_argument('--no-auth', action='store_true',
                       help='Skip authentication (for public endpoints)')

    args = parser.parse_args()

    auth_token = None

    if not args.no_auth:
        if args.token:
            auth_token = args.token
        elif args.username and args.password:
            print("Logging in...")
            auth_token = get_auth_token(args.base_url, args.username, args.password)
            if not auth_token:
                print("Failed to get auth token. Exiting.")
                sys.exit(1)
            print(f"Got auth token: {auth_token[:20]}...")
        else:
            print("Please provide either --token or --username and --password")
            sys.exit(1)

    # 샘플 데이터 (POST 요청용)
    sample_data = {
        "project_id": "sample-project-id",
        "instance_id": "sample-instance-id",
        "volume_id": "sample-volume-id",
        "network_id": "sample-network-id",
    }

    caller = EndpointCaller(args.base_url, auth_token)
    caller.run_all_endpoints(sample_data)

if __name__ == "__main__":
    main()