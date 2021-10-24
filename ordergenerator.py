from typing import List
from random import choice, random

from config import config
from ncrapi import NCRApi


ncr_api: NCRApi = NCRApi(config["ncrapi"]["bsp-organization"], config["ncrapi"]["bsp-site-id"])


def create_faux_orders(n: int) -> List[str]:
    ids = []
    
    data = {
        "customer": {
            "email": "gburdell@gatech.edu",
            "name": "George P. Burdell"
        },
        "totals": []
    }
    for _ in range(n):
        copy_data = data.copy()
        copy_data["totals"].append({
            "type": choice(["Net", "TaxExcluded", "TaxIncluded"]),
            "value": round(random() * 100, 2)
        })
        order_view = ncr_api.api_call("POST", f"{ncr_api.order_service}", data=copy_data).json()
        print(f"Created order {order_view}")
        ids.append(order_view["id"])
    
    return ids
        

def main():
    order_ids = create_faux_orders(10)
    with open("..\\generated-orders.txt", "a") as file:
        for idx in order_ids:
            file.write(f"{idx}\n")


if __name__ == '__main__':
    # RUN THIS WITH THE WORKING DIRECTORY IN /server
    main()
