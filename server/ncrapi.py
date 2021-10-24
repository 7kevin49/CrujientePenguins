import base64
import hashlib
import hmac
from dataclasses import dataclass
from datetime import datetime, timezone
from typing import ClassVar
from urllib.parse import urlsplit

import requests

from config import config


def create_hmac(request: requests.PreparedRequest):
    """
    Specification from
    https://developer.ncrcloud.com/portals/dev-portal/help-center/documentation/131#access-key-authentication
    """
    parameters = [request.method, urlsplit(request.url).path]
    headers = [
        "content-type",
        "content-md5",
        "nep-application-key",
        "nep-correlation-id",
        "nep-organization",
        "nep-service-version"
    ]

    for header in headers:
        if header in request.headers.keys():
            parameters.append(request.headers[header])

    to_sign = "\n".join(parameters)

    unique_key = bytes(
        config['ncrapi']['bsp-secret-key'] +
        datetime.strptime(request.headers['date'], '%a, %d %b %Y %H:%M:%S %Z').strftime('%Y-%m-%dT%H:%M:%S') + '.000Z',
        'utf-8')

    digest = hmac.new(unique_key, bytes(to_sign, 'utf-8'), hashlib.sha512).digest()
    signature = base64.b64encode(digest)
    auth_token = f"AccessKey {config['ncrapi']['bsp-shared-key']}:{signature.decode('ascii')}"
    return auth_token


@dataclass
class NCRApi:
    nep_organization: str  # bsp-organization
    nep_enterprise_unit: str  # bsp-site-id
    content_type: str = "application/json"

    gateway: ClassVar[str] = "https://gateway-staging.ncrcloud.com"
    site_service: ClassVar[str] = "/site"
    security_service: ClassVar[str] = "/security"
    order_service: ClassVar[str] = "/order/3/orders/1"
    tdm_service: ClassVar[str] = "/transaction-document/transaction-documents"
    cdm_service: ClassVar[str] = "/cdm"
    catalog_items_service: ClassVar[str] = "/catalog/v2"
    image_service: ClassVar[str] = "/image/v1/images"

    def api_call(self, method: str, endpoint: str, data=None) -> requests.Response:
        if data is None:
            data = dict()
        headers = {
            "date": datetime.now(timezone.utc).strftime("%a, %d %b %Y %H:%M:%S %Z"),
            "accept": "application/json",
            "content-type": self.content_type,
            "nep-organization": self.nep_organization,
            "nep-enterprise-unit": self.nep_enterprise_unit
        }

        request = requests.Request(method=method, url=f"{self.gateway}{endpoint}", headers=headers, data=data).prepare()

        session = requests.Session()
        request.headers["Authorization"] = create_hmac(request)

        return session.send(request)
